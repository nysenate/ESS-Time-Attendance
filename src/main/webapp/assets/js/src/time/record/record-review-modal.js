var essApp = angular.module('ess');

essApp.directive('recordReviewModal', ['appProps', 'modals', 'LocationService',
function (appProps, modals, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-review-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {

        $scope.iSelectedRecord = 0;

        $scope.records = modals.params().records;
        $scope.empInfos = modals.params().empInfos;

        // Settings for floating the time entry table heading
        $scope.floatTheadOpts = {
            scrollingTop: 47
        };

        /**
         *  Records are categorized under approved or disapproved, keyed by time record id
         */
        var approved = {};
        var disapproved = {};

        /** --- Display Methods --- */

        /**
         * Resolves the modal, returning the records that were selected as approved/disapproved
         */
        $scope.resolve = function () {
            modals.resolve({
                approved: approved,
                disapproved: disapproved
            });
        };

        /**
         * Closes the modal without the intention of submitting records
         */
        $scope.close = modals.reject;

        /**
         * Removes the selected record from both the approved and disapproved categories
         */
        $scope.cancelRecord = function() {
            var record = $scope.records[$scope.iSelectedRecord];
            delete approved[record.timeRecordId];
            delete disapproved[record.timeRecordId];
        };

        /**
         * Adds the selected record to the 'approved' category
         */
        $scope.approveRecord = function () {
            var record = $scope.records[$scope.iSelectedRecord];
            $scope.cancelRecord();
            approved[record.timeRecordId] = record;
            selectNextPendingRecord();
        };

        /**
         * Opens a new modal to add rejection remarks for the selected record
         * If the modal is resolved, the record is added to the rejected category
         */
        $scope.rejectRecord = function () {
            var record = $scope.records[$scope.iSelectedRecord];
            modals.open('record-review-reject', {record: record})
                .then(function rejected(reasons) {
                    $scope.cancelRecord(record);
                    record.rejectionRemarks = reasons;
                    disapproved[record.timeRecordId] = record;
                    selectNextPendingRecord();
                });
        };

        /** Sets the given index as the index of the selected record */
        $scope.selectRecord = function (index) {
            $scope.iSelectedRecord = index;
        };

        /**
         * Returns a string that indicates whether a record has been approved, disapproved or neither
         */
        $scope.getApprovalStatus = function(record) {
            if (record.timeRecordId in approved) {
                return 'approved';
            }
            if (record.timeRecordId in disapproved) {
                return 'disapproved';
            }
            return 'untouched';
        };

        $scope.submissionEmpty = function() {
            return Object.keys(approved).length === 0 && Object.keys(disapproved).length === 0;
        };

        /** --- Internal Methods --- */

        /**
         * Locates and selects the next pending record by searching after and then before the selected record;
         */
        function selectNextPendingRecord() {
            for (var i = 0; i < $scope.records.length; i++) {
                var iAdj = (i + $scope.iSelectedRecord) % $scope.records.length;
                if ($scope.getApprovalStatus($scope.records[iAdj]) === 'untouched') {
                    $scope.iSelectedRecord = iAdj;
                    locationService.scrollToId($scope.records[iAdj].timeRecordId);
                    return;
                }
            }
        }
    }
}]);

essApp.directive('recordReviewRejectModal', ['modals', function (modals){
    return {
        template:
            '<p class="content-info no-bottom-margin">Explain the reason for rejecting the time record.</p>' +
            '<textarea style="resize:none;margin:10px;width:375px;height:100px;" placeholder="Reason for rejection" ng-model="remarks" tabindex="1"></textarea>' +
            '<div style="padding:.4em;background:#eee;text-align: center;">' +
            '  <input type="button" class="reject-button" value="Reject Record" ng-click="resolve()"/>' +
            '  <input type="button" style="float:right;" class="neutral-button" value="Cancel" ng-click="cancel()"/>' +
            '</div>',
        link: function($scope, $elem, $attrs) {
            $scope.cancel = modals.reject;

            $scope.resolve = function() {
                modals.resolve($scope.remarks)
            };
        }
    };
}]);