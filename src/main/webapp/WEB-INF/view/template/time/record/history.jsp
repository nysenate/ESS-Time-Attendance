<%@ page import="gov.nysenate.seta.model.payroll.MiscLeaveType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String miscLeaveMapJson = MiscLeaveType.getJsonLabels();
%>

<div ng-controller="RecordParentCtrl" ng-init='init(<%= miscLeaveMapJson%>)'>
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
      <table id="attendance-active-table" class="ess-table attendance-listing-table">
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
            <td>{{record.totals.work}}</td>
            <td>{{record.totals.holiday}}</td>
            <td>{{record.totals.vac}}</td>
            <td>{{record.totals.personal}}</td>
            <td>{{record.totals.sickEmp}}</td>
            <td>{{record.totals.sickFam}}</td>
            <td>{{record.totals.misc}}</td>
            <td>{{record.totals.total}}</td>
            <td><a href="${ctxPath}/time/record/entry" class="action-link">Edit</a></td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="content-container" ng-show="records.other.length > 0">
      <h1>Historical Attendance Records</h1>

      <p class="content-info" style="">Time records that have been submitted for pay periods during {{year}} are listed
        in the table below.<br/>You can view details about each pay period by clicking the 'View Details' link to the
        right.</p>
      <table id="attendance-history-table" class="ess-table attendance-listing-table">
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
            <td>{{record.totals.work}}</td>
            <td>{{record.totals.holiday}}</td>
            <td>{{record.totals.vac}}</td>
            <td>{{record.totals.personal}}</td>
            <td>{{record.totals.sickEmp}}</td>
            <td>{{record.totals.sickFam}}</td>
            <td>{{record.totals.misc}}</td>
            <td>{{record.totals.total}}</td>
            <td><a class="action-link">View Details</a></td>
          </tr>
          <tr style="border-top:2px solid teal;">
            <td colspan="2"></td>
            <td><strong>Annual Totals</strong></td>
            <td><strong>{{annualTotals.work}}</strong></td>
            <td><strong>{{annualTotals.holiday}}</strong></td>
            <td><strong>{{annualTotals.vac}}</strong></td>
            <td><strong>{{annualTotals.personal}}</strong></td>
            <td><strong>{{annualTotals.sickEmp}}</strong></td>
            <td><strong>{{annualTotals.sickFam}}</strong></td>
            <td><strong>{{annualTotals.misc}}</strong></td>
            <td><strong>{{annualTotals.total}}</strong></td>
            <td></td>
          </tr>
        </tbody>
      </table>

      <div id="dialog" title="Attendance Details">
        <p class="content-info" style="margin-bottom:0;">The time entries for pay period X are displayed in the table
          below.</p>
        <table class="attendance-entry-sub-table ess-table">
          <thead>
            <tr>
              <th>Day</th>
              <th>Date</th>
              <th>Work</th>
              <th>Holiday</th>
              <th>Vacation</th>
              <th>Personal</th>
              <th>Sick Emp</th>
              <th>Sick Fam</th>
              <th>Misc</th>
              <th>Misc Type</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Fri</td>
              <td>1/30</td>
              <td>7</td>
              <td>0</td>
              <td>0</td>
              <td>0</td>
              <td>0</td>
              <td>0</td>
              <td>0</td>
              <td>--</td>
              <td>7</td>
            </tr>
          </tbody>
        </table>
      </div>

      <script>
        $("#dialog").dialog({
          autoOpen: false,
          width: '620px',
          modal: true,
          buttons: {
            Done: function () {
              $(this).dialog("close");
            }
          }
        });

        $("#attendance-history-table .action-link").click(function (e) {
          $("#dialog").dialog("open");
        });
      </script>
    </div>
  </div>
</div>
