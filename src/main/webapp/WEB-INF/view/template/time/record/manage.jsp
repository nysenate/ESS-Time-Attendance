<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="RecordManageCtrl">
  <div ng-show="loading">
    <h2 style="text-align: center">Loading Employee Time Records ...</h2>
    <div loader-indicator></div>
  </div>
  <div ng-if="!loading">
    <div class="content-container content-controls" ng-show="multipleSups()">
      <p class="content-info">View Employees Under &nbsp;
        <select name="supSelect" ng-model="$parent.selSupId" ng-change="selectNone()"
            ng-options="getOptionLabel(supId) for supId in supIds">
        </select>
      </p>
    </div>

    <section class="content-container" ng-if="supRecords[selSupId].SUBMITTED">
      <h1 class="teal">T&A Records Needing Approval</h1>

      <p class="content-info">
        Select pending records in the table below and click 'Review Selected Records'<br/>
        at the bottom to review the record details and either approve or reject them.
      </p>
      <ul class="horizontal" style="padding:0;margin:10px">
        <li style="margin-right:10px;"><a ng-click="selectAll()">Select All</a></li>
        <li style="margin-right:10px;"><a ng-click="selectNone()">Select None</a></li>
      </ul>
      <div supervisor-record-list records="supRecords[selSupId].SUBMITTED"
           emp-infos="empInfos" selected-indices="selectedIndices"></div>
      <div style="padding:.5em;text-align:right;">
        <input type="button" class="submit-button" value="Approve All" ng-click="approveAll()"/>
        <input type="button" class="submit-button" value="Review Selected Records"
               ng-click="review()" ng-disabled="selectedIndices.size == 0"/>
      </div>
    </section>


    <section class="content-container" ng-if="supRecords[selSupId].DISAPPROVED">
      <h1>T&A Records Awaiting Correction By Employee</h1>

      <p class="content-info">The following records have been rejected and are pending correction by the employee.<br/>
        Once the employee resubmits the record it will appear in the 'Records Needing Approval' section.</p>
      <div supervisor-record-list records="supRecords[selSupId].DISAPPROVED" emp-infos="empInfos"></div>
    </section>

    <section class="content-container" ng-if="supRecords[selSupId].APPROVED">
      <h1>T&A Records Pending Approval By Personnel</h1>

      <p class="content-info">The following records have been recently approved and are awaiting approval by personnel.</p>
      <div supervisor-record-list records="supRecords[selSupId].APPROVED" emp-infos="empInfos"></div>
    </section>

    <section class="content-container" ng-if="supRecords[selSupId].NOT_SUBMITTED">
      <h1>T&A Records Not Submitted</h1>

      <p class="content-info">The following table lists records that have not yet been submitted by the employee.<br/>
        You can preview the state of the record by clicking 'Show' in the Preview column.</p>
      <div supervisor-record-list records="supRecords[selSupId].NOT_SUBMITTED" emp-infos="empInfos"></div>
    </section>

    <section class="content-container" ng-if="supRecords[selSupId].DISAPPROVED_PERSONNEL">
      <h1>T&A Records Rejected By Personnel Awaiting Employee Correction</h1>

      <p class="content-info">The following table lists records that have been rejected by personnel and are awaiting re-submission by the employee<br/>
        You can preview the state of the record by clicking 'Show' in the Preview column.</p>
      <div supervisor-record-list records="supRecords[selSupId].DISAPPROVED_PERSONNEL" emp-infos="empInfos"></div>
    </section>

    <section class="content-container" ng-if="supRecords[selSupId].SUBMITTED_PERSONNEL">
      <h1>T&A Personnel Rejected Records Pending Approval</h1>

      <p class="content-info">The following records have been recently submitted to personnel by  employee to correct errors detected by personnel</p>
      <div supervisor-record-list records="supRecords[selSupId].SUBMITTED_PERSONNEL" emp-infos="empInfos"></div>
    </section>


    <div modal-container>
      <div record-detail-modal ng-if="isOpen('record-details')"></div>
      <div record-review-modal ng-if="isOpen('record-review')" ng-class="{'background-modal': top != 'record-review'}"></div>
      <div record-review-reject-modal ng-if="isOpen('record-review-reject')"></div>
    </div>
  </div>
</div>
