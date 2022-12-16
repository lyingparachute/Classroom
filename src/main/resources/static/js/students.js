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
        let link = $(this);
        let studentName = link.attr("studentName");
        let href = $(this).attr('href');
        $('#studentDeleteModal #delRef').attr('href', href);
        $("#confirmText").html("Are you sure you want to delete student with name \<strong\>" + studentName + "\<\/strong\>? This action cannot be undone and you will be unable to recover any data. ");
    });
});

function changePageSize() {
    $("#studentForm").submit();
}
