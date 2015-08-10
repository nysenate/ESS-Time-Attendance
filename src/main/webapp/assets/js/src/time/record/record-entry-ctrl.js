var essApp = angular.module('ess');

essApp.controller('RecordEntryController', ['$scope', '$http', '$filter', 'appProps', 'ActiveTimeRecordsApi', 'TimeRecordsApi',
                                            'AccrualPeriodApi',
function($scope, $http, $filter, appProps, activeRecordsApi, recordsApi, accrualPeriodApi){
    $scope.state = {
        accrual: null
    };

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

    $scope.setDisplayEntries = function() {
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
    };

    $scope.isWeekend = function(date) {
        return $filter('momentIsDOW')(date, [0, 6]);
    };

    $scope.getTotal = function(type) {
        var total = 0;
        var entries = $scope.records[$scope.iSelectedRecord].timeEntries;
        if (entries) {
            for (var i = 0; i < entries.length; i++) {
                total += +(entries[i][type] || 0);
            }
        }
        return total;
    };

    $scope.getDailyTotal = function(entry) {
        return +(entry.workHours) + +(entry.travelHours) + +(entry.holidayHours) + +(entry.vacationHours) +
                +(entry.personalHours) + +(entry.sickEmpHours) + +(entry.sickFamHours) + +(entry.miscHours);
    };

    $scope.refreshDailyTotals = function() {
        for (var i = 0, entries = $scope.records[$scope.iSelectedRecord].timeEntries; i < entries.length; i++) {
            entries[i].total = $scope.getDailyTotal(entries[i]);
        }
    };

    $scope.refreshTotals = function() {
        $scope.totals = {
            work: $scope.getTotal('workHours'),
            travel: $scope.getTotal('travelHours'),
            holiday: $scope.getTotal('holiday'),
            vac: $scope.getTotal('vacationHours'),
            personal: $scope.getTotal('personalHours'),
            sickEmp: $scope.getTotal('sickEmpHours'),
            sickFam: $scope.getTotal('sickFamHours'),
            misc: $scope.getTotal('miscHours'),
            total: $scope.getTotal('total')
        };
    };

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

    $scope.recordValid = function() {
        return allTrue($scope.validation);
    };

    $scope.validateRecord = function() {
        $scope.validateAccruals();
        // todo validate more things
    };

    $scope.validateAccruals = function() {
        var accValidation = $scope.validation.accruals;
        var accrual = $scope.state.accrual;

        if (accrual) {
            var sickUsage = $scope.totals.sickEmp + $scope.totals.sickFam;
            accValidation.sick = sickUsage <= accrual.sickAvailable;
            accValidation.personal = $scope.totals.personal <= accrual.personalAvailable;
            accValidation.vacation = $scope.totals.vac <= accrual.vacationAvailable;
        }
    };

    $scope.setDirty = function() {
        $scope.records[$scope.iSelectedRecord].dirty = true;
        $scope.refreshDailyTotals();
        $scope.refreshTotals();
        $scope.validateRecord();
    };

    $scope.recordSubmittable = function () {
        var record = $scope.records[$scope.iSelectedRecord];
        return record && $scope.recordValid() && !moment(record.endDate).isAfter(moment(), 'day');
    };

    // A map of employee record statuses to the logical next status upon record submission
    var nextStatusMap = {
        NOT_SUBMITTED: "SUBMITTED",
        DISAPPROVED: "SUBMITTED",
        DISAPPROVED_PERSONNEL: "SUBMITTED_PERSONNEL"
    };

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

    $scope.$watchGroup(['records', 'iSelectedRecord'], function() {
        if ($scope.records && $scope.records[$scope.iSelectedRecord]) {
            $scope.validation = getDefaultValidation();
            $scope.getAccrualForSelectedRecord();
            $scope.setDisplayEntries();
            $scope.refreshDailyTotals();
            $scope.refreshTotals();
            $scope.validateRecord();
        }
    });

    $scope.init = function(miscLeaveMap) {
        $scope.miscLeaves = miscLeaveMap;
        $scope.getRecords();
    };

}]);