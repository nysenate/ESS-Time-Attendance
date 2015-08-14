<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
    <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
    <jsp:body>
        <ess:ts-assets/>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/nav/home.js"></script>

        <!-- Time Entry -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/record/record-directives.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/record/record-utils.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/record/record-entry-ctrl.js"></script>
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/record/record-history-ctrl.js"></script>

        <!-- Time Off Requests -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/timeoff/new-request-ctrl.js"></script>

        <!-- Pay Period Viewer -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/period/pay-period-view-ctrl.js"></script>

        <!-- Accruals -->
        <script type="text/javascript" src="${ctxPath}/assets/js/src/time/accrual/accrual-history-ctrl.js"></script>

    </jsp:body>
</ess-layout:head>

<ess-layout:body>
    <jsp:body>
        <base href="/" />
        <ess-component-nav:top-nav activeTopic="time"/>
        <section class="content-wrapper" ng-controller="MainCtrl as main">
            <ess-component-nav:time-nav/>
            <div class="view-animate-container">
                <div ng-view class="view-animate"></div>
            </div>
        </section>
    </jsp:body>
</ess-layout:body>