<%@tag description="ESS Base Template" pageEncoding="UTF-8"%>
<%@attribute name="pageTitle" fragment="true" required="true" %>

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
    </script>
    <jsp:doBody/>
</head>