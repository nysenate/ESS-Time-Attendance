<table class="ess-table approve-attendance-rec-table">
  <thead>
    <tr>
      <th>{{selectedIndices ? 'Review' : 'Details'}}</th>
      <th colspan="2">Employee</th>
      <th>Pay Period</th>
      <th>Work</th>
      <th>Holiday</th>
      <th>Vacation</th>
      <th>Personal</th>
      <th>Sick Fam</th>
      <th>Sick Emp</th>
      <th>Misc</th>
      <th>Total Hours</th>
    </tr>
  </thead>
  <tbody>
    <tr ng-repeat="record in records" ng-click="toggleSelected($index)">
      <td>
        <input type="checkbox" ng-if="selectedIndices" ng-checked="selectedIndices.has($index)"/>
        <a ng-if="!selectedIndices" ng-click="showDetails(record, empInfos[record.employeeId])">Show</a>
      </td>
      <td>
        <div class="small-employee-profile-pic">&nbsp;</div>
      </td>
      <td>{{empInfos[record.employeeId].fullName || record.employeeId}}</td>
      <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
      <td>{{record.totals.workHours}}</td>
      <td>{{record.totals.holidayHours}}</td>
      <td>{{record.totals.vacationHours}}</td>
      <td>{{record.totals.personalHours}}</td>
      <td>{{record.totals.sickFamHours}}</td>
      <td>{{record.totals.sickEmpHours}}</td>
      <td>{{record.totals.miscHours}}</td>
      <td>{{record.totals.total}}</td>
    </tr>
  </tbody>
</table>
