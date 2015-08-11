var essApp = angular.module('ess');

essApp.controller('RecordEntryController', ['$scope', '$http', '$filter', 'appProps', 'ActiveTimeRecordsApi', 'TimeRecordsApi',
                                            'AccrualPeriodApi',
function($scope, $http, $filter, appProps, activeRecordsApi, recordsApi, accrualPeriodApi){

    $scope.state = {
        accrual: null
    };

    // Get a clean validation object where everything is set as valid
    function getDefaultValidation() {
        return {
            accruals: {
                sick: true,
                vacation: true,
                personal: true
            }
        }
    }
    $scope.validation = getDefaultValidation();

    // A map of employee record statuses to the logical next status upon record submission
    var nextStatusMap = {
        NOT_SUBMITTED: "SUBMITTED",
        DISAPPROVED: "SUBMITTED",
        DISAPPROVED_PERSONNEL: "SUBMITTED_PERSONNEL"
    };

    $scope.init = function() {
        $scope.getRecords();
    };

    /** --- API Methods --- */

    // Get active employee scoped records for the current user
    $scope.getRecords = function () {
        var empId = appProps.user.employeeId;
        $scope.records = [];
        $scope.iSelectedRecord = 0;
        activeRecordsApi.get({
            empId: empId,
            status: ['NOT_SUBMITTED', 'DISAPPROVED', 'DISAPPROVED_PERSONNEL']
        }, function (response) {
            if (empId in response.result.items) {
                $scope.records = response.result.items[empId];
                console.log($scope.records);
            }
        });
    };

    // Gets accrual usage and allowances pertinent to the currently selected time record
    $scope.getAccrualForSelectedRecord = function() {
        var empId = appProps.user.employeeId;
        var record = $scope.records[$scope.iSelectedRecord];
        var periodStartMoment = moment(record.payPeriod.startDate);
        accrualPeriodApi.get({empId: empId, beforeDate: periodStartMoment.format('YYYY-MM-DD')}, function(resp){
            if (resp.success) {
                $scope.state.accrual = resp.result;
            }
        });
    };

    // Saves the currently selected record.  If the submit parameter is true, modifies the record status
    $scope.saveRecord = function(submit) {
        var record = $scope.records[$scope.iSelectedRecord];
        if (submit) {
            // todo ensure totals hours are in line
            record.recordStatus = nextStatusMap[record.recordStatus];
        }
        recordsApi.save(record, function (response) {
            if (submit) {
                $scope.getRecords();
            } else {
                record.updateDate = moment().toISOString();
                record.savedDate = record.updateDate;
                record.dirty = false;
            }
        }, function (response) {
            // todo handle invalid record response
        });
    };

    /** --- Display Methods --- */

    // Returns true if the given date is on the weekend
    $scope.isWeekend = function(date) {
        return $filter('momentIsDOW')(date, [0, 6]);
    };

    // Returns true if all validation fields are true in the validation object
    $scope.recordValid = function() {
        return allTrue($scope.validation);
    };

    // This function is called every time a field is modified on the selected record
    $scope.setDirty = function() {
        $scope.records[$scope.iSelectedRecord].dirty = true;
        onRecordChange();
    };

    // Returns true if the record is submittable, i.e. it exists, passes validation, and has already ended
    $scope.recordSubmittable = function () {
        var record = $scope.records[$scope.iSelectedRecord];
        return record && $scope.recordValid() && !moment(record.endDate).isAfter(moment(), 'day');
    };

    /** --- Internal Methods --- */

    // Refreshes totals and validates a record when a change occurs on a record
    function onRecordChange() {
        refreshDailyTotals();
        refreshTotals();
        validateRecord();
    }

    // Creates a displayEntries array for a given record, filling in dates that the record doesn't cover with dummy entries
    function setDisplayEntries() {
        var record = $scope.records[$scope.iSelectedRecord];
        console.log('new selected record:', moment(record.beginDate).format('l'), record);
        $scope.displayEntries = [];
        var entryIndex = 0;
        for (var date = moment(record.payPeriod.startDate), periodEnd = moment(record.payPeriod.endDate);
                !date.isAfter(periodEnd); date = date.add(1, 'days')) {
            var entry;
            if (entryIndex < record.timeEntries.length && date.isSame(record.timeEntries[entryIndex].date, 'day')) {
                entry = record.timeEntries[entryIndex++];
            } else {
                entry = {dummyEntry: true};
            }
            entry.unavailable = date.isAfter(moment(), 'day');
            $scope.displayEntries.push(entry);
        }
    }

    // Updates the daily totals for each time entry in the selected record
    function refreshDailyTotals() {
        $scope.calculateDailyTotals($scope.records[$scope.iSelectedRecord]);
    }

    // Calculates the overall totals for the selected record
    function refreshTotals() {
        $scope.totals = $scope.getRecordTotals($scope.records[$scope.iSelectedRecord]);
    }

    // Recursively ensures that all boolean fields are true within the given object (see $scope.recordValid)
    function allTrue(object) {
        if (typeof object === 'boolean') {
            return object;
        }
        for (var prop in object) {
            if (object.hasOwnProperty(prop) && !allTrue(object[prop])) {
                return false;
            }
        }
        return true;
    }

    // Performs a series of validation checks on the active record, updating $scope.validation with the result of the checks
    function validateRecord() {
        validateAccruals();
        // todo validate more things
    }

    // Ensures that the active record does not use more hours than they have accrued
    function validateAccruals() {
        var accValidation = $scope.validation.accruals;
        var accrual = $scope.state.accrual;

        if (accrual) {
            var sickUsage = $scope.totals.sickEmp + $scope.totals.sickFam;
            accValidation.sick = sickUsage <= accrual.sickAvailable;
            accValidation.personal = $scope.totals.personal <= accrual.personalAvailable;
            accValidation.vacation = $scope.totals.vac <= accrual.vacationAvailable;
        }
    }

    /** --- Watches --- */

    // Update accruals, display entries, totals when a new record is selected
    $scope.$watchGroup(['records', 'iSelectedRecord'], function() {
        if ($scope.records && $scope.records[$scope.iSelectedRecord]) {
            $scope.validation = getDefaultValidation();
            $scope.getAccrualForSelectedRecord();
            setDisplayEntries();
            onRecordChange();
        }
    });

    $scope.init();
}]);