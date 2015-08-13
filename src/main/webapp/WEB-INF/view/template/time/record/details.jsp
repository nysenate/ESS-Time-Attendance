<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<p class="content-info" style="margin-bottom:0;">
  The time entries
  <span ng-if="$scope.employee">for {{$scope.employee}}</span>
  from {{record.beginDate | moment:'l'}} to {{record.endDate | moment:'l'}}
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
    <td>{{entry.miscType | miscLeave}}</td>
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
