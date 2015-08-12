angular.module('essCore', []);
angular.module('essApi', ['essCore']);

angular.module('essMyInfo', ['essApi']);
angular.module('essTime', ['essApi']);
angular.module('essPayroll', ['essApi']);

var essApp = angular.module('ess', ['ngRoute', 'ngResource', 'ngAnimate', 'essMyInfo', 'essTime', 'essPayroll',
                                    'floatThead']);

/** Transfers properties stored on the global window var into the root module. */
angular.module('essCore').constant('appProps', globalProps);