var essTime = angular.module('essTime');

/**
 * Contains common time record functions that are used across several different controllers
 */
essTime.service('RecordUtils', [function () {

    return {
        getDailyTotal: getDailyTotal,
        calculateDailyTotals: calculateDailyTotals,
        getTotal: getTotal,
        getRecordTotals: getRecordTotals
    };

    // Get the total used hours for a single time entry
    function getDailyTotal(entry) {
        return +(entry.workHours) + +(entry.travelHours) + +(entry.holidayHours) + +(entry.vacationHours) +
            +(entry.personalHours) + +(entry.sickEmpHours) + +(entry.sickFamHours) + +(entry.miscHours);
    }

    // Calculate and add the daily total as a field in each time entry within a record
    function calculateDailyTotals (record) {
        for (var i = 0, entries = record.timeEntries; i < entries.length; i++) {
            entries[i].total = getDailyTotal(entries[i]);
        }
    }

    // Gets the total number of hours used for a specific time usage type over an entire time record
    function getTotal(record, type) {
        var total = 0;
        var entries = record.timeEntries;
        if (entries) {
            for (var i = 0; i < entries.length; i++) {
                total += +(entries[i][type] || 0);
            }
        }
        return total;
    }

    // Returns an object containing the total number of hours for each time usage type over an entire time recodr
    function getRecordTotals(record) {
        return {
            work: getTotal(record, 'workHours'),
            travel: getTotal(record, 'travelHours'),
            holiday: getTotal(record, 'holidayHours'),
            vac: getTotal(record, 'vacationHours'),
            personal: getTotal(record, 'personalHours'),
            sickEmp: getTotal(record, 'sickEmpHours'),
            sickFam: getTotal(record, 'sickFamHours'),
            misc: getTotal(record, 'miscHours'),
            total: getTotal(record, 'total')
        };
    }
}]);

