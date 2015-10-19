<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
/**
 * This template provides all time record entry functionality for both regular/special annual time records
 * as well as temporary time records.
*/
%>
<div ng-controller="RecordEntryController">
  <div id="record-selection-container" class="record-selection-container content-container content-controls"
       ng-show="state.records.length > 0">
    <p class="content-info">Enter a time and attendance record by selecting from the list of active pay periods.</p>
    <% /** Record selection table for cases when there are a few active records to display. */ %>
    <table class="simple-table" ng-if="state.records.length <= 5">
      <thead>
        <tr><th>Select</th><th>Pay Period</th><th>Supervisor</th><th>Period End</th><th>Status</th><th>Last Updated</th></tr>
      </thead>
      <tbody>
        <tr ng-repeat="record in state.records" ng-click="$parent.state.iSelectedRecord = $index">
          <td>
            <input type="radio" name="recordSelect" ng-value="$index"
                   ng-model="$parent.state.iSelectedRecord"/>
          </td>
          <td>{{record.payPeriod.startDate | moment:'l'}} - {{record.payPeriod.endDate | moment:'l'}}</td>
          <td>{{record.supervisor.fullName}}</td>
          <td ng-class="{'dark-red': record.isDue === true}">{{record.dueFromNowStr}}</td>
          <td>{{record.recordStatus | timeRecordStatus}}</td>
          <td>
            <span ng-show="record.updateDate | momentCmp:'=':record.originalDate:'second' | not">
              {{record.updateDate | moment: 'lll'}}
            </span>
            <span ng-show="record.updateDate | momentCmp:'=':record.originalDate:'second'">New</span>
          </td>
        </tr>
      </tbody>
    </table>
    <% /** Record selection menu for cases when there are many active records (i.e. temporary employees). */ %>
    <div ng-if="state.records.length > 5">
      <div class="record-selection-menu-details">
        <label class="bold">Record Dates:
          <select class="record-selection-menu" ng-model="state.iSelectedRecord"
                  ng-options="state.records.indexOf(record) as getRecordRangeDisplay(record) for record in state.records"></select>
        </label>
        <span><span class="bold">Supervisor: </span>{{state.records[state.iSelectedRecord].supervisor.fullName}}</span>
        <span><span class="margin-left-10 bold">Status: </span>{{state.records[state.iSelectedRecord].recordStatus | timeRecordStatus}}</span>
        <span class="margin-left-10 bold">Last Updated: </span>
      <span ng-show="state.records[state.iSelectedRecord].updateDate | momentCmp:'=':state.records[state.iSelectedRecord].originalDate:'second' | not">
            {{state.records[state.iSelectedRecord].updateDate | moment: 'lll'}}
      </span>
        <span ng-show="state.records[state.iSelectedRecord].updateDate | momentCmp:'=':state.records[state.iSelectedRecord].originalDate:'second'">New</span>
      </div>
    </div>
  </div>

  <div loader-indicator ng-show="state.pageState === pageStates.FETCHING"></div>

  <% /** Display an error message if there are notes for a disapproved time record. */ %>
  <div ess-notification level="error" title="Time record requires correction"
       message="{{state.records[state.iSelectedRecord].remarks}}" class="margin-top-20"
       ng-show="state.records[state.iSelectedRecord].recordStatus === 'DISAPPROVED' ||
                state.records[state.iSelectedRecord].recordStatus === 'DISAPPROVED_PERSONNEL'">
  </div>

  <% /** If there are no active records for the user, display a warning message indicating such. */ %>
  <div ess-notification level="warn" title="No time records available to enter."
       ng-show="state.pageState === pageStates.FETCHED && state.records.length == 0"
       message="Please contact Senate Personnel at (518) 455-3376 if you require any assistance."></div>

  <% /** Accruals and Time entry for regular/special annual time record entries. */ %>
  <div class="content-container" ng-show="state.pageState !== pageStates.FETCHING && state.records[state.iSelectedRecord].timeEntries">
    <p class="content-info">All hours available need approval from appointing authority.</p>
    <div ess-notification level="warn" title="Record with multiple pay types" class="margin-10"
         ng-if="state.annualEntries && state.tempEntries">
      <p>
        There was a change in pay type during the time covered by this record.<br>
        Record days have been split into two seperate entry tables, one for Regular/Special Annual pay, another for Temporary pay
      </p>
    </div>
    <form id="timeRecordForm" method="post" action="">

      <!-- Annual Entry Form -->
      <div class="ra-sa-entry" ng-if="state.annualEntries">
        <h1 class="time-entry-table-title" ng-if="state.tempEntries">Regular/Special Annual Pay Entries</h1>
        <div class="accrual-hours-container">
          <div class="accrual-component">
            <div class="captioned-hour-square" style="float:left;">
              <div class="hours-caption personal">Personal Hours</div>
              <div class="hours-display">{{state.accrual.personalAvailable}}</div>
            </div>
          </div>
          <div class="accrual-component">
            <div class="captioned-hour-square" style="float:left;">
              <div class="hours-caption vacation">Vacation Hours</div>
              <div class="hours-display">{{state.accrual.vacationAvailable}}</div>
            </div>
          </div>
          <div class="accrual-component">
            <div class="captioned-hour-square" style="float:left;">
              <div class="hours-caption sick">Sick Hours</div>
              <div class="odometer hours-display">{{state.accrual.sickAvailable}}</div>
            </div>
          </div>
          <div class="accrual-component">
            <div class="captioned-hour-square" style="width:390px;">
              <div style="background:rgb(92, 116, 116);color:white"
                   class="hours-caption">Year To Date Hours Of Service
              </div>
              <div class="hours-display" style="font-size:1em">
                <div class="ytd-hours">
                  Expected: {{state.accrual.serviceYtdExpected}}
                </div>
                <div class="ytd-hours">Actual: {{state.accrual.serviceYtd}}</div>
                <div class="ytd-hours" style="border-right:none;">
                  Difference:
                  <span ng-bind-html="(state.accrual.serviceYtd - state.accrual.serviceYtdExpected) | hoursDiffHighlighter"></span>
                </div>
              </div>
            </div>
          </div>
          <div style="clear:both;"></div>
        </div>
        <hr/>
        <table class="ess-table time-record-entry-table" id="ra-sa-time-record-table"
               ng-model="state.records[state.iSelectedRecord].timeEntries">
          <thead>
            <tr>
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
          <tr class="time-record-row"
              ng-repeat="(i,entry) in regRecords = (state.records[state.iSelectedRecord].timeEntries | filter:{payType: '!TE'})"
              ng-class="{'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry}"
              ng-init="numRecs = regRecords.length">
            <td class="date-column">{{entry.date | moment:'ddd M/D/YYYY'}}</td>
            <td ng-class="{invalid: entry.workHours === undefined}">
              <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="24" ng-disabled="entry.date | momentCmp:'>':'now':'day'"
                     ng-model="entry.workHours" tabindex="1" name="numWorkHours"/>
            </td>
            <td>
              <input type="number" readonly time-record-input class="hours-input"
                     step=".5" min="0" max="7" ng-model="entry.holidayHours" name="numHolidayHours"/>
            </td>
            <td ng-class="{invalid: entry.vacationHours > 0 && !validation.accruals.vacation || entry.vacationHours === undefined}">
              <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="7"
                     ng-model="entry.vacationHours" name="numVacationHours"
                     tabindex="{{(entry.workHours == null || entry.workHours >= 7) ? 2 : 1}}"/>
            </td>
            <td ng-class="{invalid: entry.personalHours > 0 && !validation.accruals.personal || entry.personalHours === undefined}">
              <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="7"
                     tabindex="{{(entry.workHours == null || entry.workHours >= 7) ? 3 : 1}}"
                     ng-model="entry.personalHours" name="numPersonalHours"/>
            </td>
            <td ng-class="{invalid: entry.sickEmpHours > 0 && !validation.accruals.sick || entry.sickEmpHours === undefined}">
              <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="7"
                     tabindex="{{(entry.workHours == null || entry.workHours >= 7) ? 4 : 1}}"
                     ng-model="entry.sickEmpHours" name="numSickEmpHours"/>
            </td>
            <td ng-class="{invalid: entry.sickFamHours > 0 && !validation.accruals.sick || entry.sickFamHours === undefined}">
              <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="7"
                     tabindex="{{(entry.workHours == null || entry.workHours >= 7) ? 5 : 1}}"
                     ng-model="entry.sickFamHours" name="numSickFamHours"/>
            </td>
            <td ng-class="{invalid: entry.miscHours === undefined}">
              <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                     placeholder="--" step=".5" min="0" max="7"
                     tabindex="{{(entry.workHours == null || entry.workHours >= 7) ? 6 : 1}}"
                     ng-model="entry.miscHours" name="numMiscHours"/>
            </td>
            <td>
              <select style="font-size:.9em;" name="miscHourType"
                      ng-model="entry.miscType" ng-change="setDirty()"
                      tabindex="{{(entry.workHours == null || entry.workHours >= 7) ? 7 : 1}}"
                      ng-options="type as label for (type, label) in state.miscLeaves">
                <option value="">No Misc Hours</option>
              </select>
            </td>
            <td><span>{{entry.total | number}}</span></td>
          </tr>
          <tr class="time-totals-row">
            <td><span ng-if="state.tempEntries">RA/SA</span> Record Totals</td>
            <td>{{state.totals.raSaWorkHours}}</td>
            <td>{{state.totals.holidayHours}}</td>
            <td>{{state.totals.vacationHours}}</td>
            <td>{{state.totals.personalHours}}</td>
            <td>{{state.totals.sickEmpHours}}</td>
            <td>{{state.totals.sickFamHours}}</td>
            <td>{{state.totals.miscHours}}</td>
            <td></td>
            <td>{{state.totals.raSaTotal}}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Temporary Entry Form -->
      <div class="te-entry" ng-if="state.tempEntries">
        <h1 class="time-entry-table-title" ng-if="state.annualEntries">Temporary Pay Entries</h1>
        <div ess-notification level="warn" title="Record with multiple salaries" ng-show="state.salaryRecs.length > 1" class="margin-10">
          <p>
            There were one or more changes in salary during the time covered by this record.<br>
            Select a date range from the "Salary Dates" field to see the salary rate and hours available for selected dates.
          </p>
        </div>
        <div class="allowance-container">
          <div class="allowance-component">
            <div class="captioned-hour-square">
              <div style="background:rgb(92, 116, 116);color:white" class="hours-caption">
                {{state.allowances[state.selectedYear].year}} Allowance
              </div>
              <div class="hours-display" style="font-size:1em">
                <div class="ytd-hours">
                  <div class="hours-caption">Total</div>
                  {{state.allowances[state.selectedYear].yearlyAllowance | currency}}
                </div>
                <div class="ytd-hours">
                  <div class="hours-caption">Used</div>
                  {{state.allowances[state.selectedYear].moneyUsed | currency}}
                </div>
                <div class="ytd-hours" style="border-right:none;">
                  <div class="hours-caption">Remaining</div>
                  {{state.allowances[state.selectedYear].yearlyAllowance - state.allowances[state.selectedYear].moneyUsed | currency}}
                </div>
              </div>
            </div>
          </div>
          <div class="allowance-component">
            <div class="captioned-hour-square">
              <div style="background:rgb(92, 116, 116);color:white" class="hours-caption">
                Record Usage
              </div>
              <div class="hours-display" style="font-size:1em">
                <div class="ytd-hours" style="flex-grow: 1.5">
                  <div class="hours-caption">Used</div>
                  {{state.records[state.iSelectedRecord].moneyUsed | currency}}
                </div>
                <div class="ytd-hours" ng-if="state.salaryRecs.length > 1" style="flex-grow: 0.4">
                  <div class="hours-caption">Salary Dates</div>
                  <select ng-model="state.iSelSalRec" style="margin-right: 3px"
                          ng-options="state.salaryRecs.indexOf(salRec) as getSalRecDateRange(salRec) for salRec in state.salaryRecs"></select>
                </div>
                <div class="ytd-hours">
                  <div class="hours-caption">Hourly Rate</div>
                  {{state.salaryRecs[state.iSelSalRec].salaryRate | currency}}/hr.
                </div>
                <div class="ytd-hours" style="border-right:none; flex-grow: 0.6">
                  <div class="hours-caption">Hrs. Used/Avail.</div>
                  {{state.totals.tempWorkHours}} / {{getAvailableHours() | number}}
                </div>
              </div>
            </div>
          </div>
        </div>
        <hr/>
        <table class="ess-table time-record-entry-table" id="te-time-record-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Work</th>
              <th>Work Time Description / Comments</th>
            </tr>
          </thead>
          <tbody>
            <tr class="time-record-row"
                ng-repeat="(i,entry) in state.records[state.iSelectedRecord].timeEntries | filter:{payType: 'TE'}"
                ng-class="{'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry}">
              <td class="date-column">{{entry.date | moment:'ddd M/D/YYYY'}}</td>
              <td ng-class="{invalid: entry.workHours === undefined}">
                <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                       placeholder="--" step="0.25" min="0" max="24" ng-disabled="entry.date | momentCmp:'>':'now':'day'"
                       ng-model="entry.workHours" name="numWorkHours"/>
              </td>
              <td class="entry-comment-col">
                <input type="text" ng-change="setDirty()" class="entry-comment"
                       ng-model="entry.empComment" name="entryComment"/>
              </td>
            </tr>
            <tr class="time-totals-row">
              <td><span ng-if="state.annualEntries">TE</span> Record Totals</td>
              <td>{{state.totals.tempWorkHours}}</td>
              <td></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="save-record-container">
        <div class="record-remarks-container">
          <label for="remarks-text-area">Notes / Remarks</label>
        <textarea id="remarks-text-area" class="record-remarks-text-area"
                  ng-model="state.records[state.iSelectedRecord].remarks" ng-change="setDirty()">
        </textarea>
        </div>
        <div class="float-right">
          <input ng-click="saveRecord(false)" class="submit-button" type="button" value="Save Record"
                 ng-disabled="!state.records[state.iSelectedRecord].dirty"/>
          <input ng-click="saveRecord(true)" class="submit-button" type="button" value="Submit Record"
                 ng-disabled="!recordSubmittable()"/>
        </div>
        <div class="clearfix"></div>
      </div>
    </form>
  </div>

  <% /** Container for all modal dialogs */ %>
  <div modal-container>
    <% /** Modals for record save. */ %>
    <div ng-if="isOpen('save-indicator')" class="save-progress-modal">
      <div ng-show="state.pageState === pageStates.SAVING">
        <h3 class="content-info" style="margin-bottom:0;">
          Saving time record...
        </h3>
        <loader-indicator></loader-indicator>
      </div>
      <div ng-show="state.pageState === pageStates.SAVED">
        <h3 class="content-info" style="margin-bottom:0;">Your time record has been saved.</h3>
        <h4>What would you like to do next?</h4>
        <input ng-click="logout()" class="reject-button" type="button" value="Log out of ESS"/>
        <input ng-click="closeModal()" class="submit-button" type="button" value="Go back to ESS"/>
      </div>
    </div>
    <% /** Modals for record submission. */ %>
    <div ng-if="isOpen('submit-indicator')">
      <div ng-show="state.pageState === pageStates.SUBMIT_ACK">
        <h3 class="content-info" style="margin-bottom:0;">
          Before submitting, you must acknowledge the following:
        </h3>
        <div style="padding:20px;text-align:left;">
          <p>1. For purposes of submitting a timesheet, the username and password is the electronic signature of the employee.
            As liability attaches to each timesheet, the employee should ensure that his or her username and password is
            securely kept and used.
          </p>
          <hr/>
          <p>2. The hours recorded on the Submitted Time and Attendance Record accurately reflect time actually spent by me
            in the performance of my assigned duties.
          </p>
          <hr/>
          <p>3. You will be saving and submitting this Time and Attendance Record to your T&A Supervisor.
            Once submitted, you will no longer have the ability to edit this Record unless your supervisor or personnel
            disapproves the record.
          </p>
          <hr/>
          <div style="text-align: center;">
            <input ng-click="submitRecord()" class="submit-button" style="margin-right: 20px;" type="button" value="I acknowledge"/>
            <input ng-click="closeModal()" class="reject-button" type="button" value="Cancel"/>
          </div>
        </div>
      </div>
      <div ng-show="state.pageState === pageStates.SUBMITTING" class="save-progress-modal">
        <h3 class="content-info" style="margin-bottom:0;">
          Saving and submitting time record...
        </h3>
        <loader-indicator></loader-indicator>
      </div>
      <div ng-show="state.pageState === pageStates.SUBMITTED" class="save-progress-modal">
        <h3 class="content-info" style="margin-bottom:0;">Your time record has been submitted.</h3>
        <h4>What would you like to do next?</h4>
        <input ng-click="logout()" class="reject-button" type="button" value="Log out of ESS"/>
        <input ng-click="finishSubmitModal()" class="submit-button" type="button" value="Go back to ESS"/>
      </div>
    </div>
  </div>
</div>