var essApp = angular.module('ess');

essApp.controller('RecordEntryController', ['$scope', '$http', 'appProps', 'ActiveTimeRecordsApi', 'TimeRecordsApi',
                                            'AccrualPeriodApi',
                  function($scope, $http, appProps, activeRecordsApi, recordsApi, accrualPeriodApi){
    $scope.state = {
        accrual: null
    };

    $scope.getRecords = function () {
        var empId = appProps.user.employeeId;
        activeRecordsApi.get({
            empId: empId,
            status: ['NOT_SUBMITTED', 'DISAPPROVED', 'DISAPPROVED_PERSONNEL']
        }, function (response) {
            $scope.records = [];
            if (empId in response.result.items) {
                $scope.records = response.result.items[empId];
                console.log($scope.records);
            }
        });
    };

    $scope.getAccrualForSelectedRecord = function() {
        var empId = appProps.user.employeeId;
        var periodStartMoment = moment($scope.selectedRecord.payPeriod.startDate);
        accrualPeriodApi.get({empId: empId, beforeDate: periodStartMoment.format('YYYY-MM-DD')}, function(resp){
            if (resp.success) {
                $scope.state.accrual = resp.result;
            }
        });
    };

    $scope.setDisplayEntries = function() {
        var record = $scope.selectedRecord;
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
            $scope.displayEntries.push(entry);
        }
    };

    $scope.getTotal = function(type) {
        var total = 0;
        if ($scope.records) {
            for (var i = 0; i < $scope.records.length; i++) {
                total += +($scope.records[i][type] || 0);
            }
        }
        return total;
    };

    $scope.getDailyTotal = function(entry) {
        return +(entry.workHours) + +(entry.travelHours) + +(entry.holidayHours) + +(entry.vacationHours) +
                +(entry.personalHours) + +(entry.sickEmpHours) + +(entry.sickFamHours) + +(entry.miscHours);
    };

    $scope.refreshDailyTotals = function() {
        console.log($scope.selectedRecord);
        for (var i = 0, entries = $scope.selectedRecord.timeEntries; i < entries.length; i++) {
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
        $scope.selectedRecord.dirty = true;
        $scope.refreshDailyTotals();
        $scope.refreshTotals();
    };

    $scope.$watch('selectedRecord', function() {
        if ($scope.selectedRecord) {
            $scope.getAccrualForSelectedRecord();
            $scope.setDisplayEntries();
            $scope.refreshDailyTotals();
            $scope.refreshTotals();
        }
    });

    $scope.saveRecord = function() {
        var record = $scope.selectedRecord;
        recordsApi.save(record, function (response) {
            record.updateDate = moment().toISOString();
            record.savedDate = record.updateDate;
        }, function (response) {
            // todo invalid record response
        });
    };

    $scope.init = function(miscLeaveMap) {
        $scope.miscLeaves = miscLeaveMap;
        $scope.getRecords();
    };

}]);