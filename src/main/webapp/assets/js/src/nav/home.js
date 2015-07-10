var essApp = angular.module('ess');

essApp.controller('MainCtrl', ['$scope', '$http', '$route', '$routeParams', '$location',
    function($scope, $http, $route, $routeParams, $location) {
        $scope.test = 'MOose Kitteh';
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    }
]);

essApp.controller('TestCtrl', ['$scope', '$http', '$routeParams',
    function($scope, $http, $routeParams) {
        $scope.test = "Inside this view wosoo!";
    }
]);