<%@ page import="gov.nysenate.seta.model.payroll.MiscLeaveType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!--
Toggle this for temporary emps.
<div class="content-container content-controls">
<p class="content-info">Enter attendance record for pay period &nbsp;
<select>
<option>04/24/14 - 05/07/14</option>
<option>05/08/14 - 05/21/14</option>
</select>
</p>
</div> -->

<div ng-controller="RecordEntryController">
  <div id="record-selection-container" class="content-container content-controls">
    <p class="content-info">Enter a time and attendance record by selecting from the list of active pay periods.</p>
    <table class="simple-table">
      <thead>
      <tr>
        <th>Select</th>
        <th>Pay Period</th>
        <th>Supervisor</th>
        <th>Period End</th>
        <th>Status</th>
        <th>Last Updated</th>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="record in records" ng-click="$parent.iSelectedRecord = $index">
        <td>
          <input type="radio" name="recordSelect" ng-value="$index"
                 ng-model="$parent.iSelectedRecord"/>
        </td>
        <td>{{record.payPeriod.startDate | moment:'l'}} - {{record.payPeriod.endDate | moment:'l'}}</td>
        <td>{{record.supervisor.fullName}}</td>
        <td>{{record.endDate | momentFromNow}}</td>
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
  </div>
  <div class="content-container" ng-show="displayEntries">
    <p class="content-info">All hours available need approval from appointing authority.</p>

    <div class="accrual-hours-container">
      <div>
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
                Difference: {{state.accrual.serviceYtd - state.accrual.serviceYtdExpected}}
              </div>
            </div>
          </div>
        </div>
        <div style="clear:both;"></div>
      </div>
    </div>
    <hr/>
    <form id="timeRecordForm" method="post" action="">
      <table class="ess-table" id="timeRecordTable" float-thead="floatTheadOpts" ng-model="displayEntries">
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
        <tr class="time-record-row" ng-repeat="(i,entry) in displayEntries"
            ng-class="{'weekend': isWeekend(entry.date), 'dummy-entry': entry.dummyEntry}">
          <td style="text-align: right;padding-right:20px;">{{entry.date | moment:'ddd M/D/YYYY'}}</td>
          <td ng-class="{invalid: entry.workHours === undefined}">
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="24" ng-disabled="entry.unavailable"
                   ng-model="entry.workHours" tabindex="{{$index+1}}" name="numWorkHours"/>
          </td>
          <td>
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="7" ng-disabled="entry.unavailable"
                   ng-model="entry.holidayHours" name="numHolidayHours" disabled/>
          </td>
          <td ng-class="{invalid: entry.vacationHours > 0 && !validation.accruals.vacation || entry.vacationHours === undefined}">
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="7" ng-disabled="entry.unavailable"
                   ng-model="entry.vacationHours" name="numVacationHours" tabindex="{{$index+15}}"/>
          </td>
          <td ng-class="{invalid: entry.personalHours > 0 && !validation.accruals.personal || entry.personalHours === undefined}">
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="7" ng-disabled="entry.unavailable"
                   ng-model="entry.personalHours" name="numPersonalHours"/>
          </td>
          <td ng-class="{invalid: entry.sickEmpHours > 0 && !validation.accruals.sick || entry.sickEmpHours === undefined}">
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="7" ng-disabled="entry.unavailable"
                   ng-model="entry.sickEmpHours" name="numSickEmpHours"/>
          </td>
          <td ng-class="{invalid: entry.sickFamHours > 0 && !validation.accruals.sick || entry.sickFamHours === undefined}">
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="7" ng-disabled="entry.unavailable"
                   ng-model="entry.sickFamHours" name="numSickFamHours"/>
          </td>
          <td ng-class="{invalid: entry.miscHours === undefined}">
            <input type="number" ng-change="setDirty()" time-record-input class="hours-input"
                   placeholder="--" step=".5" min="0" max="7" ng-disabled="entry.unavailable"
                   ng-model="entry.miscHours" name="numMiscHours"/>
          </td>
          <td>
            <select style="font-size:.9em;" name="miscHourType"
                    ng-model="entry.miscType" ng-disabled="entry.unavailable" ng-change="setDirty()"
                    ng-options="type as label for (type, label) in miscLeaves">
              <option value="">No Misc Hours</option>
            </select>
          </td>
          <td><span>{{entry.total | number}}</span></td>
        </tr>
        <tr class="time-totals-row">
          <td>Biweekly Totals</td>
          <td>{{totals.work}}</td>
          <td>{{totals.holiday}}</td>
          <td>{{totals.vac}}</td>
          <td>{{totals.personal}}</td>
          <td>{{totals.sickEmp}}</td>
          <td>{{totals.sickFam}}</td>
          <td>{{totals.misc}}</td>
          <td></td>
          <td>{{totals.total}}</td>
        </tr>
        </tbody>
      </table>
      <div id="saveRecordContainer">
        <div id="remarksRecordContainer">
          <label for="remarksTextArea">Notes / Remarks</label>
          <textarea id="remarksTextArea" ng-model="records[iSelectedRecord].remarks"></textarea>
        </div>
        <div class="float-right">
          <label ng-show="records[iSelectedRecord].savedDate"
                 style="position: relative;top: 20px;right: 20px;font-size: 1.1em;">
            Last Saved {{records[iSelectedRecord].savedDate | moment:'lll'}}
          </label>
          <input ng-click="saveRecord(false)" class="submit-button" type="button" value="Save Record"
                 ng-disabled="!records[iSelectedRecord].dirty"/>
          <input ng-click="saveRecord(true)" class="submit-button" type="button" value="Submit Record"
                 ng-disabled="!recordSubmittable()"/>
        </div>
        <div class="clearfix"></div>
      </div>
    </form>
  </div>
</div>
