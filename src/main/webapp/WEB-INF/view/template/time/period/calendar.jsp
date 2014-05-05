<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-container content-controls">
    <p class="content-info">
        Display calendar for year &nbsp;
        <select>
            <option>2015</option>
            <option selected="selected">2014</option>
            <option>2013</option>
        </select>
    </p>
</section>

<section ng-controller="PayPeriodViewCtrl" class="content-container pay-period-cal-container">
    <h1 class="teal">Pay Period Calendar 2014</h1>
    <div class="content-info legend-container">
        <div class="legend-block" style="border: 2px solid #006b80;">&nbsp;</div>Current Day
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
</section>



