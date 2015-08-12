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
          <td><a class="action-link" ng-click="showDetails(record)">View Details</a></td>
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

      <div modal ng-show="subview" ng-switch="subview" class="modal-container">
        <div class="record-detail-modal" title="Attendance Details"
             ng-switch-when="details" ng-controller="RecordDetailsCtrl">
          <p class="content-info" style="margin-bottom:0;">
            The time entries for {{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}
            are displayed in the table below.
          </p>
          <table class="attendance-entry-sub-table ess-table">
            <thead>
              <tr>
                <th>Day</th>
                <th>Date</th>
                <th class="hour-col">Work</th>
                <th class="hour-col">Holiday</th>
                <th class="hour-col">Vacation</th>
                <th class="hour-col">Personal</th>
                <th class="hour-col">Sick Emp</th>
                <th class="hour-col">Sick Fam</th>
                <th class="hour-col">Misc</th>
                <th>Misc Type</th>
                <th class="hour-col">Total</th>
              </tr>
            </thead>
            <tbody>
            <tr ng-repeat="entry in record.timeEntries">
              <td>{{entry.date | moment:'dddd'}}</td>
              <td>{{entry.date | moment:'MMMM Do'}}</td>
              <td>{{entry.workHours || '--'}}</td>
              <td>{{entry.holidayHours || '--'}}</td>
              <td>{{entry.vacationHours || '--'}}</td>
              <td>{{entry.personalHours || '--'}}</td>
              <td>{{entry.sickEmpHours || '--'}}</td>
              <td>{{entry.sickFamHours || '--'}}</td>
              <td>{{entry.miscHours || '--'}}</td>
              <td>{{miscLeaves[entry] || '--'}}</td>
              <td>{{entry.total}}</td>
            </tr>
            <tr class="time-totals-row">
              <td></td>
              <td><strong>Record Totals</strong></td>
              <td><strong>{{record.totals.work}}</strong></td>
              <td><strong>{{record.totals.holiday}}</strong></td>
              <td><strong>{{record.totals.vac}}</strong></td>
              <td><strong>{{record.totals.personal}}</strong></td>
              <td><strong>{{record.totals.sickEmp}}</strong></td>
              <td><strong>{{record.totals.sickFam}}</strong></td>
              <td><strong>{{record.totals.misc}}</strong></td>
              <td></td>
              <td><strong>{{record.totals.total}}</strong></td>
            </tr>
            </tbody>
          </table>
        </div>
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
</div>
