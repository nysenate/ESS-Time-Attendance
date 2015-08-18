<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="RecordHistoryCtrl">
  <div class="content-container content-controls">
    <p class="content-info" style="margin-bottom:0;">
      View attendance records for year
      <select ng-model="year" ng-options="yearOpt for yearOpt in activeYears" ng-change="getRecords()"></select>
    </p>
  </div>

  <div class="content-container" ng-show="records.employee.length > 0">
    <h1>Active Attendance Records</h1>

    <p class="content-info">The following time records are in progress or awaiting submission.
      <br/>You can edit a record by clicking the 'Edit' link to
      the right.</p>
    <div class="padding-10">
      <table id="attendance-active-table" class="ess-table attendance-listing-table"
             float-thead="floatTheadOpts" ng-model="records.employee">
        <thead>
        <tr>
          <th>Date Range</th>
          <th>Pay Period</th>
          <th>Status</th>
          <th>Work</th>
          <th>Holiday</th>
          <th>Vacation</th>
          <th>Personal</th>
          <th>Sick Emp</th>
          <th>Sick Fam</th>
          <th>Misc</th>
          <th>Total</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="record in records.employee">
          <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
          <td>{{record.payPeriod.payPeriodNum}}</td>
          <td>{{record.recordStatus | timeRecordStatus}}</td>
          <td>{{record.totals.workHours}}</td>
          <td>{{record.totals.holidayHours}}</td>
          <td>{{record.totals.vacationHours}}</td>
          <td>{{record.totals.personalHours}}</td>
          <td>{{record.totals.sickEmpHours}}</td>
          <td>{{record.totals.sickFamHours}}</td>
          <td>{{record.totals.miscHours}}</td>
          <td>{{record.totals.total}}</td>
          <td><a ng-href="${ctxPath}/time/record/entry?record={{record.beginDate}}" class="action-link">Edit</a></td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div class="content-container" ng-show="records.other.length > 0">
    <h1>Historical Attendance Records</h1>

    <p class="content-info" style="">Time records that have been submitted for pay periods during {{year}} are listed
      in the table below.<br/>You can view details about each pay period by clicking the 'View Details' link to the
      right.</p>
    <div class="padding-10">
      <table id="attendance-history-table" class="ess-table attendance-listing-table"
             float-thead="floatTheadOpts" ng-model="records.other">
        <thead>
        <tr>
          <th>Date Range</th>
          <th>Pay Period</th>
          <th>Status</th>
          <th>Work</th>
          <th>Holiday</th>
          <th>Vacation</th>
          <th>Personal</th>
          <th>Sick Emp</th>
          <th>Sick Fam</th>
          <th>Misc</th>
          <th>Total</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="record in records.other">
          <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
          <td>{{record.payPeriod.payPeriodNum}}</td>
          <td>{{record.recordStatus | timeRecordStatus}}</td>
          <td>{{record.totals.workHours}}</td>
          <td>{{record.totals.holidayHours}}</td>
          <td>{{record.totals.vacationHours}}</td>
          <td>{{record.totals.personalHours}}</td>
          <td>{{record.totals.sickEmpHours}}</td>
          <td>{{record.totals.sickFamHours}}</td>
          <td>{{record.totals.miscHours}}</td>
          <td>{{record.totals.total}}</td>
          <td><a class="action-link" ng-click="showDetails(record)">View Details</a></td>
        </tr>
        <tr style="border-top:2px solid teal;">
          <td colspan="2"></td>
          <td><strong>Annual Totals</strong></td>
          <td><strong>{{annualTotals.workHours}}</strong></td>
          <td><strong>{{annualTotals.holidayHours}}</strong></td>
          <td><strong>{{annualTotals.vacationHours}}</strong></td>
          <td><strong>{{annualTotals.personalHours}}</strong></td>
          <td><strong>{{annualTotals.sickEmpHours}}</strong></td>
          <td><strong>{{annualTotals.sickFamHours}}</strong></td>
          <td><strong>{{annualTotals.miscHours}}</strong></td>
          <td><strong>{{annualTotals.total}}</strong></td>
          <td></td>
        </tr>
        </tbody>
      </table>
    </div>

    <div modal-container ng-show="subview" ng-switch="subview">
      <div record-detail-modal ng-switch-when="details"></div>
    </div>

    <%--<script>--%>
    <%--$("#dialog").dialog({--%>
    <%--autoOpen: false,--%>
    <%--width: '620px',--%>
    <%--modal: true,--%>
    <%--buttons: {--%>
    <%--Done: function () {--%>
    <%--$(this).dialog("close");--%>
    <%--}--%>
    <%--}--%>
    <%--});--%>

    <%--$("#attendance-history-table .action-link").click(function (e) {--%>
    <%--$("#dialog").dialog("open");--%>
    <%--});--%>
    <%--</script>--%>
  </div>
</div>
