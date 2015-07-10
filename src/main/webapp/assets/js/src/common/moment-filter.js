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