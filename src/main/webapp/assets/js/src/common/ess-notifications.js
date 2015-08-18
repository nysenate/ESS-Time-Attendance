var essApp = angular.module('ess');

essApp.directive('essNotification', [function() {
    return {
        restrict: 'AE',
        scope: {
            level: '@',
            title: '@',
            message: '@',
            mode: '@'
        },
        template: '<div class="ess-notification {{level}}">' +
                    '<h2>{{title}}</h2>' +
                    '<p>{{message}}</p>' +
                  '</div>',
        link: function(scope, element, attrs) {
            //console.log("Inside the essNotification link function");
        }
    };
}]);