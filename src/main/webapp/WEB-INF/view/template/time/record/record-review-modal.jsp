<section id="review-records-modal" class="content-container content-controls" title="Review and Approve Records">
  <p class="content-info no-bottom-margin">
    Click a record from the Employee Record List on the left hand side to review the time record. You can then either
    Approve
    or Reject the record.
  </p>
  <hr/>
  <div id="record-selection-pane">
    <div class="pane-title">
      <span>Employee Record List</span>
    </div>
    <table id="record-selection-table" class="ess-table approve-attendance-rec-table"
           float-thead="floatTheadOpts" ng-model="records">
      <thead>
        <tr>
          <th>Employee</th>
          <th>Pay Period</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr ng-repeat='record in records' ng-class="{'active': iSelectedRecord === $index,
                                                     'approved': getApprovalStatus(record) === 'approved',
                                                     'disapproved': getApprovalStatus(record) === 'disapproved'}"
            ng-click="selectRecord($index)" id="{{record.timeRecordId}}">
          <td class="name-column">
            {{record.employee.fullName}}
          </td>
          <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
          <td ng-switch="getApprovalStatus(record)" style="width: 10em">
            <span ng-switch-when="approved">Approve</span>
            <span ng-switch-when="disapproved">Disapprove</span>
            <span ng-switch-default>--</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div id="record-details-view">
    <div record-details record="records[iSelectedRecord]"></div>
      <hr/>
      <div id="action-container">
        <div ng-switch="getApprovalStatus(records[iSelectedRecord])" class="record-approval-buttons">
          <input type="button" value="Undo Approval" class="reject-button"
                 ng-switch-when="approved" ng-click="cancelRecord()"/>
          <input type="button" value="Undo Disapproval" class="neutral-button"
                 ng-switch-when="disapproved" ng-click="cancelRecord()"/>
          <input type="button" value="Approve Record" class="submit-button"
                 ng-switch-default ng-click="approveRecord()"/>
          <input type="button" value="Disapprove Record" class="reject-button"
                 ng-switch-default ng-click="rejectRecord()"/>

        </div>
        <div>
          <input type="button" class="submit-button" value="Submit Changes"
                 ng-click="resolve()" ng-disabled="submissionEmpty()"/>
          <input type="button" class="neutral-button" value="Cancel" ng-click="close()"/>
        </div>
      </div>
    </div>
</section>
