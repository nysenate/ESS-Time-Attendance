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

    $scope.getTotal = function(type) {
        var total = 0;
        if ($scope.record) {
            for (var i = 0; i < $scope.dates.length; i++) {
                total += +($scope.record[i][type] || 0);
            }
        }
        return total;
    };

    $scope.getDailyTotal = function(index) {
        var total = 0;
        if ($scope.record && $scope.record[index]) {
            total = +($scope.record[index].work) + +($scope.record[index].holiday) + +($scope.record[index].vac) +
                    +($scope.record[index].personal) + +($scope.record[index].sickEmp) + +($scope.record[index].sickFam) +
                    +($scope.record[index].misc)
        }
        return total;
    };

    $scope.refreshDailyTotals = function() {
        for (var i = 0; i < $scope.dates.length; i++) {
            $scope.record[i].total = $scope.getDailyTotal(i);
        }
    };

    $scope.refreshTotals = function() {
        $scope.totals = {
            work: $scope.getTotal('work'),
            holiday: $scope.getTotal('holiday'),
            vac: $scope.getTotal('vac'),
            personal: $scope.getTotal('personal'),
            sickEmp: $scope.getTotal('sickEmp'),
            sickFam: $scope.getTotal('sickFam'),
            misc: $scope.getTotal('misc'),
            total: $scope.getTotal('total')
        };
    };

    $scope.lastSaveTime = null;
    $scope.showSaveButton = true;

    $scope.setDirty = function() {
        $scope.showSaveButton = true;
        $scope.refreshDailyTotals();
        $scope.refreshTotals();
    };

    $scope.saveRecord = function() {
        this.lastSaveTime = new Date();
        this.showSaveButton = false;
    };

    $scope.init = function() {
        $scope.record = [];
        for (var i = 0; i < $scope.dates.length; i++) {
            $scope.record[i] = {
                work: '',
                holiday: '',
                vac: '',
                personal : '',
                sickEmp: '',
                sickFam: '',
                misc: ''
            };
        }
    };

    $scope.init();

}]);