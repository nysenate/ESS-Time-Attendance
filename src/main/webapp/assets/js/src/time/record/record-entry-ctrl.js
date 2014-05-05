var essApp = angular.module('ess');

essApp.controller('RecordEntryController', ['$scope', '$http', function($scope, $http){
    $scope.payPeriod = {
        range: '04/24/2014 - 05/07/2014',
        start: new Date('04/24/2014'),
        end: new Date('05/07/2014')
    };

    $scope.dates = [
        new Date('04/24/2014'), new Date('04/25/2014'), new Date('04/26/2014'), new Date('04/27/2014'), new Date('04/28/2014'),
        new Date('04/29/2014'), new Date('04/30/2014'), new Date('05/01/2014'), new Date('05/02/2014'), new Date('05/03/2014'),
        new Date('05/04/2014'), new Date('05/05/2014'), new Date('05/06/2014'), new Date('05/07/2014')
    ];


}]);