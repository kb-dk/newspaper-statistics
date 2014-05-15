var batchid = location.search.split('batchid=')[1];
var filepath = "data/" + batchid + '/count-statistics.xml';
$(function() { new XMLTree({
    fpath: filepath, container: '#xmltree', startExpanded: false, openAtPath: 'Batch' , attrs: 'hidden'
}); });

function loadXmlStatistics() {
    jQuery.expr[':'].Contains = function(a,i,m){
        return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase())>=0;
    };

    $('#search_input').focus().keyup(function(e){
        var filter = $(this).val();
        filterTree(filter);
    });

    $('#search_input').focus().keydown(function(e){
        $('#xmltree').unhighlight();
    });

    $.get(filepath, function(xml) {
        avisID = xml.evaluate("/Batch/AvisIDs/AvisID/@name", xml, null, XPathResult.STRING_TYPE, null).stringValue;
        $("#statistics-headline").append(" " + avisID);
    });

}

function filterTree(filter) {
    if (filter) {
        $('#xmltree').find("li:not(:Contains(" + filter + "))").parent().hide();
        $('#xmltree').find("li:Contains(" + filter + ")").parent().show();
        $("#xmltree").find("li:Contains(" + filter + ")").children().show();
        $('#xmltree').highlight(filter);
    }
}
