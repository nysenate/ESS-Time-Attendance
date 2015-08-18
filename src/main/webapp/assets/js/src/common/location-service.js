angular.module('ess')
    .service('LocationService', ['$location', locationService]);

/**
 * A collection of utility functions that utilize $location
 */
function locationService($location) {

    return {
        setSearchParam: setSearchParam,
        getSearchParam: getSearchParam,
        clearSearchParams: clearSearchParams
    };

    /**
     * Sets the search param with 'paramName' to 'paramValue'
     * @param paramName - name of param to set
     * @param paramValue - new value for the param
     * @param condition - the param will be set to null if this is exactly false
     * @param replace - the new url will replace the previous history entry unless this is exactly false
     */
    function setSearchParam(paramName, paramValue, condition, replace) {
        var search = $location.search(paramName, (condition !== false) ? paramValue : null);
        if (replace !== false) {
            search.replace();
        }
    }

    /**
     * Gets the search param by the given name if it exists
     */
    function getSearchParam(paramName) {
        return $location.search()[paramName];
    }

    /**
     * Clears all search params
     */
    function clearSearchParams() {
        $location.search({});
    }

}