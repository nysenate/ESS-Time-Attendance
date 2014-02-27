var essApp = angular.module('ess');

essApp.directive('essNotification', [function() {
    var levels = {
        info: {},
        warn: {},
        error: {}
    };
    var types = {
        text: {},
        popup: {}
    };

    return {
        restrict: 'AE',
        link: function(scope, element, attrs) {

        }
    };
}]);