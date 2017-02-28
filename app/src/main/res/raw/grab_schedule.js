// This script is loaded into "https://edziekanat.zut.edu.pl/*"
// In WebView in WebPlanActivity
(function () {
    if (isOnLoginPage()) {
        doLogin();
    } else if (hasMultipleFieldsOfStudy()) {
        parseFieldsOfStudyAndSendToAndroid();
    } else if (isOnStartPage()) {
        location.href = '/WU/PodzGodzin.aspx';
    } else if (isOnSchedulePage()) {
        selectSemesterAndPrintTable();
    } else if (isOnScheduleTablePage()) {
        passScheduleTableToAndroid();
    } else {
        hideError();
    }


    function isOnLoginPage() {
        return location.pathname == '/WU/' || isOnPage('Logowanie');
    }

    function doLogin() {
        var loginField = document.querySelector('input[id$="_txtIdent"]');
        loginField.value = android.getLogin();
        var passwordField = document.querySelector('input[id$="_txtHaslo');
        passwordField.value = android.getPassword();
        var loginButton = document.querySelector('input[id$="_butLoguj');
        loginButton.click();
        var errorField = document.querySelector('[id$="_lblMessage');
        if (errorField) {
            android.onLoginError(errorField.innerHTML);
        }
    }

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
                android.onServerDataError();
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
        android.onTableGrabbed(JSON.stringify(tableDump));
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

})();

