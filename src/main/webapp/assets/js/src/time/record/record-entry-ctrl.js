var essApp = angular.module('ess');

essApp.controller('RecordEntryController', ['$scope', '$http', '$filter', 'appProps', 'ActiveTimeRecordsApi', 'TimeRecordsApi',
                                            'AccrualPeriodApi',
                  function($scope, $http, $filter, appProps, activeRecordsApi, recordsApi, accrualPeriodApi){
    $scope.state = {
        accrual: null
    };

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
        console.log('setDisplayEntries:', record);
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

    $scope.setDirty = function() {
        $scope.records[$scope.iSelectedRecord].dirty = true;
        $scope.refreshDailyTotals();
        $scope.refreshTotals();
    };

    $scope.$watchGroup(['records', 'iSelectedRecord'], function() {
        if ($scope.records && $scope.records[$scope.iSelectedRecord]) {
            $scope.getAccrualForSelectedRecord();
            $scope.setDisplayEntries();
            $scope.refreshDailyTotals();
            $scope.refreshTotals();
            console.log($scope.records[$scope.iSelectedRecord]);
        }
    });

    // A map of employee record statuses to the logical next status upon record submission
    var nextStatusMap = {
        NOT_SUBMITTED: "SUBMITTED",
        DISAPPROVED: "SUBMITTED",
        DISAPPROVED_PERSONNEL: "SUBMITTED_PERSONNEL"
    };

    $scope.saveRecord = function(submit) {
        var record = $scope.records[$scope.iSelectedRecord];
        if (submit) {
            // todo ensure totals + accruals are in line
            record.recordStatus = nextStatusMap[record.recordStatus];
        }
        // todo some basic validation for saving e.g. accrual usage,
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

    $scope.init = function(miscLeaveMap) {
        $scope.miscLeaves = miscLeaveMap;
        $scope.getRecords();
    };

}]);