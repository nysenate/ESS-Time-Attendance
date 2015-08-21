<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmpTransactionHistoryCtrl">
  <div class="content-container content-controls">
    <p class="content-info">Show Transactions for Year &nbsp;
      <select ng-model="state.selectedYear" ng-change="getTransRecords(state.selectedYear)"
              ng-options="year for year in state.activeYears">
      </select>
    </p>
  </div>

  <div class="content-container content-controls">
      <p class="content-info">Filter By Transaction Type &nbsp;
          <select ng-model="state.filterCodes" ng-change="getTransRecords(state.selectedYear)">
              <option value="">Show All</option>
              <option value="APP,RTP">Initial</option>
              <option value="CHK,LEG">Address</option>
              <option value="MAR">Marital Status</option>
              <option value="MIN">Minimum Total Hours</option>
              <option value="TYP">Payroll Type</option>
              <option value="PHO">Phone Number</option>
              <option value="SAL">Salary Change</option>
              <option value="SUP">Supervisor Change</option>
          </select>
      </p>
  </div>

  <div class="padding-10">
      <ess-notification ng-show="state.transactions[state.selectedYear] === false" level="info"
                        message="No transactions found with the given filters."></ess-notification>
      <div ng-repeat="(date,txArr) in state.transactions[state.selectedYear]">
        <h2>Effective {{date | moment:'ll'}}</h2>
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