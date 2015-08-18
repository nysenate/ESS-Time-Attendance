var essTime = angular.module('essTime');

essTime.controller('AccrualHistoryCtrl',
    ['$scope', '$http', 'appProps', 'AccrualHistoryApi', 'EmpActiveYearsApi',
    function($scope, $http, appProps, AccrualHistoryApi, EmpActiveYearsApi) {

    $scope.state = {
        empId: appProps.user.employeeId,
        today: moment(),
        accSummaries: null,
        activeYears: [],
        selectedYear: null
    };

    $scope.getAccSummaries = function(year) {
        var fromDate = moment([year, 0, 1]);
        var toDate = moment([year + 1, 0, 1]).subtract(1, 'days');
        var accSummariesResp = AccrualHistoryApi.get({
            empId: $scope.state.empId,
            fromDate: fromDate.format('YYYY-MM-DD'),
            toDate: toDate.format('YYYY-MM-DD')
        }, function(resp) {
            if (resp.success) {
                $scope.state.accSummaries = resp.result;
                // Compute deltas
                for (var i = 0; i < $scope.state.accSummaries.length; i++) {
                    var currSummary = $scope.state.accSummaries[i];
                    if (i == 0) {
                        currSummary.vacationUsedDelta = currSummary.vacationUsed;
                        currSummary.personalUsedDelta = currSummary.personalUsed;
                        currSummary.sickUsedDelta = currSummary.empSickUsed + currSummary.famSickUsed;
                    }
                    else {
                        var prevSummary = $scope.state.accSummaries[i - 1];
                        currSummary.vacationUsedDelta = currSummary.vacationUsed - prevSummary.vacationUsed;
                        currSummary.personalUsedDelta = currSummary.personalUsed - prevSummary.personalUsed;
                        currSummary.sickUsedDelta = (currSummary.empSickUsed + currSummary.famSickUsed) -
                                                    (prevSummary.empSickUsed + prevSummary.famSickUsed);
                    }
                }
            }
            else {
                alert("Error while fetching accruals.");
            }
        });
    };

    $scope.getEmpActiveYears = function(callBack) {
        EmpActiveYearsApi.get({empId: $scope.state.empId}, function(resp) {
            $scope.state.activeYears = resp.activeYears.reverse();
            $scope.state.selectedYear = resp.activeYears[0];
            if (callBack) callBack();
        });
    };

    $scope.floatTheadOpts = {
        scrollingTop: 47
    };

    $scope.init = function() {
        $scope.getEmpActiveYears(function() {
            $scope.getAccSummaries($scope.state.selectedYear);
        });
    }();
}]);