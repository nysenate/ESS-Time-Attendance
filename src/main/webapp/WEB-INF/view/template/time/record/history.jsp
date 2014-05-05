<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-container">
    <h1>Active Attendance Records</h1>
    <p class="content-info">The following time records are in progress or awaiting submission.
    <br/>You can edit a record by clicking the 'Edit' link to
    the right.</p>
    <table id="attendance-active-table" class="ess-table attendance-listing-table">
        <thead>
        <tr>
            <th>Date Range</th>
            <th>Pay Period</th>
            <th>Status</th>
            <th>Work</th>
            <th>Holiday</th>
            <th>Vacation</th>
            <th>Personal</th>
            <th>Sick Emp</th>
            <th>Sick Fam</th>
            <th>Misc</th>
            <th>Total</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
            <tr>
                <td>4/24/14 - 5/7/14</td>
                <td>2</td><td>In Progress</td>
                <td>32</td><td>0</td><td>2</td><td>3</td><td>3</td><td>0</td><td>0</td><td>40</td>
                <td><a href="${ctxPath}/time/record/entry" class="action-link">Edit</a></td>
            </tr>
        </tbody>
    </table>
</section>

<section class="content-container">
    <h1>Prior Attendance Records</h1>
    <div class="content-controls">
        <p class="content-info" style="margin-bottom:0;">
            View attendance records for year &nbsp;
            <select>
                <option selected="selected">2014</option>
                <option>2013</option>
                <option>2012</option>
            </select>
        </p>
    </div>
    <p class="content-info" style="">Time records that have been submitted for pay periods during 2014 are listed in
    the table below.<br/>You can view details about each pay period by clicking the 'View Details' link to the right.</p>
    <table id="attendance-history-table" class="ess-table attendance-listing-table">
        <thead>
            <tr>
                <th>Date Range</th>
                <th>Pay Period</th>
                <th>Status</th>
                <th>Work</th>
                <th>Holiday</th>
                <th>Vacation</th>
                <th>Personal</th>
                <th>Sick Emp</th>
                <th>Sick Fam</th>
                <th>Misc</th>
                <th>Total</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>1/1/14 - 1/1/14</td>
                <td>20B</td><td>Approved By Personnel</td><td>0</td><td>7</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>1/2/14 - 1/15/14</td>
                <td>21</td><td>Approved By Personnel</td><td>67</td><td>0</td><td>0</td><td>0</td><td>3</td><td>0</td><td>0</td><td>70</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>1/16/14 - 1/29/14</td>
                <td>21</td><td>Approved By Personnel</td><td>67</td><td>2</td><td>0</td><td>0</td><td>1</td><td>0</td><td>0</td><td>70</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>1/30/14 - 2/12/14</td>
                <td>21</td><td>Approved By Personnel</td><td>71</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>71</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>2/13/14 - 2/26/14</td>
                <td>21</td><td>Approved By Personnel</td><td>72</td><td>0</td><td>0</td><td>0</td><td>3</td><td>0</td><td>0</td><td>75</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>2/27/14 - 3/12/14</td>
                <td>21</td><td>Approved By Personnel</td><td>65</td><td>0</td><td>0</td><td>5</td><td>0</td><td>0</td><td>0</td><td>70</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>3/13/14 - 3/26/14</td>
                <td>21</td><td>Approved By Personnel</td><td>68</td><td>0</td><td>0</td><td>2</td><td>0</td><td>0</td><td>0</td><td>70</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr>
                <td>3/27/14 - 4/09/14</td>
                <td>21</td><td>Submitted</td><td>70</td><td>0</td><td>0</td><td>0</td><td>3</td><td>0</td><td>0</td><td>70</td>
                <td><a class="action-link">View Details</a></td>
            </tr>
            <tr style="border-top:2px solid teal;">
                <td colspan="2"></td>
                <td><strong>Annual Totals</strong></td>
                <td><strong>334</strong></td>
                <td><strong>0</strong></td><td><strong>0</strong></td><td><strong>7</strong></td>
                <td><strong>7</strong></td><td><strong>0</strong></td><td><strong>0</strong></td><td><strong>390</strong></td>
                <td></td>
            </tr>
        </tbody>
    </table>

    <div id="dialog" title="Attendance Details">
        <p class="content-info" style="margin-bottom:0;">The time entries for pay period X are displayed in the table below.</p>
        <table class="attendance-entry-sub-table ess-table">
            <thead>
            <tr>
                <th>Day</th>
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
            </tbody>
        </table>
    </div>

    <script>
        $("#dialog").dialog({
            autoOpen: false,
            width: '620px',
            modal: true,
            buttons: {
                Done: function() {
                    $( this ).dialog( "close" );
                }
            }
        });

        $("#attendance-history-table .action-link").click(function(e){
            $("#dialog").dialog("open");
        });
    </script>
</section>
