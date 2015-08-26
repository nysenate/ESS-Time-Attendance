var essApp = angular.module('ess');

essApp.directive('supervisorRecordList', ['appProps', 'modals', function (appProps, modals) {
    return {
        scope: {
            records: '=',           // a list of records to display
            empInfos: '=',          // a map of employee id -> employee info
            selectedIndices: '=?'   // a set of selected record indices
        },
        templateUrl: appProps.ctxPath + '/template/time/record/supervisor-record-list',
        link: link
    };

    function link($scope, $elem, $attr) {

        /** --- Display Methods --- */

        /**
         * Displays a record detail modal for the supplied record
         */
        $scope.showDetails = function(record) {
            var params = {
                record: record,
                employee: $scope.empInfos[record.employeeId]
            };
            modals.open('record-details', params);
        };

        /**
         * Removes the given index from selectedIndices if it already exists there, adds it if it doesn't
         */
        $scope.toggleSelected = function(index) {
            if ($scope.selectedIndices) {
                if ($scope.selectedIndices.has(index)) {
                    $scope.selectedIndices.delete(index);
                } else {
                    $scope.selectedIndices.add(index);
                }
            }
        };
    }
}]);
