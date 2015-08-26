var essApp = angular.module('ess');

essApp.controller('RecordManageCtrl', ['$scope', '$q', 'appProps', 'RecordUtils', 'modals',
    'SupervisorTimeRecordsApi', 'EmpInfoApi', 'TimeRecordsApi',
    recordManageCtrl]);

function recordManageCtrl($scope, $q, appProps, recordUtils, modals, supRecordsApi, empInfoApi, timeRecordsApi) {

    $scope.empInfos = {};

    var allSupervisorsId = 'all';

    function setDefaultValues() {
        $scope.selSupId = allSupervisorsId;

        $scope.supIds = [allSupervisorsId];

        $scope.supRecords = {};
    }

    $scope.selectedIndices = new Set();

    $scope.init = function () {
        getEmployeeRecords();
    };

    /** --- Api Methods --- */

    // The default status set for the supervisor record api currently matches this
    //var desiredStatuses =  // everything but APPROVED_PERSONNEL
    //    ['SUBMITTED', 'NOT_SUBMITTED', 'APPROVED', 'DISAPPROVED', 'SUBMITTED_PERSONNEL', 'DISAPPROVED_PERSONNEL'];

    /**
     * Retrieves all active, in progress records that are under supervision of the current user
     * Also gets employee infos for the supervisor, any overridden supervisor, and all employees in the returned records
     */
    function getEmployeeRecords() {
        $scope.loading = true;
        setDefaultValues();
        var empId = appProps.user.employeeId;
        console.log('getting employee records...');
        supRecordsApi.get({supId: empId}, function onSuccess(response) {
            var missingEmpIds = initializeRecords(response.result.items);
            $scope.selectNone();
            console.log('got records', $scope.supRecords);
            getEmployees(missingEmpIds);
        }, function onFail(response) {
            $scope.loading = false;
            console.log('get employee records failed:', response);
            // Todo handle failed response
        });
    }

    /**
     * Gets employee info for each of the given employee ids
     * Stores employee info in $scope.empInfos keyed by employee id
     * @param empIds - [] of employee ids
     */
    function getEmployees (empIds) {
        if (empIds.length > 0) {
            console.log('getting employees..');
            empInfoApi.get({empId: empIds}, function onSuccess(response) {
                for (var iEmp in response.employees) {
                    var employee = response.employees[iEmp];
                    $scope.empInfos[employee.employeeId] = employee;
                }
                console.log('got employees', $scope.empInfos);
                $scope.loading = false;
            }, function onFail(response) {
                $scope.loading = false;
                console.log('get employees failed:', response);
                // Todo handle failed response
            });
        } else {
            $scope.loading = false;
        }
    }

    /**
     * Submits each record of an array of records
     * Refreshes record data after each record has been submitted
     */
    function submitRecords (records) {
        var promises = [];
        records.forEach(function(record) {
            promises.push(timeRecordsApi.save(record,
                function(response){console.log('success:', record.timeRecordId, response)},
                function(response){console.log('fail:', record.timeRecordId, response)}).$promise);
        });
        $scope.loading = true;
        return $q.all(promises)
            .then(function onFulfilled() {
                console.log(records.length, 'records submitted');
                getEmployeeRecords();
            }, function onRejected(response) {
                $scope.loading = false;
                console.log('record submission failed:', response);
                // Todo handle failed response
            })
    }

    /** --- Display Methods --- */

    /**
     * Generates an option label for the given supervisor id
     * not the angular way, but you try fitting this into an ng-options
     */
    $scope.getOptionLabel = function(supId) {
        return (supId == allSupervisorsId ? 'All Supervisors' : $scope.empInfos[supId].fullName) +
            ' - (' + ($scope.supRecords[supId].SUBMITTED || []).length + ' Pending Records)';
    };

    /**
     * Returns true if there are multiple supervisors to approve records for,
     *  i.e. the current supervisor has been granted an override
     */
    $scope.multipleSups = function() {
        return Object.keys($scope.supRecords).length > 2;
    };

    /**
     * Causes all SUBMITTED records to be selected for review
     */
    $scope.selectAll = function() {
        for(var i = 0; i < $scope.supRecords[$scope.selSupId].SUBMITTED.length; i++) {
            $scope.selectedIndices.add(i);
        }
    };
    /**
     * Clears all SUBMITTED currently selected for review
     */
    $scope.selectNone = function() {
        $scope.selectedIndices.clear();
    };

    /**
     * Open a record detail modal using the given record as data
     * @param record
     */
    $scope.showDetails = function(record) {
        var params = { record: record };
        modals.open('record-details', params);
    };

    /**
     * Opens a record review modal in which the supervisor will view record details and approve/reject records accordingly
     * Passes all selected records
     * Upon resolution, the modal will return an object containing
     */
    $scope.review = function() {
        var selectedRecords = [];
        $scope.selectedIndices.forEach(function(index) {
            selectedRecords.push($scope.supRecords[$scope.selSupId].SUBMITTED[index]);
        });
        var params = {
            records: selectedRecords,
            empInfos: $scope.empInfos
        };
        modals.open('record-review', params)
            .then(submitReviewedRecords)
            .then($scope.selectNone);
    };

    /**
     * Submits all displayed records that are awaiting supervisor approval as 'APPROVED'
     */
    $scope.approveAll = function () {
        var displayedRecords = $scope.supRecords[$scope.selSupId].SUBMITTED;
        displayedRecords.forEach(function (record) {
            record.recordStatus = 'APPROVED';
        });
        submitRecords(displayedRecords);
    };

    /** --- Internal Methods --- */

    /**
     * Calculates totals for each given record
     * Puts record into $scope.supRecords keyed by supervisor id and record status
     * Returns a list of employee ids for which we don't yet have employee infos
     */
    function initializeRecords(recordResponse) {
        var empIdSet = {};
        function addToEmpIdSet(empId) {
            if (!$scope.empInfos.hasOwnProperty(empId)) {
                empIdSet[empId] = null;
            }
        }

        // All records are stored under an additional supId to facilitate access to all records regardless of supervisor
        var allRecords = $scope.supRecords[allSupervisorsId] = {};
        for (var supId in recordResponse) {
            addToEmpIdSet(supId);   // Add supervisor id to emp info retrieval set
            if (!$scope.supRecords[supId]) {
                $scope.supIds.push(supId);
            }
            var supRecords = $scope.supRecords[supId] = $scope.supRecords[supId] || {};
            for (var iRecord in recordResponse[supId]) {
                var record = recordResponse[supId][iRecord];
                // Add empId to emp info retrieval set
                addToEmpIdSet(record.employeeId);
                // Calculate totals for record
                recordUtils.calculateDailyTotals(record);
                record.totals = recordUtils.getRecordTotals(record);

                var statusList = supRecords[record.recordStatus] = supRecords[record.recordStatus] || [];
                var allStatusList = allRecords[record.recordStatus] = allRecords[record.recordStatus] || [];

                statusList.push(record);        // Store under supervisor
                allStatusList.push(record);     // Store under all
            }
        }
        return Object.keys(empIdSet);
    }

    /**
     * Takes in an object with two sets: one of approved records, one of disapproved records
     * Modifies the record status accordingly for each record in these sets
     * Submits all passed in records via the API
     */
    function submitReviewedRecords(reviewedRecords) {
        console.log('review modal resolved:', reviewedRecords);
        var recordsToSubmit = [];
        angular.forEach(reviewedRecords.approved, function(record) {
            record.recordStatus = 'APPROVED';
            recordsToSubmit.push(record);
        });
        angular.forEach(reviewedRecords.disapproved, function(record) {
            record.recordStatus = 'DISAPPROVED';
            record.remarks = record.rejectionRemarks;
            recordsToSubmit.push(record);
        });
        if (recordsToSubmit.length > 0) {
            submitRecords(recordsToSubmit);
        } else {
            console.log('no records to submit');
        }
    }

    $scope.init();
}
