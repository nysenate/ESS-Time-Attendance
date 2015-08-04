var essApp = angular.module('ess');

/** --- Filters --- */

essApp.filter('timeRecordStatus', function () {
    var statusDispMap = {
        SUBMITTED: "Submitted",
        NOT_SUBMITTED: "Not Submitted",
        APPROVED: "Supervisor Approved",
        DISAPPROVED: "Supervisor Disapproved",
        SUBMITTED_PERSONNEL: "Personnel Approved",
        DISAPPROVED_PERSONNEL: "Personnel Disapproved"
    };
    return function (status) {
        if (statusDispMap.hasOwnProperty(status)) {
            return statusDispMap[status];
        }
        return status + "?!";
    };
});

/** --- Directives --- */

essApp.directive('timeRecordInput', [function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.on('focus', function(event){
                $(this).attr('type', 'number');
                $(this).parent().parent().addClass("active");
            });
            element.on('blur', function(event){
                $(this).attr('type', 'text');
                $(this).parent().parent().removeClass("active");
            });
        }
    }
}]);