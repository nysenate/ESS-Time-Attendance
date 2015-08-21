var essMyInfo = angular.module('essMyInfo');

essMyInfo.controller('EmpTransactionHistoryCtrl',
    ['$scope', '$http', 'appProps', 'EmpActiveYearsApi', 'EmpTransactionsApi',
    function($scope, $http, appProps, EmpActiveYearsApi, EmpTransactionsApi) {

    $scope.state = {
        empId: appProps.user.employeeId,
        today: moment(),
        transactions: {},
        activeYears: [],
        selectedYear: null,
        filterCodes: ''
    };

    $scope.getTransRecords = function(year, callBack) {
        var fromDate, toDate;
        if (year === 'All') {
            fromDate = moment([1980, 0, 1]);
            toDate = moment([2999, 0, 1]);
        }
        else {
            fromDate = moment([year, 0, 1]);
            toDate = moment([year + 1, 0, 1]).subtract(1, 'days');
        }
        EmpTransactionsApi.get({empId: $scope.state.empId,
                fromDate: fromDate.format('YYYY-MM-DD'),
                toDate: toDate.format('YYYY-MM-DD'),
                codes: $scope.state.filterCodes},
            function(resp) {
                if (resp.success && resp.total > 0) {
                    $scope.state.transactions[year] =
                        // Group the transactions by date
                        resp.transactions.reduce(function(res,curr) {
                            if (!res[curr.effectDate]) res[curr.effectDate] = [];
                            res[curr.effectDate].push(curr);
                            return res;
                        }, {});
                }
                else {
                    $scope.state.transactions[year] = false;
                }
                if (callBack) callBack();
            },
            function(resp) {
                $scope.state.transactions[year] = false;
            });
    };

    $scope.getEmpActiveYears = function(callBack) {
        EmpActiveYearsApi.get({empId: $scope.state.empId}, function(resp) {
            $scope.state.activeYears = resp.activeYears;
            $scope.state.activeYears.push('All');
            $scope.state.activeYears.reverse();
            $scope.state.selectedYear = resp.activeYears[0];
            if (callBack) callBack();
        });
    };

    $scope.init = function() {
        $scope.getEmpActiveYears(function() {
            $scope.getTransRecords($scope.state.selectedYear);
        });
    }();
}]);