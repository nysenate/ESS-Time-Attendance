var essMyInfo = angular.module('essMyInfo');

essMyInfo.controller('EmpCheckHistoryCtrl',
    ['$scope', 'appProps', 'EmpCheckHistoryApi',
        function($scope, appProps, EmpCheckHistoryApi) {

            /** Map with deduction descriptions as keys.
             * Map is used instead of array for faster look ups.
             * If key is defined, at least one paycheck has that deduction. */
            $scope.deductionSet = {};

            $scope.checkHistory = {
                searching: false,
                recordYears: null,
                year: null
            };

            $scope.ytd = {
                gross: 0,
                directDeposit: 0,
                check: 0
            };

            $scope.init = function() {
                $scope.checkHistory.recordYears = appProps.empActiveYears;
                $scope.checkHistory.year = $scope.checkHistory.recordYears[$scope.checkHistory.recordYears.length - 1];
                $scope.getRecords();
            };

            $scope.getRecords = function() {
                $scope.checkHistory.searching = true;
                $scope.paychecks = [];
                var empId = appProps.user.employeeId;
                var params = {
                    empId: empId,
                    year: $scope.checkHistory.year.toString()
                };
                EmpCheckHistoryApi.get(params, function(response) {
                    $scope.paychecks = response.paychecks;
                    initializeYtdValues(response.paychecks);
                    processDeductions(response.paychecks);
                    $scope.checkHistory.searching = false;
                }, function(response) {
                    $scope.checkHistory.searching = false;
                    // todo error handling
                })
            };

            function initializeYtdValues(paychecks) {
                for (var i = 0; i < paychecks.length; i++) {
                    var paycheck = paychecks[i];
                    $scope.ytd.gross += paycheck.grossIncome;
                    $scope.ytd.directDeposit += paycheck.directDepositAmount;
                    $scope.ytd.check += paycheck.checkAmount;
                }
            }

             function processDeductions(paychecks) {
                for (var i = 0; i < paychecks.length; i++) {
                    var deductionMap = paychecks[i].deductions;
                    for (var key in deductionMap) {
                        if (deductionMap.hasOwnProperty(key)) {
                            addToDeductionSet(deductionMap[key]);
                            addToYtdValue(deductionMap[key]);
                        }
                    }
                }
            }

            /** Must have a set of all deductions to display in table;
             * if a deduction only occurs in one paycheck we still need a column for it. */
            function addToDeductionSet(deduction) {
                if (!$scope.deductionSet.hasOwnProperty(deduction.description)) {
                    $scope.deductionSet[deduction.description] = true;
                }
            }

            function addToYtdValue(deduction) {
                if ($scope.ytd[deduction.description]) {
                    $scope.ytd[deduction.description] += deduction.amount;
                } else {
                    $scope.ytd[deduction.description] = deduction.amount;
                }
            }

            $scope.init();
        }
    ]
);
