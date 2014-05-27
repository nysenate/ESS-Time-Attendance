<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="content-container content-controls">
    <p class="content-info">View Employees Under &nbsp;
        <select>
            <option>Current Supervisor - (6 Pending Records)</option>
            <option>Supervisor 1 - (3 Pending Records)</option>
            <option>Supervisor 2 - (4 Pending Records)</option>
        </select>
    </p>
</div>

<section class="content-container">
    <h1 class="teal">T&A Records Needing Approval</h1>
    <p class="content-info">
        Select pending records in the table below and click 'Review Selected Records'<br/>
        at the bottom to review the record details and either approve or reject them.
    </p>
    <ul class="horizontal" style="padding:0;margin:10px">
        <li style="margin-right:10px;"><a>Select All</a></li>
        <li style="margin-right:10px;"><a>Select None</a></li>
    </ul>
    <table class="ess-table approve-attendance-rec-table">
        <thead>
            <tr>
                <th>Review</th>
                <th colspan="2">Employee</th>
                <th>Pay Period</th>
                <th>Work</th>
                <th>Holiday</th>
                <th>Vacation</th>
                <th>Personal</th>
                <th>Sick</th>
                <th>Misc</th>
                <th>Total Hours</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><input type="checkbox"/></td>
                <td><div class="small-employee-profile-pic">&nbsp;</div></td>
                <td>Employee 1 Fullname</td>
                <td>04/24/14 - 05/07/14</td>
                <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
            </tr>
            <tr>
                <td><input type="checkbox"/></td>
                <td><div style="background:url('https://avatars0.githubusercontent.com/u/330720?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
                <td>Employee 2 Fullname</td>
                <td>04/24/14 - 05/07/14</td>
                <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
            </tr>
            <tr>
                <td><input type="checkbox"/></td>
                <td><div style="background:url('https://avatars2.githubusercontent.com/u/94740?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
                <td>Employee 3 Fullname</td>
                <td>04/24/14 - 05/07/14</td>
                <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
            </tr>
        </tbody>
    </table>
    <div style="padding:.5em;text-align:right;">
        <input id="review-sel-records-btn" type="button" class="submit-button" value="Review Selected Records"/>
    </div>
</section>

<section id="review-records-modal" class="" title="Review and Approve Records">
    <p class="content-info no-bottom-margin">
        Click a record from the Employee Record List on the left hand side to review the time record. You can then either Approve
        or Reject the record.
    </p>
    <div id="record-selection-pane">
        <div class="pane-title">
            <span>Employee Record List</span>
        </div>
        <table id="record-selection-table" class="ess-table approve-attendance-rec-table">
            <thead>
                <tr>
                    <th colspan="2">Employee</th>
                    <th>Pay Period</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr class="active">
                    <td><div style="background:url('https://avatars0.githubusercontent.com/u/2687188?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
                    <td class="name-column">A. Islam</td>
                    <td>04/24/14 - 05/07/14</td>
                    <td>Pending</td>
                </tr>
                <tr>
                    <td><div style="background:url('https://avatars0.githubusercontent.com/u/330720?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
                    <td class="name-column">S. Crain</td>
                    <td>04/24/14 - 05/07/14</td>
                    <td>Pending</td>
                </tr>
                <tr>
                    <td><div style="background:url('https://avatars2.githubusercontent.com/u/94740?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
                    <td class="name-column">K. Zalewski</td>
                    <td>04/24/14 - 05/07/14</td>
                    <td>Pending</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div id="record-details-view">
        <div class="pane-title">
            <span>Record for A. Islam - 04/24/14 - 05/07/14</span>
        </div>
        <table class="attendance-entry-sub-table ess-table small-text">
            <thead>
            <tr>
                <th>Day</th>
                <th style="width:80px;">Date</th>
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
            <tr><td>Fri</td><td>1/30</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Sat</td><td>1/31</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Sun</td><td>2/1</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Mon</td><td>2/2</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Tue</td><td>2/3</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Wed</td><td>2/4</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Thu</td><td>2/5</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Fri</td><td>2/6</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Sat</td><td>2/7</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Sun</td><td>2/8</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Mon</td><td>2/9</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Tue</td><td>2/10</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Wed</td><td>2/11</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr><td>Thu</td><td>2/12</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>--</td><td>7</td></tr>
            <tr style="border-top:2px solid #aaa;"><td colspan="2"><strong>Totals</strong></td><td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td></td><td>70</td></tr>
            </tbody>
        </table>
        <div>
            <div id="remarks-container">
                <label>Remarks: </label>
                <span>Any remarks made by the employee will be displayed here.</span><br/>
            </div>
            <div id="action-container">
                <input onclick="$('#rejection-dialog').dialog('open');" class="reject-button" type="button" value="Disapprove Record"/>
                <input class="submit-button" type="button" value="Approve Record"/>
                <input style="float:right;" onclick="$('#review-records-modal').dialog('close');" class="neutral-button" type="button" value="Close"/>
            </div>
        </div>
    </div>
</section>

<section class="content-container">
    <h1>T&A Records Awaiting Correction By Employee</h1>
    <p class="content-info">The following records have been rejected and are pending correction by the employee.<br/>
        Once the employee resubmits the record it will appear in the 'Records Needing Approval' section.</p>
    <table class="ess-table approve-attendance-rec-table">
        <thead>
        <tr>
            <th>Details</th>
            <th colspan="2">Employee</th>
            <th>Pay Period</th>
            <th>Approval Date</th>
            <th>Work</th>
            <th>Holiday</th>
            <th>Vacation</th>
            <th>Personal</th>
            <th>Sick</th>
            <th>Misc</th>
            <th>Total Hours</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><a href="">Show</a></td>
            <td><div class="small-employee-profile-pic">&nbsp;</div></td>
            <td class="name-column">Rejected Employee</td>
            <td>04/24/14 - 05/07/14</td>
            <td>05/05/14 3:34 PM</td>
            <td>70</td><td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        </tbody>
    </table>
</section>

<section class="content-container">
    <h1>T&A Records Pending Approval By Personnel</h1>
    <p class="content-info">The following records have been recently approved and are awaiting approval by personnel.</p>
    <table class="ess-table approve-attendance-rec-table">
        <thead>
        <tr>
            <th>Details</th>
            <th colspan="2">Employee</th>
            <th>Pay Period</th>
            <th>Approval Date</th>
            <th>Work</th>
            <th>Holiday</th>
            <th>Vacation</th>
            <th>Personal</th>
            <th>Sick</th>
            <th>Misc</th>
            <th>Total Hours</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><a href="">Show</a></td>
            <td><div style="background:url('https://avatars0.githubusercontent.com/u/2687188?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
            <td class="name-column">A. Islam</td>
            <td>04/24/14 - 05/07/14</td>
            <td>05/05/14 3:34 PM</td>
            <td>70</td><td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        </tbody>
    </table>
</section>

<section class="content-container">
    <h1>T&A Records Not Submitted</h1>
    <p class="content-info">The following table lists records that have not yet been submitted by the employee.<br/>
    You can preview the state of the record by clicking 'Show' in the Preview column.</p>
    <table class="ess-table approve-attendance-rec-table">
        <thead>
        <tr>
            <th>Details</th>
            <th colspan="2">Employee</th>
            <th>Pay Period</th>
            <th>Work</th>
            <th>Holiday</th>
            <th>Vacation</th>
            <th>Personal</th>
            <th>Sick</th>
            <th>Misc</th>
            <th>Total Hours</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><a>Show</a></td>
            <td><div class="small-employee-profile-pic">&nbsp;</div></td>
            <td>Employee 1 Fullname</td>
            <td>04/24/14 - 05/07/14</td>
            <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        <tr>
            <td><a>Show</a></td>
            <td><div style="background:url('https://avatars0.githubusercontent.com/u/330720?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
            <td>Employee 2 Fullname</td>
            <td>04/24/14 - 05/07/14</td>
            <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        <tr>
            <td><a>Show</a></td>
            <td><div style="background:url('https://avatars2.githubusercontent.com/u/94740?s=30')" class="small-employee-profile-pic">&nbsp;</div></td>
            <td>Employee 3 Fullname</td>
            <td>04/24/14 - 05/07/14</td>
            <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        <tr>
            <td><a>Show</a></td>
            <td><div class="small-employee-profile-pic">&nbsp;</div></td>
            <td>Employee 4 Fullname</td>
            <td>04/24/14 - 05/07/14</td>
            <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        <tr>
            <td><a>Show</a></td>
            <td><div class="small-employee-profile-pic">&nbsp;</div></td>
            <td>Employee 5 Fullname</td>
            <td>04/24/14 - 05/07/14</td>
            <td>70</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>70</td>
        </tr>
        </tbody>
    </table>
</section>

<section id="rejection-dialog" title="Reject Time Record">
    <p class="content-info no-bottom-margin">Explain the reason for rejecting the time record.</p>
    <textarea style="resize:none;margin:10px;width:375px;height:100px;" placeholder="Reason for rejection"></textarea>
    <div style="padding:.4em;background:#eee;text-align: center;">
        <input class="reject-button" type="button" value="Reject Record"/>
        <input style="float:right;" onclick="$('#rejection-dialog').dialog('close');" class="neutral-button" type="button" value="Cancel"/>
    </div>
</section>

<script>
    $("#review-records-modal").dialog({
        width: 1100,
        modal: true,
        autoOpen: false
    });

    $("#rejection-dialog").dialog({
        modal: true,
        autoOpen: false,
        width: 400
    });

    $("#review-sel-records-btn").click(function(){
        $("#review-records-modal").dialog("open");
    });
</script>

