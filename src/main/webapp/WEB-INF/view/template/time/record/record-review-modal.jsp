<section id="review-records-modal" class="" title="Review and Approve Records">
  <p class="content-info no-bottom-margin">
    Click a record from the Employee Record List on the left hand side to review the time record. You can then either
    Approve
    or Reject the record.
  </p>

  <div id="record-selection-pane">
    <div class="pane-title">
      <span>Employee Record List</span>
    </div>
    <table id="record-selection-table" class="ess-table approve-attendance-rec-table"
           float-thead="floatTheadOpts" ng-model="records">
      <thead>
        <tr>
          <th colspan="2">Employee</th>
          <th>Pay Period</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <tr ng-repeat='record in records' ng-class="{'active' : iSelectedRecord === $index}"
            ng-click="selectRecord($index)" id="{{record.timeRecordId}}">
          <td> <div class="small-employee-profile-pic">&nbsp;</div> </td>
          <td class="name-column" ng-init="empInfo = empInfos[record.employeeId]">
            {{empInfo.firstName[0]}}. {{empInfo.lastName}}
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
    <div record-details record="records[iSelectedRecord]" employee="empInfos[records[iSelectedRecord].employeeId]"></div>
    <div>
      <div id="action-container">
        <div ng-switch="getApprovalStatus(records[iSelectedRecord])" class="record-approval-buttons">
          <input type="button" value="Undo Approval" class="neutral-button"
                 ng-switch-when="approved" ng-click="cancelRecord()"/>
          <input type="button" value="Undo Disapproval" class="neutral-button"
                 ng-switch-when="disapproved" ng-click="cancelRecord()"/>
          <input type="button" value="Disapprove Record" class="reject-button"
                 ng-switch-default ng-click="rejectRecord()"/>
          <input type="button" value="Approve Record" class="submit-button"
                 ng-switch-default ng-click="approveRecord()"/>
        </div>
        <div>
          <input type="button" class="neutral-button" value="Submit"
                 ng-click="resolve()" ng-disabled="submissionEmpty()"/>
          <input type="button" class="neutral-button" value="Cancel" ng-click="close()"/>
        </div>
      </div>
    </div>
  </div>
</section>
