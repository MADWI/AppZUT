// This script is loaded into "https://edziekanat.zut.edu.pl/*"
// In WebView in WebPlanActivity
(function () {
    if (hasMultipleFieldsOfStudy()) {
        parseFieldsOfStudyAndSendToAndroid();
    } else if (isOnStartPage()) {
        window.location = location.protocol + '//' + location.host + '/WU/PodzGodzin.aspx';
    } else if (isOnSchedulePage()) {
        selectSemesterAndPrintTable();
    } else if (isOnSchedulePage()) {
        passScheduleTableToAndroid();
    } else {
        hideError();
    }
})();

function hasMultipleFieldsOfStudy() {
    return isOnPage("KierunkiStudiow");
}

function isOnPage(page) {
    return location.pathname.indexOf(page) != -1
}

function isOnStartPage() {
    return isOnPage("Ogloszenia");
}

function isOnSchedulePage() {
    return isOnPage("PodzGodzin");
}

function isOnScheduleTablePage() {
    return isOnPage("PodzGodzDruk");
}

function parseFieldsOfStudyAndSendToAndroid() {
    var fieldsTable = document.getElementById("ctl00_ctl00_ContentPlaceHolder_RightContentPlaceHolder_rbKierunki")
    var fieldsNames = [];
    var fieldsIds = [];
    for (var i = 0; i < fieldsTable.rows.length; i++) {
        var row = fieldsTable.rows[i];
        var field = row.cells[0];
        var name = field.textContent;
        fieldsNames.push(name);
        var id = $(field.innerHTML).attr('id');
        fieldsIds.push(id);
    }
    android.chooseFieldOfStudy(fieldsNames, fieldsIds);
}

function chooseFieldOfStudyById(id) {
    var fieldOfStudy = document.getElementById(id);
    fieldOfStudy.click();
    var selectButton = document.querySelector('input[id$="_Button1"]');
    selectButton.click();
}

function selectSemesterAndPrintTable() {
    var semesterCheckbox = document.querySelector('input[id$="_rbJak_2"]');
    if (!semesterCheckbox.checked) {
        semesterCheckbox.click();
    } else {
        // Overwrite window.open to load content in same WebView
        window.open = function (url) {
            location.href = url;
        };
        var printButton = document.querySelector('input[id$="_btDrukuj"]');
        if (!printButton) {
            android.serverDataError();
        } else {
            printButton.onclick();
        }
    }
}

function passScheduleTableToAndroid() {
    var table = document.querySelector('table');

    // Build array of rows
    var tableDump =
        [].map.call(table.rows, function (row) {
            // Rows are arrays of cell contents
            return [].map.call(row.cells, function (cell) {
                return cell.textContent;
            });
        });

    // Pass result to Java to shouldOverrideUrlLoading
    location.href = 'js-grabbed-table:' + encodeURI(JSON.stringify(tableDump));
}

function hideError() {
    var loginError = document.querySelector('.login_criteria ~ * .error_label');
    if (
        loginError && (
            loginError.textContent.indexOf("czas bezczyn") != -1 ||
            loginError.textContent.indexOf("idle timeout") != -1
        )
    ) {
        login_error.style.visibility = 'hidden';
    }
}

