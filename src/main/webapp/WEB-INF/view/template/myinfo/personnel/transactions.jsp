<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmpTransactionHistoryCtrl">
  <div class="content-container content-controls">
    <p class="content-info">Show Transactions for Year &nbsp;
      <select ng-model="state.selectedYear" ng-change="getTransRecords(state.selectedYear)"
              ng-options="year for year in state.activeYears">
      </select>
    </p>
  </div>

  <div class="padding-10">
      <ess-notification ng-show="!state.transactions[state.selectedYear]" level="info" message="No transactions found for this year."></ess-notification>
      <div ng-repeat="(date,txArr) in state.transactions[state.selectedYear]">
        <h2>{{date | moment:'ll'}}</h2>
        <hr/>
        <div class="tx-container" ng-repeat="tx in txArr">
            <h3 class="tx-heading">{{tx.transDesc}}</h3>
            <span class="tx-update-date">Last updated on {{tx.updateDate | moment: 'lll'}}</span>
            <div class="content-container padding-10">
                <table class="">
                    <tbody>
                    <tr ng-repeat="(k,v) in tx.valueMap">
                        <td style="width:200px;">{{k}}</td>
                        <td>{{v}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
  </div>
</section>