<%@tag description="ESS Base Template" pageEncoding="UTF-8"%>
<%@attribute name="pageTitle" fragment="true" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html id="ng-app" ng-app="ess">
<head>
    <title><jsp:invoke fragment="pageTitle"/></title>
    <script>
        window.globalProps = {
            ctxPath: '${ctxPath}',
            runtimeLevel: '${runtimeLevel}',
            loginUrl: '${loginUrl}'
        };
        <c:if test="${not empty principalJson}">
            window.globalProps.user = ${principalJson};
        </c:if>
    </script>
    <jsp:doBody/>
</head>