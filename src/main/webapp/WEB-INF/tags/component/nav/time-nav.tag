<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<section class="left-nav" ess-navigation>
    <h3 class="main-topic">Home</h3>
    <ul class="sub-topic-list">
        <li><a href="${ctxPath}/ui/record/entry">Time Dashboard</a></li>
    </ul>
    <h3 class="main-topic">My Attendance</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/ui/record/entry">Enter Time Record</a></li>
        <li class="sub-topic"><a href="${ctxPath}/ui/record/timeoff">Request Time Off</a></li>
        <li class="sub-topic"><a href="">View Attendance History</a></li>
        <li class="sub-topic"><a href="">View Attendance Periods</a></li>
    </ul>
    <h3 class="main-topic">Manage Employees</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/ui/record/manage">Review Time Records</a></li>
        <li class="sub-topic"><a href="">Review Time Off Requests</a></li>
        <li class="sub-topic"><a href="">Send Notifications</a></li>
    </ul>
    <h3 class="main-topic">Preferences</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/ui/time/emailprefs">Email Reminders</a></li>
    </ul>
</section>