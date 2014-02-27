var essApp = angular.module('ess');

/**
 * Sets up URL routing for the angular app. When a route is matched, the template
 * is loaded via the templateUrl, bound to the associated controller, and rendered
 * in an ngView element on the main page.
 *
 * We use angular routes because it allows for url linking in a single page app.
 *
 * {@link http://docs.angularjs.org/api/ngRoute.$route}
 */
essApp.config(function($routeProvider, $locationProvider) {
    var ctxPath = globalProps.ctxPath;

    /** Dashboard */
    $routeProvider.when(ctxPath + '/ui/dashboard/profile', {
        templateUrl: ctxPath + '/template/dashboard/profile',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/ui/dashboard/preferences', {
        templateUrl: ctxPath + '/template/dashboard/preferences',
        controller: 'TestCtrl'
    });

    /** Time Record */
    $routeProvider.when(ctxPath + '/ui/record/entry', {
        templateUrl: ctxPath + '/template/record/entry',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/ui/record/history', {
        templateUrl: ctxPath + '/template/record/history',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/ui/record/timeoff', {
        templateUrl: ctxPath + '/template/record/timeoff',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/ui/record/manage', {
        templateUrl: ctxPath + '/template/record/manage',
        controller: 'TestCtrl'
    });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
});