<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="section-title-container teal">
    <span>Time and Attendance</span>
</section>
<section class="bread-crumb-container teal">
    <span>Time Record Entry</span>
</section>

<section class="left-nav" style="">
    <h3 class="main-topic">Home</h3>
    <ul>
        <li><a href="${ctxPath}/ui/record/entry">Time Dashboard</a></li>
    </ul>
    <h3 class="main-topic">My Attendance</h3>
    <ul>
        <li><a href="${ctxPath}/ui/record/entry">Enter Time Record</a></li>
        <li><a href="${ctxPath}/ui/record/timeoff">Request Time Off</a></li>
        <li><a href="">View Attendance History</a></li>
        <li><a href="">View Attendance Periods</a></li>
    </ul>
    <h3 class="main-topic">Manage Employees</h3>
    <ul>
        <li><a href="${ctxPath}/ui/record/manage">Review Time Records</a></li>
        <li><a href="">Review Time Off Requests</a></li>
    </ul>
    <h3 class="main-topic">Preferences</h3>
    <ul>
        <li><a href="${ctxPath}/ui/time/emailprefs">Email Reminders</a></li>
    </ul>


</section>

<section class="content-container">
    <h1 class="teal">Active Pay Periods</h1>
    <div id="payPeriodListContainer">
        <p>You have multiple pay period records to submit. Please select one from the listing to edit it.</p>
        <p></p>
        <table>
            <thead>
            <tr>
                <th>Edit</th>
                <th>Pay Period</th>
                <th>Days Remaining</th>
                <th>Status</th>
                <th>Supervisor</th>
                <th>Last Updated</th>
            </tr>
            </thead>
            <tbody>
            <tr class="active">
                <td><input checked="checked" name="activePayPeriod" type="radio"/></td>
                <td>1/30/2014 - 2/12/2014</td>
                <td style="color:#B90504;">Due</td>
                <td>Not Submitted</td>
                <td>Kenneth J. Zalewski</td>
                <td>1/31/2014 3:04 PM</td>
            </tr>
            <tr>
                <td><input name="activePayPeriod" type="radio"/></td>
                <td>2/13/2014 - 2/27/2014</td>
                <td>11</td>
                <td>Not Due Yet</td>
                <td>Kenneth J. Zalewski</td>
                <td>12/20/2014 3:04 PM</td>
            </tr>
            </tbody>
        </table>
    </div>
</section>

<section ng-controller="RecordEntryController">
    <section class="content-container">
        <h1 class="teal">Time Record Details for 1/30 - 2/12</h1>
        <div id="hourlyAccrualContainer">
            <p style="">The following accruals are available for use as of this pay period.<br/>
                <strong>Note:</strong> All hours available need approval from your appointing authority.
            </p>
            <section id="accrualsListing">
                <div class="accrual-component">
                    <div class="captioned-hour-square" style="float:left;">
                        <div class="hours-caption">Sick</div>
                        <div class="hours-display">98.5</div>
                    </div>
                </div>
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
                        <th>Sick Employee</th>
                        <th>Sick Family</th>
                        <th>Misc Hours</th>
                        <th>Misc Type</th>
                        <th>Daily Total</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-class="{'weekend': $index%6 == 0 || $index%7 == 0}" class="time-record-row" ng-repeat="item in [0,1,2,3,4,5,6,7,8,9,10,12,13,14]">
                        <td ng-class="{'today' : $index==4}">Fri 12/{{$index}}/2014</td>
                        <td><input time-record-input tabindex="{{$index+1}}" class="hours-input" placeholder="--" type="text" min="0" max="24" step=".5" name="numWorkHours"/></td>
                        <td><input time-record-input class="hours-input" disabled placeholder="--" type="text" min="0" max="7" step=".5" name="numHolidayHours"/></td>
                        <td><input time-record-input tabindex="{{$index+15}}" class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numVacationHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numPersonalHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numSickEmpHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numSickFamHours"/></td>
                        <td><input time-record-input class="hours-input" placeholder="--" type="text" min="0" max="7" step=".5" name="numMiscHours"/></td>
                        <td>
                            <select style="font-size:.9em;margin:5px;" name="miscHourType">
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
                        <input type="button" value="Save Record"/>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </form>
        </section>
    </section>
</section>
