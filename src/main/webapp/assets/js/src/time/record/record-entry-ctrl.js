var essApp = angular.module('ess')
        .controller('RecordEntryController', ['$scope', '$http', '$filter', 'appProps', 'ActiveTimeRecordsApi',
                                            'TimeRecordsApi', 'AccrualPeriodApi', 'RecordUtils', 'LocationService',
                                            recordEntryCtrl]);


function recordEntryCtrl($scope, $http, $filter, appProps, activeRecordsApi,
                         recordsApi, accrualPeriodApi, recordUtils, locationService) {

    $scope.state = {
        accrual: null
    };

    $scope.miscLeaves = appProps.miscLeaves;

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

    // Settings for floating the time entry table heading
    $scope.floatTheadOpts = {
        scrollingTop: 47
    };

    $scope.init = function() {
        console.log('rec init');
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
                linkToRecord();
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
        console.log(submit ? 'submitting' : 'saving', 'record', record);
        recordsApi.save(record, function (response) {
            if (submit) {
                $scope.getRecords();
                console.log('submitted');
            } else {
                record.updateDate = moment().toISOString();
                record.savedDate = record.updateDate;
                record.dirty = false;
                console.log('saved');
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
        var record = $scope.records[$scope.iSelectedRecord];

        recordUtils.calculateDailyTotals(record);
        $scope.totals = recordUtils.getRecordTotals(record);

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
            var sickUsage = $scope.totals.sickEmpHours + $scope.totals.sickFamHours;
            accValidation.sick = sickUsage <= accrual.sickAvailable;
            accValidation.personal = $scope.totals.personalHours <= accrual.personalAvailable;
            accValidation.vacation = $scope.totals.vacationHours <= accrual.vacationAvailable;
        }
    }

    // Sets search params pertaining to the current active record
    function setRecordSearchParams() {
        var record = $scope.records[$scope.iSelectedRecord];
        locationService.setSearchParam('record', record.beginDate);
    }

    // Checks for a 'record' search param
    // If a record exists with a start date equal to the 'record' param, set that record as selected record
    function linkToRecord() {
        var recordParam = locationService.getSearchParam('record');
        if (recordParam) {
            console.log('linking to', recordParam);
            for(var iRecord in $scope.records) {
                var record = $scope.records[iRecord];
                if (record.beginDate === recordParam) {
                    $scope.iSelectedRecord = iRecord;
                    break;
                }
            }
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
            setRecordSearchParams();
        }
    });

    $scope.init();
}