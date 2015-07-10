var essTime = angular.module('essTime');

essTime.controller('PayPeriodCalendarCtrl',
    ['$scope', '$http', 'PayPeriodByYearApi', 'HolidaysDuringYearApi',
    function($scope, $http, PayPeriodByYearApi, HolidaysDuringYearApi) {

    $scope.state = {
        year: moment().year()
    };
    $scope.yearList = Array.apply(0, Array(10)).map(function (x, y) { return (($scope.state.year + 2) - y - 1); });
    $scope.months = [];
    $scope.periods = [];

    $scope.getPayPeriods = function(year, callback) {
        $scope.periodResp = PayPeriodByYearApi.get({
            periodType: 'AF', year: year
        }, function() {
            $scope.periods = $scope.periodResp.result.items;
            if (callback) callback();
        });
    };

    $scope.getHolidays = function(year, callback) {
        $scope.holidaysResp = HolidaysDuringYearApi.get({year: year}, function() {
            $scope.holidays = $scope.holidaysResp.result.items;
            if (callback) callback();
        });
    };

    $scope.generateMonths = function(year) {
        $scope.months = [];
        for (var i = 0; i < 12; i++) {
            $scope.months.push(moment().year(year).month(i).format('M/D/YYYY'));
        }
    };

    $scope.$watch('state.year', function(year, oldYear) {
        $scope.getPayPeriods(year, function() {
            $scope.getHolidays(year, function() {
                $scope.generateMonths(year);
            });
        });
    });

    //$scope.init = function() {
        //$scope.getPayPeriods($scope.state.year, function() {
        //    $scope.generateMonths($scope.state.year);
        //});
    //}();

    /**
     * Method to call for 'beforeShowDate' on the datepicker. This will mark the pay period
     * dates and other relevant dates with a specific class so that they are highlighted.
     * @returns {Function}
     *
     * TODO: Could probably optimize this a bit so it doesn't iterate so much.
     */
    $scope.periodHighlight = function() {
        return function(date) {
            var cssClasses = [];
            var toolTips = [];

            var mDate = moment(date).startOf('day');
            var mCurrent = moment().startOf('day');

            if (mDate.isSame(mCurrent)) {
                cssClasses.push('current-date');
            }
            if (mDate.day() == 6 || mDate.day() == 0) {
                cssClasses.push('weekend-date');
            }
            else {
                $.each($scope.holidays, function(i,v) {
                    if (mDate.isSame(moment(v.date))) {
                        toolTips.push(v.name);
                        cssClasses.push('holiday-date');
                    }
                });
                $.each($scope.periods, function(i,v) {
                    if (mDate.isSame(moment(v.endDate))) {
                        toolTips.push('Last Day of Pay Period ' + v.payPeriodNum);
                        cssClasses.push('pay-period-end-date');
                    }
                });
            }
            return [false, cssClasses.join(' '), toolTips.join(' \\ ')];
        }
    }
}]);