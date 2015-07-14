var essApi = angular.module('essApi');

/** --- Pay Period API --- */

essApi.factory('PayPeriodByYearApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/periods/:periodType/year/:year');
}]);

/** --- Holiday API --- */

essApi.factory('HolidaysDuringYearApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/holidays/year/:year');
}]);

essApi.factory('HolidaysDuringDatesApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/holidays/dates/:startDate/:endDate');
}]);