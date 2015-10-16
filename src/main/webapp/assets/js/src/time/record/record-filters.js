var essTime = angular.module('essTime');

/** --- Filters --- */

essTime.filter('timeRecordStatus', function () {
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
essTime.filter('miscLeave', ['appProps', function (appProps) {
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

/**
 * Colors a number based on whether it's positive or negative to provide a
 * visual cue.
 *
 * Example,
 * given 7 -> +7 (green)
 * given -3 -> -3 (red)
 * given 0 -> 0 (default color)
 */
essTime.filter('hoursDiffHighlighter', ['$sce', function($sce) {
    return function (hours) {
        var color = '#0e4e5a';
        var sign = '';
        if (hours > 0) {
            color = '#09BB05';
            sign = '+';
        }
        else if (hours < 0) {
            color = '#BB0505';
        }
        return $sce.trustAsHtml('<span style="color:' + color + '">' + sign + hours + '</span>');
    }
}]);
