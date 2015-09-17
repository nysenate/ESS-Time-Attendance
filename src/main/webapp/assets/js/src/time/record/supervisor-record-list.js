var essApp = angular.module('ess');

essApp.directive('supervisorRecordList', ['appProps', 'modals', function (appProps, modals) {
    return {
        scope: {
            records: '=',           // a list of records to display
            selectedIndices: '=?'   // a map of selected record indices, where the indices are
                                    // stored as object properties with a value of true
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
                employee: record.employee
            };
            modals.open('record-details', params);
        };

        /**
         * Removes the given index from selectedIndices if it already exists there, adds it if it doesn't
         */
        $scope.toggleSelected = function(index) {
            if ($scope.selectedIndices) {
                if ($scope.selectedIndices[index] === true) {
                    delete $scope.selectedIndices[index];
                } else {
                    $scope.selectedIndices[index] = true;
                }
            }
        };
    }
}]);