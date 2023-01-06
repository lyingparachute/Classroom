/**
 *
 */
$(document).ready(function () {
    $("#btnClear").on("click", function (e) {
        e.preventDefault();
        $("#name").text("");
        window.location = "/dashboard/students";
    });

    $('.delBtn').on('click', function (event) {
        event.preventDefault();
        let link = $(this);
        let name = link.attr("name");
        let href = $(this).attr('href');
        $('#deleteModal #delRef').attr('href', href);
        $("#confirmText").html("Are you sure you want to delete subject with name \<strong\>" + name + "\<\/strong\>? This action cannot be undone and you will be unable to recover any data. ");
    });
});

function changePageSize() {
    $("#subjectForm").submit();
}