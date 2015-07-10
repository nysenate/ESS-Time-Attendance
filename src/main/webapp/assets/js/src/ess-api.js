var essApi = angular.module('essApi');

/** --- Pay Period API --- */

essApi.factory('PayPeriodApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/periods/:periodType/dates/:startDate/:endDate');
}]);

/** --- Holiday API --- */

essApi.factory('HolidaysDuringYearApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/holidays/year/:year');
}]);

essApi.factory('HolidaysDuringDatesApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/holidays/dates/:startDate/:endDate');
}]);