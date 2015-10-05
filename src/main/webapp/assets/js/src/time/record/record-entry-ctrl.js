var essApp = angular.module('ess')
        .controller('RecordEntryController', ['$scope', '$filter', 'appProps', 'ActiveTimeRecordsApi',
                                            'TimeRecordsApi', 'AccrualPeriodApi', 'RecordUtils', 'LocationService',
                                             'modals', recordEntryCtrl]);

function recordEntryCtrl($scope, $filter, appProps, activeRecordsApi,
                         recordsApi, accrualPeriodApi, recordUtils, locationService, modals) {

    var initialState = {
        empId: appProps.user.employeeId,  // Employee Id
        miscLeaves: appProps.miscLeaves,  // Listing of misc leave types
        accrual: null,                    // Accrual info for selected record
        records: [],                      // All active employee records
        iSelectedRecord: 0,               // Index of the currently selected record,
        displayEntries: [],               // The entries that are being displayed

        // Page state
        pageState: 0                      // References the values from $scope.pageStates
    };

    $scope.state = null;                  // The container for all the state variables for this page

    // Enumeration of the possible page states.
    $scope.pageStates = {
        INITIAL: 0,
        FETCHING: 1,
        FETCHED: 2,
        SAVING: 3,
        SAVED: 4,
        SAVE_FAILURE: 5,
        SUBMIT_ACK: 6,
        SUBMITTING: 7,
        SUBMITTED: 8,
        SUBMIT_FAILURE: 9
    };

    // Create a new state from the values in the default state.
    $scope.initializeState = function() {
        $scope.state = angular.extend({}, initialState);
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
        console.log('Time record initialization');
        $scope.initializeState();
        $scope.getRecords();
    };

    /** --- Watches --- */

    // Update accruals, display entries, totals when a new record is selected
    $scope.$watchGroup(['state.records', 'state.iSelectedRecord'], function() {
        if ($scope.state.records && $scope.state.records[$scope.state.iSelectedRecord]) {
            $scope.validation = getDefaultValidation();
            $scope.getAccrualForSelectedRecord();
            setDisplayEntries();
            onRecordChange();
            setRecordSearchParams();
        }
    });

    /** --- API Methods --- */

    /**
     * Fetches the employee's active records from the server, auto-selecting a record
     * if it's end date is supplied in the query params.
     */
    $scope.getRecords = function() {
        $scope.state.pageState = $scope.pageStates.FETCHING;
        activeRecordsApi.get({
            empId: $scope.state.empId,
            scope: 'E'
        }, function (response) {
            if ($scope.state.empId in response.result.items) {
                $scope.state.records = response.result.items[$scope.state.empId];
                angular.forEach($scope.state.records, function(record){
                    // Compute the due from dates for each record
                    var endDateMoment = moment(record.endDate);
                    record.dueFromNowStr = endDateMoment.fromNow(false);
                    record.isDue = endDateMoment.isBefore(moment().add(1, 'days').startOf('day'));
                });
                // Change the selected record based on the query param if it exists
                linkRecordFromQueryParam();
            }
        }).$promise.finally(function() {
            $scope.state.pageState = $scope.pageStates.FETCHED;
        });
    };

    /**
     * Returns the currently selected record.
     * @returns timeRecord object
     */
    $scope.getSelectedRecord = function() {
        return $scope.state.record[$scope.state.iSelectedRecord];
    };

    /**
     * Fetches the accruals for the currently selected time record from the server.
     */
    $scope.getAccrualForSelectedRecord = function() {
        var empId = $scope.state.empId;
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        var periodStartMoment = moment(record.payPeriod.startDate);
        accrualPeriodApi.get({empId: empId, beforeDate: periodStartMoment.format('YYYY-MM-DD')}, function(resp) {
            if (resp.success) {
                $scope.state.accrual = resp.result;
            }
        });
    };

    /**
     * Validates and saves the currently selected record.
     * @param submit - Set to true if user is also submitting the record. This will modify the record status if
     *                 it completes successfully.
     */
    $scope.saveRecord = function(submit) {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        if (submit) {
            // TODO: Ensure totals hours are in line.
            record.recordStatus = nextStatusMap[record.recordStatus];
        }
        console.log(submit ? 'submitting' : 'saving', 'record', record);
        // TODO: Validate the current record
        // Open the modal to indicate save/submit
        if (submit) {
            $scope.state.pageState = $scope.pageStates.SUBMIT_ACK;
            modals.open('submit-indicator', {'record': record});
        }
        else {
            modals.open('save-indicator', {'record': record});
            $scope.state.pageState = $scope.pageStates.SAVING;
            recordsApi.save(record, function (resp) {
                record.updateDate = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
                record.savedDate = record.updateDate;
                record.dirty = false;
                $scope.state.pageState = $scope.pageStates.SAVED;
            }, function (resp) {
                alert("Failed to save record!");
                console.log(resp);
                $scope.state.pageState = $scope.pageStates.SAVE_FAILURE;
            });
        }
    };

    /**
     * Submits the currently selected record. This assumes any necessary validation has already been
     * made on this record.
     */
    $scope.submitRecord = function() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        $scope.state.pageState = $scope.pageStates.SUBMITTING;
        recordsApi.save(record, function (resp) {
            $scope.state.pageState = $scope.pageStates.SUBMITTED;
        }, function (resp) {
            alert("Failed to submit time record!");
            console.log(resp);
            $scope.state.pageState = $scope.pageStates.SUBMIT_FAILURE;
        });
    };

    $scope.finishSubmitModal = function() {
        $scope.closeModal();
        $scope.init();
    };

    /**
     * Closes any open modals by resolving them.
     */
    $scope.closeModal = function() {
        modals.resolve();
    };

    /** --- Display Methods --- */

    /**
     * Returns true if the given date falls on a weekend.
     * @param date - ISO, JS, or Moment Date
     * @returns {boolean} - true if weekend, false otherwise.
     */
    $scope.isWeekend = function(date) {
        return $filter('momentIsDOW')(date, [0, 6]);
    };

    /**
     * Returns true if all validation fields are true in the validation object.
     * @returns {boolean}
     */
    $scope.recordValid = function() {
        return allTrue($scope.validation);
    };

    /**
     * This method is called every time a field is modified on the currently selected record.
     */
    $scope.setDirty = function() {
        $scope.state.records[$scope.state.iSelectedRecord].dirty = true;
        onRecordChange();
    };

    /**
     * Returns true if the record is submittable, i.e. it exists, passes all validations, and has ended or will end
     * today.
     * @returns {boolean}
     */
    $scope.recordSubmittable = function () {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        return record && $scope.recordValid() && !moment(record.endDate).isAfter(moment(), 'day');
    };

    /** --- Internal Methods --- */

    /**
     * Refreshes totals and validates a record when a change occurs on a record.
     */
    function onRecordChange() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        recordUtils.calculateDailyTotals(record);
        $scope.totals = recordUtils.getRecordTotals(record);
        validateRecord();
    }

    /**
     * Creates a displayEntries array for a given record, filling in dates that the record doesn't cover with
     * dummy entries.
     */
    function setDisplayEntries() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        console.log('new selected record:', moment(record.beginDate).format('l'), record);
        $scope.state.displayEntries = [];
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
            $scope.state.displayEntries.push(entry);
        }
    }

    /**
     * Recursively ensures that all boolean fields are true within the given object. (see $scope.recordValid)
     * @param object
     * @returns {boolean}
     */
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

    /**
     * Performs a series of validation checks on the active record, updating $scope.validation with the result of
     * the checks.
     */
    function validateRecord() {
        validateAccruals();
        // todo validate more things
    }

    /**
     * Ensures that the active record does not use more hours than they have accrued.
     */
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

    /**
     * Sets the search params to indicate the currently active record.
     */
    function setRecordSearchParams() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        locationService.setSearchParam('record', record.beginDate);
    }

    /**
     * Checks for a 'record' search param in the url and if it exists, the record with a start date that matches
     * the given date will be set as the selected record.
     */
    function linkRecordFromQueryParam() {
        var recordParam = locationService.getSearchParam('record');
        if (recordParam) {
            // Need to break out early, hence no angular.forEach.
            for (var iRecord in $scope.state.records) {
                var record = $scope.state.records[iRecord];
                if (record.beginDate === recordParam) {
                    $scope.state.iSelectedRecord = iRecord;
                    break;
                }
            }
        }
    }

    $scope.init();
}