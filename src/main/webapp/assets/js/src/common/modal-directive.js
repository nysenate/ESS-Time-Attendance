/**
 * A modal container
 *
 * Insert markup for one or more modals inside this directive and display based on $scope.subview
 */
angular.module('ess')
    .directive('modalContainer', ['$rootScope', 'modals',
function ($rootScope, modals) {

    return link;

    function link($scope, $element) {
        // Determines which modal is being rendered
        $scope.subview = null;

        // Reject modal when the user clicks the backdrop
        $element.on('click', function (event) {
            if ($element[0] !== event.target) {
                return;
            }
            $scope.$apply(modals.reject);
        });

        // Set subview upon modal open event
        $rootScope.$on('modals.open', function (event, modalType) {
            $scope.subview = modalType;
        });

        // Remove subview upon modal close event
        $rootScope.$on('modals.close', function(event) {
            $scope.subview = null;
        });
    }
}]);