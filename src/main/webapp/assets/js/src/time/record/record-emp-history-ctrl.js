var essTime = angular.module('essTime');

essTime.controller('EmpRecordHistoryCtrl', ['$scope', 'appProps', 'TimeRecordsApi', 'SupervisorEmployeesApi',
                                            'modals', 'RecordUtils',
    function ($scope, appProps, TimeRecordsApi, SupervisorEmployeesApi, modals, recordUtils) {

        $scope.state = {
            supId: appProps.user.employeeId,
            searching: false,
            selectedEmp: null,
            primaryEmps: [],
            secondaryEmps: []
        };

        $scope.getEmployeeGroups = function(supId, fromDate, toDate) {
            var fromDateMoment = (fromDate) ? moment(fromDate) : moment().subtract(2, 'years');
            var toDateMoment = (toDate) ? moment(toDate) : moment();
            $scope.state.searching = true;
            SupervisorEmployeesApi.get({
                supId: supId,
                fromDate: fromDateMoment.format('YYYY-MM-DD'),
                toDate: toDateMoment.format('YYYY-MM-DD')
            }, function(resp) {
                if (resp.success == true) {
                    $scope.state.primaryEmps = resp.result.primaryEmployees.sort(function(a,b) {
                        return a.empLastName.localeCompare(b.empLastName)});
                    angular.forEach($scope.state.primaryEmps, function(emp) {
                        var startMoment = moment(emp.supStartDate);
                        var endMoment = (emp.supEndDate) ? moment(emp.supEndDate) : moment();
                        emp.group = 'Direct employees';
                        emp.dropDownLabel = emp.empLastName + ' (' + startMoment.format('MMM YYYY') + ' - ' +
                                                                     endMoment.format('MMM YYYY') + ')';
                    });
                    $scope.state.secondaryEmps = resp.result.empOverrideEmployees;
                }
                $scope.state.searching = false;
            }, function(resp) {
                $scope.state.searching = false;
            });
        };

        $scope.getAttendanceRecords = function(empId) {

        };

        $scope.init = function() {
            $scope.getEmployeeGroups($scope.state.supId);
        }();
    }]
);