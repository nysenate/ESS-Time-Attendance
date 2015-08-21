var essApp = angular.module('ess');

essApp.directive('recordReviewModal', ['appProps', 'modals', function (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-review-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {

        $scope.submission = {
            approved: [],
            disapproved: []
        };

        /** --- Display Methods --- */

        /**
         * Resolves the modal, returning the records that were selected as approved/disapproved
         */
        $scope.resolve = function () {
            modals.resolve($scope.submission);
        };

        /**
         * Closes the modal
         */
        $scope.close = function () {
            modals.reject();
        }
    }
}]);