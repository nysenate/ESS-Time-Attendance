<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<section class="left-nav" ess-navigation>
    <ess-component-nav:nav-header topicTitle="My Info Menu"/>
    <h3 class="main-topic">My Profile</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/time/record/entry">Enter Time Record</a></li>
        <!--<li class="sub-topic"><a href="${ctxPath}/time/timeoff/request">Time Off Requests</a></li>-->
        <li class="sub-topic"><a href="${ctxPath}/time/record/history">Attendance History</a></li>
        <li class="sub-topic"><a href="${ctxPath}/time/period/calendar">Pay Period Calendar</a></li>
    </ul>
    <h3 class="main-topic">My Personal Information</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/time/accrual/history">Accrual History</a></li>
        <li class="sub-topic"><a href="${ctxPath}/time/accrual/projections">Accrual Projections</a></li>
    </ul>
</section>