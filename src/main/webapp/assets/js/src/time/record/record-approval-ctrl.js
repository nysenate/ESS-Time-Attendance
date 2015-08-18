var essApp = angular.module('ess');

essApp.controller('RecordApprovalCtrl', ['$scope', 'appProps', 'RecordUtils', 'modals', recordApprovalCtrl]);

function recordApprovalCtrl($scope, appProps, recordUtils, modals) {


    /** --- Api Methods --- */

    $scope.getEmployeeRecords = function() {
        var empId = appProps.user.employeeId;

    };
}
