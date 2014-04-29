<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-container">
    <p class="content-info">Display accrual information for year &nbsp;
    <select>
        <option selected="selected">2014</option>
        <option>2013</option>
    </select></p>
</section>

<section class="content-container">
    <h1>Annual Accrual Usage</h1>
    <p class="content-info">Depicts the accrual hours used during each pay period in 2014.</p>
    <div id="accrual-usage-stacked-bar-plot"></div>
</section>

<section class="content-container">
    <h1>Personal/Vacation Hours Remaining</h1>
    <p class="content-info">Depicts the personal and vacation hours that were left over at the end of each pay period in 2014.</p>
    <div id="accrual-rem-stacked-area-plot"></div>
</section>

<script>
    $('#accrual-usage-stacked-bar-plot').highcharts({
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
    });
</script>

<section class="content-container">
    <h1 class="teal">Detailed Accrual History</h1>
    <p class="content-info">The hours accrued, used, and remaining per pay period are listed in the table below.</p>
    <table id="detail-acc-history-table">
        <thead>
            <tr>
                <th colspan="2">Period</th>
                <th colspan="2">Rates</th>
                <th colspan="3" style="background:#006B80;color:white;">Personal</th>
                <th colspan="3" style="background:#799933;color:white;">Vacation</th>
                <th colspan="3" style="background:#ab7b1e;color:white;">Sick</th>
                <th colspan="1" style="background:#e64727;color:white;">Misc</th>
            </tr>
            <tr>
                <th>Dates</th>
                <th>Number</th>
                <th>Vac</th>
                <th>Sick</th>
                <th>Acc</th>
                <th>Used</th>
                <th>Rem</th>
                <th>Acc</th>
                <th>Used</th>
                <th>Rem</th>
                <th>Acc</th>
                <th>Used</th>
                <th>Rem</th>
                <th>Used</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>1/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>2/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>3/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>4/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>5/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>6/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>7/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>8/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
            <tr>
                <td>9/1/14 - 1/15/14</td><td>3</td><td>0</td><td>3</td><td>4</td><td>5</td><td>6</td><td>33</td><td>34</td><td>21</td><td>23</td><td>12</td><td>23</td><td>0</td>
            </tr>
        </tbody>
    </table>

</section>