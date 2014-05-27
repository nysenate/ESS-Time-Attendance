<%@tag description="Left navigation menu for Time & Attendance screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>

<section class="left-nav" ess-navigation>
    <ess-component-nav:nav-header topicTitle="My Info Menu" colorClass="orange"/>
    <h3 class="main-topic">My Profile</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/myinfo/profile/summary">Summary</a></li>
    </ul>
    <h3 class="main-topic">My Personal Information</h3>
    <ul class="sub-topic-list">
        <li class="sub-topic"><a href="${ctxPath}/myinfo/"></a></li>
    </ul>
</section>