/*! Login Page */
var essApp = angular.module('ess', ['ngRoute', 'ngAnimate']);

/** Transfers properties stored on the global window var into the root module. */
essApp.constant('appProps', {
    ctxPath: globalProps.ctxPath,
    runtimeLevel: globalProps.runtimeLevel,
    loginUrl: globalProps.loginUrl
});