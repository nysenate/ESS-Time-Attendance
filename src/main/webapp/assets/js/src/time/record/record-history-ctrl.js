var essTime = angular.module('essTime');

essTime.controller('RecordHistoryCtrl', ['$scope', 'appProps', 'TimeRecordsApi', 'modals',
function ($scope, appProps, timeRecordsApi, modals) {

    $scope.activeYears = appProps.empActiveYears;
    $scope.year = $scope.activeYears[$scope.activeYears.length - 1];

    $scope.init = function() {
        $scope.getRecords();
    };

    /** --- API Methods --- */

    // Get all records for the current user for the selected year
    $scope.getRecords = function() {
        $scope.records = {employee: [], other: []};
        $scope.annualTotals = {};
        var empId = appProps.user.employeeId;
        var now = moment();
        var params = {
            empId: empId,
            from: $scope.year.toString() + '-01-01',
            to: $scope.year < now.year() ?
                $scope.year.toString() + '-12-31' :
                now.format('YYYY-MM-DD')
        };
        console.log('getting new records', params);
        timeRecordsApi.get(params, function(response) {
            initializeRecords(response.result.items[empId]);
        }, function(response) {
            // todo error handling
        })
    };

    /** --- Display Methods --- */

    // Open a new modal window showing a detailed view of the given record
    $scope.showDetails = function(record) {
        var params = { record: record };
        modals.open('details', params);
    };

    /** --- Internal Methods --- */

    // Calculate totals for each record from a record response and categorize them by scope
    function initializeRecords(responseRecords) {
        for(var i in responseRecords) {
            var record = responseRecords[i];

            $scope.calculateDailyTotals(record);
            record.totals = $scope.getRecordTotals(record);

            if (record.scope === "E") {
                $scope.records.employee.push(record);
            } else {
                addToAnnualTotals(record);
                $scope.records.other.push(record);
            }
        }
    }

    // Add the totals of the given record to the running annual totals
    function addToAnnualTotals(record) {
        for(var field in record.totals) {
            if (record.totals.hasOwnProperty(field)) {
                if (!$scope.annualTotals.hasOwnProperty(field)) {
                    $scope.annualTotals[field] = 0;
                }
                $scope.annualTotals[field] += record.totals[field];
            }
        }
    }

    $scope.init();
}]);
