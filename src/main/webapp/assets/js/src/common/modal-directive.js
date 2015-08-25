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
        // A stack of modal names in order of opening
        $scope.openModals = [];

        // The name of the modal most recently opened
        $scope.top = null;

        $scope.isOpen = function(viewName) {
            return $scope.openModals.indexOf(viewName) >= 0;
        };

        // Reject modal when the user clicks the backdrop
        $element.on('click', function (event) {
            if ($element[0] !== event.target) {
                return;
            }
            $scope.$apply(modals.reject);
        });

        // Set subview upon modal open event
        $rootScope.$on('modals.open', function (event, modalType) {
            console.log('showing modal of type', modalType);
            $scope.openModals.push(modalType);
            updateTop();
        });

        // Remove subview upon modal close event
        $rootScope.$on('modals.close', function(event) {
            $scope.openModals.pop();
            updateTop();
        });

        function updateTop() {
            $scope.top = $scope.openModals[$scope.openModals.length - 1];
        }
    }
}]);