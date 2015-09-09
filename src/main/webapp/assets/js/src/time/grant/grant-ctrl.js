var essTime = angular.module('essTime');

essTime.controller('GrantPrivilegesCtrl', ['$scope', '$http', 'appProps', 'SupervisorChainApi', 'SupervisorGrantsApi',
   function($scope, $http, appProps, SupervisorChainApi, SupervisorGrantsApi) {

       $scope.state = {
           empId: appProps.user.employeeId,
           selectedGrantee: null,
           grantees: {}
       };

       $scope.init = function() {
           SupervisorChainApi.get({empId: $scope.state.empId}, function(resp) {
               if (resp.success == true) {
                   angular.forEach(resp.result.supChain, function(sup) {
                       sup.granted = false;
                       sup.grantStart = sup.grantEnd = null;
                       $scope.state.grantees[sup.employeeId] = sup;
                   });
                   SupervisorGrantsApi.get({supId: $scope.state.empId}, function(resp) {
                     if (resp.success) {
                         angular.forEach(resp.grants, function(grant) {
                             if (!$scope.state.grantees[grant.grantSupervisorId]) {
                                 $scope.state.grantees[grant.grantSupervisorId] = grant.grantSupervisor;
                             }
                             $scope.state.grantees[grant.grantSupervisorId].granted = true;
                             $scope.state.grantees[grant.grantSupervisorId].grantStart =
                                (grant.startDate != null) ? moment(grant.startDate).format('MM/DD/YYYY') : null;
                             $scope.state.grantees[grant.grantSupervisorId].grantEnd =
                                (grant.endDate != null) ? moment(grant.endDate).format('MM/DD/YYYY') : null;
                         });
                     }
                   });
               }
           });
       };

       $scope.setStartDate = function(grantee) {
           if (grantee.grantStart) {
               grantee.grantStart = null;
           }
           else {
               grantee.grantStart = moment().format('MM/DD/YYYY');
           }
       };

       $scope.setStartDate = function(grantee) {
           if (grantee.grantEnd) {
               grantee.grantEnd = null;
           }
           else {
               grantee.grantEnd = moment().format('MM/DD/YYYY');
           }
       };

       $scope.reset = function() {
           $scope.state.selectedGrantee = null;
           $scope.state.grantees = {};
           $scope.init();
       };

       $scope.init();
    }]);
