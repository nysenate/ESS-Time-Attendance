<%@tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeTopic" required="true" description="A key indicating which nav topic should be active." %>

<header class="ess-top-header">
    <section class="left-header-area">
        <div class="nysslogo"></div>
        <section id="logoTextSection">
            <div id="logoText">
                <span id="subLogoText">NYSS </span>
                <span>ESS</span>
            </div>
        </section>
        <ul id="topNavList">
            <li id="dashboardLink" class="main-topic orange <c:if test='${activeTopic == "myinfo"}'>active</c:if>">
                <a target="_self" href="${ctxPath}/myinfo"><img class="nav-icon" src="${ctxPath}/assets/img/user.png"/>My Info</a>
            </li>
            <li id="timeAttendanceLink" class="main-topic teal <c:if test='${activeTopic == "time"}'>active</c:if>">
                <a target="_self" href="${ctxPath}/time"><img class="nav-icon" src="${ctxPath}/assets/img/20px-ffffff/clock.png"/>Time</a>
            </li>
            <li id="payrollLink" class="main-topic">
                <a target="_self"><img class="nav-icon" src="${ctxPath}/assets/img/20px-ffffff/dollar.png"/>Payroll</a>
            </li>
            <li id="helpLink" class="main-topic">
                <a target="_self"><img class="nav-icon" src="${ctxPath}/assets/img/20px-ffffff/question.png"/>Help</a>
            </li>
        </ul>
    </section>
    <section class="right-header-area">
        <section id="profileSection">
            <div id="headerProfileSquare">
                <div id="headerProfileImg"></div>
            </div>
        </section>
        <section id="logoutSection">
            <a target="_self" href="${ctxPath}/logout">
                <div class="logout-icon"></div>Sign Out
            </a>
        </section>
    </section>
</header>