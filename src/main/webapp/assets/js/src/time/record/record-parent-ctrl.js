var essTime = angular.module('essTime');

essTime.controller('RecordParentCtrl', ['$scope', function ($scope) {

    $scope.init = function(miscLeaveMap) {
        $scope.miscLeaves = miscLeaveMap;
    };

    // Get the total used hours for a single time entry
    $scope.getDailyTotal = function(entry) {
        return +(entry.workHours) + +(entry.travelHours) + +(entry.holidayHours) + +(entry.vacationHours) +
            +(entry.personalHours) + +(entry.sickEmpHours) + +(entry.sickFamHours) + +(entry.miscHours);
    };

    // Calculate and add the daily total as a field in each time entry within a record
    $scope.calculateDailyTotals = function(record) {
        for (var i = 0, entries = record.timeEntries; i < entries.length; i++) {
            entries[i].total = $scope.getDailyTotal(entries[i]);
        }
    };

    // Gets the total number of hours used for a specific time usage type over an entire time record
    $scope.getTotal = function(record, type) {
        var total = 0;
        var entries = record.timeEntries;
        if (entries) {
            for (var i = 0; i < entries.length; i++) {
                total += +(entries[i][type] || 0);
            }
        }
        return total;
    };

    // Returns an object containing the total number of hours for each time usage type over an entire time recodr
    $scope.getRecordTotals = function(record) {
        return {
            work: $scope.getTotal(record, 'workHours'),
            travel: $scope.getTotal(record, 'travelHours'),
            holiday: $scope.getTotal(record, 'holidayHours'),
            vac: $scope.getTotal(record, 'vacationHours'),
            personal: $scope.getTotal(record, 'personalHours'),
            sickEmp: $scope.getTotal(record, 'sickEmpHours'),
            sickFam: $scope.getTotal(record, 'sickFamHours'),
            misc: $scope.getTotal(record, 'miscHours'),
            total: $scope.getTotal(record, 'total')
        };
    }
}]);

