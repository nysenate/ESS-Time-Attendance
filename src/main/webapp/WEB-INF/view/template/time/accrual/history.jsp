<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-container content-controls">
    <p class="content-info">Display accrual information for year &nbsp;
    <select>
        <option selected="selected">2014</option>
        <option>2013</option>
    </select></p>
</section>

<section class="content-container hidden">
    <h1>Annual Accrual Usage</h1>
    <p class="content-info">Depicts the accrual hours used during each pay period in 2014.</p>
    <div id="accrual-usage-stacked-bar-plot"></div>
</section>

<section class="content-container hidden">
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
    <h1 class="teal">Accrual Summary By Period</h1>
    <p class="content-info">The hours accrued, used, and remaining per pay period are listed in the table below.</p>
    <table id="detail-acc-history-table">
        <thead>
            <tr>
                <th colspan="2">Period</th>
                <th colspan="3" style="background:#006B80;color:white;border-right:none;">Personal</th>
                <th colspan="4" style="background:#799933;color:white;border-right:none;">Vacation</th>
                <th colspan="4" style="background:#ab7b1e;color:white;border-right:none;">Sick</th>
                <th colspan="1" style="background:#e64727;color:white;border-right:none;">Misc</th>
            </tr>
            <tr>
                <th>Dates</th>
                <th>Number</th>
                <th>Acc</th>
                <th>Used</th>
                <th>Rem</th>
                <th>Rate</th>
                <th>Acc</th>
                <th>Used</th>
                <th>Rem</th>
                <th>Rate</th>
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

<section class="content-container">
    <h1 class="teal">Accrual Usage Details</h1>
    <p class="content-info">Dates during which accruals were used are listed in the table below.</p>
    <table id="accrual-usage-details-table">
        <thead>
            <tr>
                <th>Date</th>
                <th>Pay Period</th>
                <th>Work Hours</th>
                <th style="background:#006B80;color:white;border-right:none;">Personal</th>
                <th style="background:#799933;color:white;border-right:none;">Vacation</th>
                <th style="background:#ab7b1e;color:white;border-right:none;">Sick Emp</th>
                <th style="background:#ab7b1e;color:white;border-right:none;">Sick Fam</th>
                <th style="background:#e64727;color:white;border-right:none;">Misc</th>
                <th>Holiday</th>
                <th>Total</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Fri 1/12/14</td><td>2</td><td>6</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>7</td>
            </tr>
            <tr>
                <td>Tue 2/2/14</td><td>2</td><td>6</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>7</td>
            </tr>
            <tr>
                <td>Thu 4/13/14</td><td>2</td><td>6</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>7</td>
            </tr>
            <tr>
                <td>Mon 5/1/14</td><td>2</td><td>6</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>7</td>
            </tr>
            <tr>
                <td>Wed 6/12/14</td><td>2</td><td>6</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>7</td>
            </tr>
        </tbody>
    </table>
</section>
