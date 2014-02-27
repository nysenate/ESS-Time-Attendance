<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<h1 class="hero">Time Record History</h1>

<section class="content-container">
    <h1>Accrual Usage History</h1>
    <div ess-chart
         hc-chart-type="area"
         hc-title=""
         hc-subtitle="">

    </div>
</section>

<section class="content-container">
    <h1>Pay Record History</h1>
    <p class="info-text">
        Filter by year
            <select>
                <option>2014</option>
                <option>2013</option>
            </select>
        Show Advanced Filters
    </p>
    <table id="timeRecordHistoryTable" class="ess-table">
        <thead>
            <tr>
                <th>Pay Period</th>
                <th>Status</th>
                <th style="width:65px;">Work</th>
                <th style="width:65px;">Holiday</th>
                <th style="width:65px;">Vacation</th>
                <th style="width:65px;">Personal</th>
                <th style="width:65px;">Sick Employee</th>
                <th style="width:65px;">Sick Family</th>
                <th style="width:65px;">Misc</th>
                <th style="width:65px;">Total</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>02/13/14 - 02/26/14</td>
                <td>NOT SUBMITTED</td>
                <td>0</td>
                <td>7</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>4</td>
                <td>11</td>
            </tr>
            <tr>
                <td>02/1/14 - 02/13/14</td>
                <td>APPROVED BY PERSONNEL</td>
                <td>0</td>
                <td>7</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>4</td>
                <td>11</td>
            </tr>
        </tbody>
    </table>
</section>