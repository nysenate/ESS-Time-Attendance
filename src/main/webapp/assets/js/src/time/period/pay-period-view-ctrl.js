var essTime = angular.module('essTime');

essTime.controller('PayPeriodViewCtrl', ['$scope', '$http', function($scope, $http){
    $scope.year = 2014;
    $scope.months = [
        "1/1/2014", "2/1/2014", "3/1/2014", "4/1/2014", "5/1/2014", "6/1/2014",
        "7/1/2014", "8/1/2014", "9/1/2014", "10/1/2014", "11/1/2014", "12/1/2014"
    ];

    $scope.periods = [
        "1/1/2014", "1/15/2014", "1/29/2014",
        "2/12/2014", "2/26/2014",
        "3/12/2014", "3/26/2014",
        "4/9/2014", "4/23/2014",
        "5/7/2014", "5/21/2014",
        "6/11/2014", "6/25/2014",
        "7/2/2014", "7/16/2014", "7/30/2014",
        "8/13/2014", "8/27/2014",
        "9/10/2014", "9/24/2014",
        "10/8/2014", "10/22/2014",
        "11/5/2014","11/19/2014",
        "12/3/2014", "12/17/2014", "12/31/2014"
    ];

    $scope.holidays = [
        "1/1/2014", "1/21/2014", "2/18/2014", "5/27/2014", "7/4/2014", "9/2/2014", "10/14/2014", "11/05/2014",
        "11/11/2014", "11/28/2014", "11/29/2014", "12/25/2014"
    ];

    /**
     * Method to call for 'beforeShowDate' on the datepicker. This will mark the pay period
     * dates and other relevant dates with a specific class so that they are highlighted.
     * @returns {Function}
     */
    $scope.periodHighlight = function() {
        return function(date) {
            var dt = new Date(date.setHours(0,0,0,0));
            var today = new Date(new Date().setHours(0,0,0,0));
            var cssClass = "";
            var toolTip = "";
            var isCurrDay = false;
            var isWeekend = false;
            var isPeriodEndDate = false;
            var isHoliday = false;
            if (dt.getDay() == 6 || dt.getDay() == 0) {
                isWeekend = true;
            }
            if (dt.getTime() == today.getTime()) {
                isCurrDay = true;
            }
            else {
                $.each($scope.holidays, function(i,v){
                    if (new Date(v).getTime() == date.getTime()) {
                        isHoliday = true;
                    }
                });

                $.each($scope.periods, function(i,v){
                    if (new Date(v).getTime() == date.getTime()) {
                        isPeriodEndDate = true;
                    }
                });
            }
            if (isWeekend) {
                cssClass = 'weekend-date';
            }
            else if (isHoliday && isPeriodEndDate) {
                cssClass = 'holiday-and-pay-period-date';
            }
            else {
                if (isHoliday) {
                    cssClass = 'holiday-date';
                    toolTip += 'Holiday';
                }
                if (isPeriodEndDate) {
                    cssClass = 'pay-period-end-date';
                    toolTip += 'Pay Period End Date';
                }
            }
            if (isCurrDay) {
                cssClass += ' current-date';
            }
            return [false, cssClass, toolTip];
        }
    }
}]);