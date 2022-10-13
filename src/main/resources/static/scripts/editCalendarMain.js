// ########## Init logic ##########
document.addEventListener("DOMContentLoaded", function () {
    // Create starting view
    toggleSubsectionAddEditCalendar(true);
    toggleSubsectionAddEditEntry(true);

    // Init onClick events for buttons
    document.querySelector("#edit-calendar-button").onclick = () =>
        openAddEditCalendarSubsection(false);

    document.querySelector("#add-calendar-button").onclick = () =>
        openAddEditCalendarSubsection(true);

    // TODO delete calendar button click

    document.querySelector("#save-calendar-button").onclick = () =>
        closeAddEditCalendarSubsection();

    document.querySelector("#cancel-calendar-button").onclick = () =>
        closeAddEditCalendarSubsection();

    document.querySelector("#edit-entry-button").onclick = () =>
        openAddEditEntrySubsection(false);

    document.querySelector("#add-entry-button").onclick = () =>
        openAddEditEntrySubsection(true);

    // TODO delete calendar button click

    document.querySelector("#save-entry-button").onclick = () =>
        closeAddEditEntrySubsection();

    document.querySelector("#cancel-entry-button").onclick = () =>
        closeAddEditEntrySubsection();
});

// ########## Button onClick functions ##########
function openAddEditCalendarSubsection(isCreateMode) {
    // Visuals
    let subSectionLabel = isCreateMode ? "Hinzufügen" : "Bearbeiten";
    document.querySelector("#add-edit-calendar-label").textContent =
        subSectionLabel;

    toggleSubsectionAddEditCalendar(false);

    // TODO clear fields for adding | init fields for editing calendar
}

function closeAddEditCalendarSubsection(isToBeSaved) {
    // TODO Read input & POST/PUT request if "isToBeSaved"

    toggleSubsectionAddEditCalendar(true);

    // TODO GET all calendars request & Update Calender Table
}

function openAddEditEntrySubsection(isCreateMode) {
    // Visuals
    let subSectionLabel = isCreateMode ? "Hinzufügen" : "Bearbeiten";
    document.querySelector("#add-edit-entry-label").textContent =
        subSectionLabel;

    toggleSubsectionAddEditEntry(false);

    // Init Fields
    // TODO
}

function closeAddEditEntrySubsection(isToBeSaved) {
    // TODO Read input & POST/PUT request if "isToBeSaved"

    toggleSubsectionAddEditEntry(true);

    // TODO GET all entries request & Update Entry Table
}

// ########## Misc. helper functions ##########
function toggleSubsectionAddEditCalendar(isToBeHidden) {
    document.querySelector("#add-edit-calendar-label-div").style.display =
        isToBeHidden ? "none" : "block";
    document.querySelector("#add-edit-calendar-editing-div").style.display =
        isToBeHidden ? "none" : "grid";
}

function toggleSubsectionAddEditEntry(isToBeHidden) {
    document.querySelector("#add-edit-entry-label-div").style.display =
        isToBeHidden ? "none" : "block";
    document.querySelector("#add-edit-entry-editing-div").style.display =
        isToBeHidden ? "none" : "grid";
}
