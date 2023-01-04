/**
 *
 */
$(document).ready(function () {
    $("#btnClear").on("click", function (e) {
        e.preventDefault();
        $("#name").text("");
        window.location = "/dashboard/teachers";
    });

    $('.delBtn').on('click', function (event) {
        event.preventDefault();
        let link = $(this);
        let teacherName = link.attr("teacherName");
        let href = $(this).attr('href');
        $('#teacherDeleteModal #delRef').attr('href', href);
        $("#confirmText").html("Are you sure you want to delete teacher with name \<strong\>" + teacherName + "\<\/strong\>? This action cannot be undone and you will be unable to recover any data. ");
    });
});

$("#teacherDeleteModal").on("hidden.bs.modal", function(){
    $(".modal-delete-body").html("<span id=\"confirmText\"></span>");
    $(".modal-view-body").html("<p id=\"teacherFirstName\">Details</p>\n" +
        "                <p id=\"teacherLastName\">Details</p>\n" +
        "                <p id=\"teacherEmail\">Details</p>\n" +
        "                <p id=\"teacherAge\">Details</p>\n" +
        "                <p id=\"teacherFieldOfStudy\">Details</p>\n" +
        "                <p id=\"teacherStudents\">List of assigned students:</p>\n" +
        "                <div class=\"col-lg-12\" id=\"students-list\">\n" +
        "                </div>");
});

$("#teacherViewModal").on("hidden.bs.modal", function(){
    $(".modal-view-body").html("<p id=\"teacherFirstName\">Details</p>\n" +
        "                <p id=\"teacherLastName\">Details</p>\n" +
        "                <p id=\"teacherEmail\">Details</p>\n" +
        "                <p id=\"teacherAge\">Details</p>\n" +
        "                <p id=\"teacherFieldOfStudy\">Details</p>\n" +
        "                <p id=\"teacherStudents\">List of assigned students:</p>\n" +
        "                <div class=\"col-lg-12\" id=\"students-list\">\n" +
        "                </div>");
});
function changePageSize() {
    $("#teacherForm").submit();
}

function openTeacherViewModal(id) {
    $.ajax({
        url: "/api/teachers/"+ id,
        success: function(data){
            let myJSON = JSON.stringify(data);
            let tmpData = JSON.parse(myJSON);
            let formattedJson = JSON.stringify(tmpData, null, ' ');
            $('#teacherViewModal .modal-title').html('Viewing teacher with ID: ' + data.id);
            $('#teacherFirstName').html('First Name: ' + data.firstName);
            $('#teacherLastName').html('Last Name: ' + data.lastName);
            $('#teacherEmail').html('Email: ' + data.email);
            $('#teacherAge').html('Age: ' + data.age);
            $('#teacherFieldOfStudy').html('Subject: ' + data.subject);

            let listDiv = document.getElementById('students-list');
            let ul = document.createElement('ul');
            for (let i = 0; i < tmpData.studentsList.length; i++) {
                let counter = tmpData.studentsList[i];
                let li = document.createElement('li');
                li.innerHTML = counter.firstName + ' ' + counter.lastName;   // Use innerHTML to set the text
                ul.appendChild(li);
            }
            listDiv.appendChild(ul);
        }
    });
}
