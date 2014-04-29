<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-container">
    <h1>Accrual Usage For 2014</h1>
    <div id="accrual-usage-stacked-bar-plot"></div>
</section>

<section class="content-container">
    <h1>Accruals Remaining For 2014</h1>
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
            data: [0, 3, 4, 0, 2, 0, 1, 0]
        }, {
            name: 'Vacation',
            data: [2, 2, 3, 2, 1, 0, 0, 14]
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
            data: [39.75, 43, 40, 43, 47, 41, 44, 39]
        }]
    });
</script>

<section class="content-container">
    <h1 class="teal">Detailed Accrual History</h1>
</section>