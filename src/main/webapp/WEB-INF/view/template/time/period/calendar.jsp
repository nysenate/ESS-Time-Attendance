<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="PayPeriodCalendarCtrl">
    <div class="content-container content-controls">
        <p class="content-info">
            Display calendar for year &nbsp;
            <select ng-model="state.year" ng-options="year for year in yearList"></select>
        </p>
    </div>

    <div class="content-container pay-period-cal-container">
        <h1 class="teal">Pay Period Calendar {{state.year}}</h1>
        <div class="content-info legend-container">
            <div class="legend-block" style="background:#B8E3EB;">&nbsp;</div>Pay Period End Date
            <div class="legend-block" style="background:#d4ff60;">&nbsp;</div>Holiday
        </div>
        <div class="pay-period-cal">
            <div ng-repeat="(i, month) in months" class="pay-period-month">
                <div datepicker
                     step-months="0" default-date="{{month}}" inline="false"
                     before-show-day="periodHighlight()"></div>
            </div>
        </div>
    </div>

    <div class="grid margin-top-20 hidden">
        <div class="col-1-2">
            <div class="content-container padding-10">
                <h1 class="content-info">Pay Periods during {{state.year}}</h1>
                <table class="simple-table">
                    <thead>
                    <tr>
                        <th>Pay Period Number</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="period in periods">
                        <td>{{period.payPeriodNum}}</td>
                        <td>{{period.startDate | moment:'MMMM D'}}</td>
                        <td>{{period.endDate | moment:'MMMM D'}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="col-1-2" style="padding-right: 2px;">
            <div class="content-container padding-10">
                <h1 class="content-info">Holidays during {{state.year}}</h1>
                <table class="simple-table">
                    <thead>
                    <tr>
                        <th>Holiday</th>
                        <th>Official</th>
                        <th>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="holiday in holidays">
                        <td>{{holiday.name}}</td>
                        <td><span ng-if="!holiday.unofficial">Yes</span><span ng-if="holiday.unofficial">No</span></td>
                        <td>{{holiday.date | moment:'MMMM D'}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>