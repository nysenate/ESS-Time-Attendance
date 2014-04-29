angular.module('essMyInfo', []);
angular.module('essTime', []);
angular.module('essPayroll', []);

var essApp = angular.module('ess', ['ngRoute', 'ngAnimate', 'essMyInfo', 'essTime', 'essPayroll']);

/** Transfers properties stored on the global window var into the root module. */
essApp.constant('appProps', {
    ctxPath: globalProps.ctxPath,
    runtimeLevel: globalProps.runtimeLevel,
    loginUrl: globalProps.loginUrl
});