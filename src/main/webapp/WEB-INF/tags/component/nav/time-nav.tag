<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<section class="left-nav" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Time And Attendance Menu" colorClass="teal"/>
    <p class="user-heading">Welcome, ${principal.getFullName()}</p>
    <h3 class="main-topic">My Attendance</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/time/record/entry">Enter Time Record</a></li>
        <!--<li class="sub-topic"><a href="${ctxPath}/time/timeoff/request">Time Off Requests</a></li>-->
        <li class="sub-topic"><a href="${ctxPath}/time/record/history">Attendance History</a></li>
        <li class="sub-topic"><a href="${ctxPath}/time/period/calendar">Pay Period Calendar</a></li>
    </ul>
    <h3 class="main-topic">My Accruals</h3>
        <ul class="sub-topic-list">
            <li class="sub-topic"><a href="${ctxPath}/time/accrual/history">Accrual History</a></li>
            <li class="sub-topic"><a href="${ctxPath}/time/accrual/projections">Accrual Projections</a></li>
        </ul>
    <h3 class="main-topic">Manage Employees</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/time/record/manage">Review Time Records</a></li>
        <li class="sub-topic"><a href="${ctxPath}/time/record/emphistory">Employee Record History</a></li>
        <!--<li class="sub-topic"><a href="${ctxPath}/time/timeoff/manage">Review Time Off Requests</a></li>-->
        <li class="sub-topic"><a href="${ctxPath}/time/record/grant">Grant Privileges</a></li>
        <li class="sub-topic"><a href="">Send Notifications</a></li>
    </ul>
    <h3 class="main-topic">Preferences</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/ui/time/emailprefs">Email Reminders</a></li>
    </ul>
</section>