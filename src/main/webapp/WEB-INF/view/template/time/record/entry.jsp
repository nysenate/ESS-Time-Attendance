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
</div>  -->

<section ng-controller="RecordEntryController">
    <section class="content-container">
        <h1 class="teal">Time and Attendance Record Entry</h1>
        <div id="record-selection-container" class="content-info">
            <table>
                <thead>
                    <tr>
                        <th>Edit</th>
                        <th>Pay Period</th>
                        <th>Supervisor</th>
                        <th>Days Remaining</th>
                        <th>Status</th>
                        <th>Last Updated</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><input type="radio" checked="checked"/></td>
                        <td>04/24/2014 - 5/7/2014</td>
                        <td>Kenneth J. Zalewski</td>
                        <td>5</td>
                        <td>In Progress</td>
                        <td>04/24/2014 3:04 PM</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div id="hourlyAccrualContainer" style="margin-top:5px;">
            <section id="accrualsListing">
                <div class="accrual-component">
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Personal</div>
                        <div class="hours-display">32</div>
                    </div>
                </div>
                <div class="accrual-component">
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Vacation</div>
                        <div class="hours-display">34</div>
                    </div>
                </div>
                <div class="accrual-component">
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Sick</div>
                        <div class="odometer hours-display">232</div>
                    </div>
                </div>
                <div class="accrual-component">
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Expected Ytd</div>
                        <div class="hours-display">147</div>
                    </div>
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Actual Ytd</div>
                        <div class="hours-display">156.5</div>
                    </div>
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Difference</div>
                        <div class="hours-display" style="color:#799933;">+9.5</div>
                    </div>
                </div>
                <div style="clear:both;"></div>
            </section>
        </div>
        <section>
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
                    <tr ng-class="{'weekend': date.getDay() == 0 || date.getDay() == 6}" class="time-record-row"
                        ng-repeat="date in dates">
                        <td style="text-align: right;padding-right:20px;" ng-class="">{{date.toDateString()}}</td>
                        <td><input time-record-input tabindex="{{$index+1}}" class="hours-input" placeholder="--" type="text" min="0" max="24" step=".5" name="numWorkHours"/></td>
                        <td><input time-record-input class="hours-input" disabled placeholder="--" type="text" min="0" max="7" step=".5" name="numHolidayHours"/></td>
                        <td><input time-record-input tabindex="{{$index+15}}" class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numVacationHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numPersonalHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numSickEmpHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numSickFamHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numMiscHours"/></td>
                        <td>
                            <select style="font-size:.9em;" name="miscHourType">
                                <option>No Misc Hours</option>
                                <option>Bereavement Leave</option>
                                <option>Blood Donation</option>
                                <option>Breast, Prostate Screening</option>
                                <option>Extended Sick Leave</option>
                                <option>Extraordinary Leave</option>
                            </select>
                        </td>
                        <td><span>0</span></td>
                    </tr>
                    <tr class="time-totals-row">
                        <td>Biweekly Totals</td>
                        <td>0</td>
                        <td>0</td>
                        <td>0</td>
                        <td>0</td>
                        <td>0</td>
                        <td>0</td>
                        <td>0</td>
                        <td></td>
                        <td>0</td>
                    </tr>
                    </tbody>
                </table>
                <div id="saveRecordContainer">
                    <div id="remarksRecordContainer">
                        <label for="remarksTextArea">Notes / Remarks</label>
                        <textarea id="remarksTextArea"></textarea>
                    </div>
                    <div class="float-right">
                        <input class="submit-button" type="button" value="Save Record"/>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </form>
        </section>
    </section>
</section>
