<%@tag description="Includes common assets for the Timesheets app based on the runtime level" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% /* Left Navigation Menu */ %>

<nav id="leftNav" ess-navigation>
    <ul id="mainNavList">
        <li id="dashboardLink" class="main-topic">
            <a><div class="nav-icon"></div>Dashboard</a>
        </li>
        <li>
            <ul class="sub-topic-list">
                <li class="sub-topic"><a href="${ctxPath}/ui/dash/profile">My Profile</a></li>
                <li class="sub-topic"><a href="${ctxPath}/ui/dash/preferences">Preferences</a></li>
            </ul>
        </li>
        <li id="timeAttendanceLink" class="main-topic">
            <a><div class="nav-icon"></div>Time and Attendance</a>
        </li>
        <li>
            <ul class="sub-topic-list">
                <li class="sub-topic"><a href="${ctxPath}/ui/record/entry">Time Record Entry</a></li>
                <li class="sub-topic"><a href="${ctxPath}/ui/record/history">Time Record History</a></li>
                <li class="sub-topic"><a href="${ctxPath}/ui/record/request">Time Off Requests</a></li>
                <li class="sub-topic"><a href="${ctxPath}/ui/record/manage">Manage Employee Records</a></li>
            </ul>
        </li>
        <li id="productivityLink" class="main-topic">
            <a><div class="nav-icon"></div>Paychecks</a>
        </li>
        <li>
            <ul class="sub-topic-list">
                <li class="sub-topic"><a href="${ctxPath}/ui/productivity/tracker">Paycheck History</a></li>
            </ul>
        </li>
        <li id="helpLink" class="main-topic">
            <a><div class="nav-icon"></div>Help</a>
        </li>
        <li>
            <ul class="sub-topic-list">
                <li class="sub-topic"><a href="${ctxPath}/ui/help/docs">Application Docs</a></li>
                <li class="sub-topic"><a href="${ctxPath}/ui/help/contact">Contact STS/Personnel</a></li>
            </ul>
        </li>
    </ul>
</nav>