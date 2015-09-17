<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<h3 class="content-info" style="margin-bottom:0;">
  Time entries for {{record.employee.fullName}} from {{record.beginDate | moment:'l'}} to {{record.endDate | moment:'l'}}
</h3>
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
      <td>{{entry.date | moment:'ddd'}}</td>
      <td>{{entry.date | moment:'l'}}</td>
      <td>{{entry.workHours || '--'}}</td>
      <td>{{entry.holidayHours || '--'}}</td>
      <td>{{entry.vacationHours || '--'}}</td>
      <td>{{entry.personalHours || '--'}}</td>
      <td>{{entry.sickEmpHours || '--'}}</td>
      <td>{{entry.sickFamHours || '--'}}</td>
      <td>{{entry.miscHours || '--'}}</td>
      <td>{{entry.miscType | miscLeave}}</td>
      <td>{{entry.total}}</td>
    </tr>
    <tr class="time-totals-row">
      <td></td>
      <td><strong>Record Totals</strong></td>
      <td><strong>{{record.totals.workHours}}</strong></td>
      <td><strong>{{record.totals.holidayHours}}</strong></td>
      <td><strong>{{record.totals.vacationHours}}</strong></td>
      <td><strong>{{record.totals.personalHours}}</strong></td>
      <td><strong>{{record.totals.sickEmpHours}}</strong></td>
      <td><strong>{{record.totals.sickFamHours}}</strong></td>
      <td><strong>{{record.totals.miscHours}}</strong></td>
      <td></td>
      <td><strong>{{record.totals.total}}</strong></td>
    </tr>
  </tbody>
</table>
<div class="record-remarks">
  <label ng-show="!record.remarks">No Remarks.  Any remarks made will be displayed here.</label>
  <label ng-show="record.remarks">Remarks: </label>
  <span ng-bind="record.remarks"></span>
</div>
