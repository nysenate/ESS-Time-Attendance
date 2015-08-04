var essApp = angular.module('ess');

essApp.filter('moment', ['$filter', function($filter) {
    return function(input, format, defaultVal) {
        if (input) {
            return moment(input).format(format);
        }
        else {
            return (typeof defaultVal !== 'undefined') ? defaultVal : "--";
        }
    };
}]);

essApp.filter('momentFromNow', function () {
    return function(input, suffix, defaultVal) {
        suffix = suffix === true;
        if (input) {
            return moment(input).fromNow(suffix);
        }
        return (typeof defaultVal !== 'undefined') ? defaultVal : "--";
    };
});

essApp.filter('momentEquals', function () {
    return function (lhs, rhs, precision) {
        return moment(lhs).isSame(moment(rhs), precision);
    };
});