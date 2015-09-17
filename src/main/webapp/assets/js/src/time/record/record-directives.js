var essApp = angular.module('ess');

/** --- Filters --- */

essApp.filter('timeRecordStatus', function () {
    var statusDispMap = {
        NOT_SUBMITTED: "Not Submitted",
        SUBMITTED: "Submitted",
        DISAPPROVED: "Supervisor Disapproved",
        APPROVED: "Supervisor Approved",
        DISAPPROVED_PERSONNEL: "Personnel Disapproved",
        SUBMITTED_PERSONNEL: "Submitted Personnel",
        APPROVED_PERSONNEL: "Personnel Approved"
    };
    return function (status) {
        if (statusDispMap.hasOwnProperty(status)) {
            return statusDispMap[status];
        }
        return status + "?!";
    };
});

// Returns a display label for the given misc leave id
essApp.filter('miscLeave', ['appProps', function (appProps) {
    return function (miscLeave, defaultLabel) {
        if (appProps.miscLeaves.hasOwnProperty(miscLeave)) {
            return appProps.miscLeaves[miscLeave];
        }
        if (!miscLeave) {
            return defaultLabel ? defaultLabel : '--';
        }
        return miscLeave + "?!";
    };
}]);

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

/**
 * A table that displays details for a specific time record
 */
essApp.directive('recordDetails', ['appProps', function (appProps) {
    return {
        scope: {
            record: '='
        },
        templateUrl: appProps.ctxPath + '/template/time/record/details'
    };
}]);

essApp.directive('recordDetailModal', ['modals', function (modals) {
    return {
        template: '<div class="record-detail-modal" record-details record="record" employee="employee"></div>',
        link: function ($scope, $elem, $attrs) {
            var params = modals.params();
            $scope.record = params.record;
            $scope.employee = params.employee;
        }
    }
}]);

