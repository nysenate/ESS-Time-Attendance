<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
  <jsp:body>
    <ess:ts-assets/>
    <script type="text/javascript" src="${ctxPath}/assets/js/src/help/help.js"></script>
  </jsp:body>
</ess-layout:head>

<ess-layout:body>
  <jsp:body>
    <base href="/" />
    <ess-component-nav:top-nav activeTopic="help"/>
    <section class="content-wrapper" ng-controller="HelpMainCtrl">
      <ess-component-nav:help-nav/>
      <div class="view-animate-container">
        <div ng-view class="view-animate"></div>
      </div>
    </section>
  </jsp:body>
</ess-layout:body>