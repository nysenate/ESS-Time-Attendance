var essApp = angular.module('ess');

essApp.service('modals', ['$rootScope', '$q', function($rootScope, $q) {

    // State of the active modal instance
    var modal = {
        deferred: null,
        params: null
    };

    return { // exposed methods
        open: open,
        params: params,
        reject: reject,
        resolve: resolve
    };

    /** --- Public Methods --- */

    function open(type, params, pipeResponse) {
        var prevDeferred = modal.deferred;

        modal.deferred = $q.defer();
        modal.params = params;

        if (prevDeferred && pipeResponse) {
            modal.deferred.promise
                .then(prevDeferred.resolve, prevDeferred.reject)
        } else if (prevDeferred) {
            prevDeferred.reject();
        }

        $rootScope.$emit("modals.open", type);
        return modal.deferred.promise;
    }

    function params() {
        return modal.params || {};
    }

    function reject(reason) {
        if (!modal.deferred) {
            return;
        }

        modal.deferred.reject(reason);
        modal.deferred = modal.params = null;
        console.log('rejecting modal', reason);
        $rootScope.$emit("modals.close");
    }

    function resolve(response) {
        if (!modal.deferred) {
            return;
        }

        modal.deferred.resolve(response);
        modal.deferred = modal.params = null;
        $rootScope.$emit("modals.close");
    }

}]);