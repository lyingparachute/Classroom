/**
 *
 */
$(document).ready(function () {
    $("#btnClear").on("click", function (e) {
        e.preventDefault();
        $("#name").text("");
        window.location = "/students";
    });

    $('.delBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $('#studentDeleteModal #delRef').attr('href', href);
    });
});

function changePageSize() {
    $("#studentForm").submit();
}
