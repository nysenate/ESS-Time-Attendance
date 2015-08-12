<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccrualHistoryCtrl" >
    <div class="content-container content-controls">
        <p class="content-info">Display accrual information for year &nbsp;
            <select ng-model="state.selectedYear" ng-change="getAccSummaries(state.selectedYear)"
                    ng-options="year for year in state.activeYears">
            </select>
        </p>
    </div>

    <div class="content-container">
        <h1 class="teal">Running Accrual Summary</h1>
        <p class="content-info">The hours accrued, used, and remaining are listed in the table below.<br/>
            The accrued, used, and available hours in each column are a running total from the start of the year.</p>
        <table class="detail-acc-history-table" float-thead="floatTheadOpts">
            <thead>
            <tr>
                <th colspan="2">Pay Period</th>
                <th colspan="3" class="personal">Personal</th>
                <th colspan="4" class="vacation">Vacation</th>
                <th colspan="4" class="sick">Sick</th>
                <%--<th colspan="1" class="misc">Misc</th>--%>
            </tr>
            <tr>
                <th>Number</th>
                <th>End Date</th>
                <th>Accrued</th>
                <th>Used</th>
                <th>Avail</th>
                <th>Rate</th>
                <th>Accrued</th>
                <th>Used Ytd</th>
                <th>Avail</th>
                <th>Rate</th>
                <th>Accrued</th>
                <th>Used Ytd</th>
                <th>Avail</th>
                <%--<th>Used</th>--%>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="record in state.accSummaries" ng-class="{'highlighted': record.current}">
                <td>{{record.payPeriod.payPeriodNum}}</td>
                <td>{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
                <td>{{record.personalAccruedYtd}}</td>
                <td>{{record.personalUsed}}</td>
                <td>{{record.personalAvailable}}</td>
                <td>{{record.vacationRate}}</td>
                <td>{{record.vacationAccruedYtd + record.vacationBanked}}</td>
                <td>{{record.vacationUsed}}</td>
                <td>{{record.vacationAvailable}}</td>
                <td>{{record.sickRate}}</td>
                <td>{{record.sickAccruedYtd}}</td>
                <td>{{record.empSickUsed + record.famSickUsed}}</td>
                <td>{{record.sickAvailable}}</td>
                <%--<td>{{record.miscUsed}}</td>--%>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        /*$('#accrual-usage-stacked-bar-plot').highcharts({
         colors: ['#006B80', '#799933', '#d19525', '#e64727' ],
         chart: {
         type: 'column',
         height: 250
         },
         credits: {
         enabled: false
         },
         title: {
         text: ''
         },
         xAxis: {
         categories: ['1/1/14', '2/1/14', '3/1/14', '4/1/14', '5/1/14', '6/1/14', '7/1/14', '8/1/14'],
         title: {
         text: 'Pay Period End Date'
         }
         },
         yAxis: {
         min: 0,
         title: {
         text: 'Hours Used'
         },
         stackLabels: {
         enabled: true,
         style: {
         fontWeight: 'bold',
         color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
         }
         },
         gridLineColor: '#ddd',
         gridLineDashStyle: 'longdash'
         },
         legend: {
         borderWidth: 0
         },
         tooltip: {
         formatter: function() {
         return '<b>'+ this.x +'</b><br/>'+
         this.series.name +': '+ this.y +'<br/>'+
         'Total: '+ this.point.stackTotal;
         }
         },
         plotOptions: {
         area: { stacking: 'normal'},
         column: {
         stacking: 'normal',
         dataLabels: {
         enabled: false
         }
         }
         },
         series: [{
         name: 'Personal',
         data: [0, 3, 2, 0, 2, 0, 1, 0]
         }, {
         name: 'Vacation',
         data: [2, 0, 0, 2, 1, 0, 0, 1]
         }, {
         name: 'Sick',
         data: [3, 0, 0, 2, 0, 0, 0, 0]
         }, {
         name: 'Misc',
         data: [0, 0, 0, 0, 0, 0, 0, 0]
         }]
         });

         $('#accrual-rem-stacked-area-plot').highcharts({
         colors: ['#006B80', '#799933', '#d19525', '#e64727' ],
         chart: {
         type: 'area',
         height: 250
         },
         credits: {
         enabled: false
         },
         title: {
         text: ''
         },
         xAxis: {
         categories: ['1/1/14', '2/1/14', '3/1/14', '4/1/14', '5/1/14', '6/1/14', '7/1/14', '8/1/14'],
         title: {
         text: 'Pay Period End Date'
         }
         },
         yAxis: {
         min: 0,
         title: {
         text: 'Hours Remaining'
         },
         stackLabels: {
         enabled: true,
         style: {
         fontWeight: 'bold',
         color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
         }
         },
         gridLineColor: '#ddd',
         gridLineDashStyle: 'longdash'
         },
         legend: {
         borderWidth: 0
         },
         tooltip: {
         formatter: function() {
         return '<b>'+ this.x +'</b><br/>'+
         this.series.name +': '+ this.y +'<br/>'+
         'Total: '+ this.point.stackTotal;
         }
         },
         plotOptions: {
         area: { stacking: 'normal'},
         column: {
         stacking: 'normal',
         dataLabels: {
         enabled: false
         }
         }
         },
         series: [{
         name: 'Personal',
         data: [35, 32, 28, 28, 22, 22, 21, 21]
         }, {
         name: 'Vacation',
         data: [39.75, 43, 40, 43, 47, 41, 44, 47]
         }]
         }); */
    </script>
</section>