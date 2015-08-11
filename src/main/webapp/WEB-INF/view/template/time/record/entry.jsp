<%@ page import="gov.nysenate.seta.model.payroll.MiscLeaveType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String miscLeaveMapJson = MiscLeaveType.getJsonLabels();
%>
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

<div ng-controller="RecordEntryController" ng-init='init(<%= miscLeaveMapJson%>)'>
    <div id="record-selection-container" class="content-container content-controls">
        <p class="content-info">Enter a time and attendance record by selecting from the list of active pay periods.</p>
        <table>
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
            <tr ng-repeat="record in records">
                <td>
                    <input type="radio" name="recordSelect" ng-value="records[$index]"
                           ng-model="$parent.selectedRecord"/>
                </td>
                <td>{{record.payPeriod.startDate | moment:'l'}} - {{record.payPeriod.endDate | moment:'l'}}</td>
                <td>{{record.supervisor.fullName}}</td>
                <td>{{record.endDate | momentFromNow}}</td>
                <td>{{record.recordStatus | timeRecordStatus}}</td>
                <td>
                    <span ng-show="record.updateDate | momentEquals:record.originalDate:'second' | not">
                      {{record.updateDate | moment: 'lll'}}
                    </span>
                    <span ng-show="record.updateDate | momentEquals:record.originalDate:'second'">New</span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div class="content-container">
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
                             class="hours-caption">Year To Date Hours Of Service</div>
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
            <table class="ess-table" id="timeRecordTable">
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
                <tr ng-class="{'weekend': date.getDay() == 0 || date.getDay() == 6, 'dummy-entry': entry.dummyEntry}"
                    class="time-record-row" ng-repeat="(i,entry) in displayEntries">
                    <td style="text-align: right;padding-right:20px;">{{entry.date | moment:'ddd l'}}</td>
                    <td>
                        <input ng-change="setDirty(entry)" ng-model="entry.workHours" time-record-input
                               tabindex="{{$index+1}}" class="hours-input" placeholder="--" type="text" min="0" max="24"
                               step=".5" name="numWorkHours"/>
                    </td>
                    <td>
                        <input ng-change="setDirty(entry)" ng-model="entry.holidayHours" time-record-input class="hours-input"
                               disabled placeholder="--" type="number" min="0" max="7" step=".5" name="numHolidayHours"/>
                    </td>
                    <td>
                        <input ng-change="setDirty(entry)" ng-model="entry.vacationHours" time-record-input
                               tabindex="{{$index+15}}" class="hours-input" placeholder="--" type="number" min="0" max="7"
                               step=".5" name="numVacationHours"/></td>
                    <td>
                        <input ng-change="setDirty(entry)" ng-model="entry.personalHours" time-record-input
                               class="hours-input" placeholder="--" type="number" min="0" max="7" step=".5"
                               name="numPersonalHours"/></td>
                    <td>
                        <input ng-change="setDirty(entry)" ng-model="entry.sickEmpHours" time-record-input class="hours-input"
                               placeholder="--" type="number" min="0" max="7" step=".5" name="numSickEmpHours"/></td>
                    <td><input ng-change="setDirty(entry)" ng-model="entry.sickFamHours" time-record-input class="hours-input"
                               placeholder="--" type="number" min="0" max="7" step=".5" name="numSickFamHours"/></td>
                    <td><input ng-change="setDirty(entry)" ng-model="entry.miscHours" time-record-input class="hours-input"
                               placeholder="--" type="number" min="0" max="7" step=".5" name="numMiscHours"/></td>
                    <td>
                        <select style="font-size:.9em;" name="miscHourType"
                                ng-model="entry.miscType"
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
                    <textarea id="remarksTextArea"></textarea>
                </div>
                <div class="float-right">
                    <label ng-show="selectedRecord.savedDate" style="position: relative;top: 20px;right: 20px;font-size: 1.1em;">Last
                        Saved {{selectedRecord.savedDate | moment:'lll'}}
                    </label>
                    <input ng-click="saveRecord()" class="submit-button" type="button"
                           value="Save Record"/>
                </div>
                <div class="clearfix"></div>
            </div>
        </form>
    </div>
</div>
