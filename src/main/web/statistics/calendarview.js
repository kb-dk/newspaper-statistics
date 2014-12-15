// TODO document/comment functions and code

var batchid = location.search.split('batchid=')[1];
var datefilepath = "data/" + batchid + '/date-statistics.xml';

function loadDateStatistics() {
    var films;
    var partialDates = [];
    var startdate;
    var lastdate;
    $.get(datefilepath, function(xml) {
        films = [];
        fuzzyDates = [];
        $(xml).find('Film').each(function() {
            var filmname = $(this).attr('name');
            films[filmname] = []
            $(this).find('Edition-dates').each(function() {
                $(this).children().each(function() {
                    date = $(this).attr("name");
                    var dashes = date.match(/-/g);
                    if (dashes != null && dashes.length === 2) {
                        films[filmname].push(date);
                        if (date < startdate || startdate == undefined) startdate = date;
                        if (date > lastdate || lastdate == undefined) lastdate = date;
                    } else {
                        partialDates.push(date)
                    }
                });
            });
        });

        minDate = new Date (startdate);
        maxDate = new Date (lastdate);
        initDateRange(minDate, maxDate);
        initCalendar(films, minDate, maxDate);
        initPartialDates({dates: partialDates});
    });
}

function initCalendar(films, minDate,  maxDate) {
    $("#calendar-container").datepicker({
        numberOfMonths: [3,2],
        changeMonth: true,
        changeYear: true,
        minDate: minDate,
        maxDate: maxDate,
        dateFormat: 'yy-mm-dd', onSelect: function() {
            date = $(this).datepicker('getDate');
            filterTree(formatDate(date));
        },
        beforeShowDay: function(date) {
            var r = [true, "", ''];
            if (films  == undefined) {} else {
                var search = formatDate(date);
                for (var film in films) {
                    if ($.inArray(search, films[film]) > -1) {
                        r[1] = "dp-highlight";
                        r[2] = film;
                        break;
                    }
                }
            }
            return r;
        }
    });
    $("#calendar-container").datepicker("setDate", minDate);
}

function initPartialDates(parameters) {
    var editiondates = parameters.dates;
    $("#fuzzydates").append(editiondates.toString());
}

function initDateRange(minDate,  maxDate) {
    $("#calender-headline").append(' (' + formatDate(minDate) + ' - ' + formatDate(maxDate) + ')');
}

function formatDate(date) {
    return $.datepicker.formatDate('yy-mm-dd', date);
}