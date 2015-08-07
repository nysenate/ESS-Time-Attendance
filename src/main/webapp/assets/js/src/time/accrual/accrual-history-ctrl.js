var essTime = angular.module('essTime');

essTime.controller('AccrualHistoryCtrl', ['$scope', '$http', 'appProps', 'AccrualHistoryApi',
    function($scope, $http, appProps, AccrualHistoryApi) {

    $scope.state = {
        year: moment().year(),
        accSummaries: null
    };

    $scope.getAccSummaries = function(year) {
        var empId = appProps.user.employeeId;
        var fromDate = moment([year, 0, 1]);
        var toDate = moment([year + 1, 0, 1]);
        if ($scope.state.year == moment().year()) {
            toDate = moment();
        }
        var accSummariesResp = AccrualHistoryApi.get({
            empId: empId,
            fromDate: fromDate.format('YYYY-MM-DD'),
            toDate: toDate.format('YYYY-MM-DD')
        }, function(resp) {
            if (resp.success) {
                $scope.state.accSummaries = resp.result;
            }
            else {
                alert("Error while fetching accruals.");
            }
        });
    };

    $scope.init = function() {
        $scope.getAccSummaries($scope.state.year);
    }();
}]);