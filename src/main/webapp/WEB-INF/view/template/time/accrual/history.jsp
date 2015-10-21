<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccrualHistoryCtrl" ng-init="showDialog = false">
    <div class="time-attendance-hero">
        <h2>Accrual Summary and Projections</h2>
    </div>
    <div class="content-container content-controls">
        <p class="content-info">Filter By Year &nbsp;
            <select ng-model="state.selectedYear" ng-change="getAccSummaries(state.selectedYear)"
                    ng-options="year for year in state.activeYears">
            </select>
        </p>
    </div>

    <div loader-indicator ng-show="state.searching === true"></div>

    <ess-notification ng-show="state.searching === false && state.error !== null" level="warn"
                     title="{{state.error.title}}" message="{{state.error.message}}">
    </ess-notification>

    <div class="content-container" ng-show="state.searching === false && state.accSummaries[state.selectedYear]">
        <h1 class="teal">Running Accrual Summary</h1>
        <p class="content-info">The hours accrued, used, and remaining are listed in the table below.<br/>
            The accrued, used, and available hours in each column are a running total from the start of the year.</p>
        <div class="padding-10">
            <table class="detail-acc-history-table" float-thead="floatTheadOpts" ng-model="state.accSummaries[state.selectedYear]">
                <thead>
                <tr>
                    <th colspan="2">Pay Period</th>
                    <th colspan="4" class="">Personal</th>
                    <th colspan="5" class="">Vacation</th>
                    <th colspan="5" class="">Sick</th>
                    <%--<th colspan="1" class="misc">Misc</th>--%>
                </tr>
                <tr>
                    <th>#</th>
                    <th>End Date</th>
                    <th class="personal">Accrued</th>
                    <th class="personal">Used</th>
                    <th class="personal">Used Ytd</th>
                    <th class="personal">Avail</th>
                    <th class="vacation">Rate</th>
                    <th class="vacation">Accrued</th>
                    <th class="vacation">Used</th>
                    <th class="vacation">Used Ytd</th>
                    <th class="vacation">Avail</th>
                    <th class="sick">Rate</th>
                    <th class="sick">Accrued</th>
                    <th class="sick">Used</th>
                    <th class="sick">Used Ytd</th>
                    <th class="sick">Avail</th>
                    <%--<th>Used</th>--%>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="record in state.accSummaries[state.selectedYear]" ng-class="{'highlighted': record.payPeriod.current}">
                    <td>{{record.payPeriod.payPeriodNum}}</td>
                    <td>{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
                    <td>{{record.personalAccruedYtd}}</td>
                    <td>{{record.personalUsedDelta}}</td>
                    <td>{{record.personalUsed}}</td>
                    <td>{{record.personalAvailable}}</td>
                    <td>{{record.vacationRate}}</td>
                    <td>{{record.vacationAccruedYtd + record.vacationBanked}}</td>
                    <td>{{record.vacationUsedDelta}}</td>
                    <td>{{record.vacationUsed}}</td>
                    <td>{{record.vacationAvailable}}</td>
                    <td>{{record.sickRate}}</td>
                    <td>{{record.sickAccruedYtd}}</td>
                    <td>{{record.sickUsedDelta}}</td>
                    <td>{{record.empSickUsed + record.famSickUsed}}</td>
                    <td>{{record.sickAvailable}}</td>
                    <%--<td>{{record.miscUsed}}</td>--%>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</section>