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
        let studentName = link.attr("name");
        let href = $(this).attr('href');
        $('#deleteModal #delRef').attr('href', href);
        $("#confirmText").html("Are you sure you want to delete student with name \<strong\>" + studentName + "\<\/strong\>? This action cannot be undone and you will be unable to recover any data. ");
    });
});

function changePageSize() {
    $("#studentForm").submit();
}

$("#deleteModal").on("hidden.bs.modal", function () {
    $(".modal-delete-body").html("<span id=\"confirmText\"></span>");
});