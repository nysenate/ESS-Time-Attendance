<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="PayPeriodViewCtrl" class="content-container pay-period-cal-container">
    <h1 class="teal">Pay Period Calendar</h1>
    <p class="content-info">
        Payroll Year &nbsp;
        <select>
            <option>2015</option>
            <option selected="selected">2014</option>
            <option>2013</option>
        </select>
    </p>
    <div class="legend-container">
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



