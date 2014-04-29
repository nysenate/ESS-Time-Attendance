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

    /** My Info */
    $routeProvider.when(ctxPath + '/ui/dashboard/profile', {
        templateUrl: ctxPath + '/template/dashboard/profile',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/ui/dashboard/preferences', {
        templateUrl: ctxPath + '/template/dashboard/preferences',
        controller: 'TestCtrl'
    });

    /** Time and Attendance */
    $routeProvider.when(ctxPath + '/time/record/entry', {
        templateUrl: ctxPath + '/template/time/record/entry',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/time/record/history', {
        templateUrl: ctxPath + '/template/time/record/history',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/time/timeoff/request', {
        templateUrl: ctxPath + '/template/time/timeoff/request',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/time/record/manage', {
        templateUrl: ctxPath + '/template/time/record/manage',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/time/period/calendar', {
        templateUrl: ctxPath + '/template/time/period/calendar',
        controller: 'TestCtrl'
    });

    $routeProvider.when(ctxPath + '/time/accrual/history', {
        templateUrl: ctxPath + '/template/time/accrual/history',
        controller: 'TestCtrl'
    });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
});

/**
 * Create a smooth fade transition for the ng-view.
 */
essApp.animation('.view-animate', function() {
    return {
        enter: function(element, done) {
            element.hide();
            element.delay(150).fadeIn(300, done);
            return function() {
                element.stop();
            }
        },
        leave: function(element, done) {
            element.fadeOut(100, done);
            return function() {
                element.stop();
            }
        }
    }
});