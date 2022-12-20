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

function openStudentViewModal(id) {
    $.ajax({
        url: "/api/students/"+ id,
        success: function(data){
            const myJSON = JSON.stringify(data);
            const tmpData = JSON.parse(myJSON);
            const formattedJson = JSON.stringify(tmpData, null, ' ');
            $('#studentViewModal .modal-title').html('Viewing student with ID: ' + data.id);
            $('#studentFirstName').html('First Name: ' + data.firstName);
            $('#studentLastName').html('LastName : ' + data.lastName);
            $('#studentEmail').html('Email: ' + data.email);
            $('#studentAge').html('Age: ' + data.age);
            $('#studentFieldOfStudy').html('Field of study: ' + data.fieldOfStudy);
        }
    });
}
