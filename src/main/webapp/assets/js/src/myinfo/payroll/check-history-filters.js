var essMyInfo = angular.module('essMyInfo');

essMyInfo.filter('formatDeductionHeader', function() {
    return function(input, scope) {
        if (input !== null) {
            return input.replace(/\w\S*/g, function(txt) {
                txt = txt.replace(":", "");
                return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            });
        }
    }
});