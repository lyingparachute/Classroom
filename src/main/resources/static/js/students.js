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

    $("#studentViewModal").on("hidden.bs.modal", function(){
        $(".modal-body").html("<p id=\"studentFirstName\">Details</p>\n" +
            "                <p id=\"studentLastName\">Details</p>\n" +
            "                <p id=\"studentEmail\">Details</p>\n" +
            "                <p id=\"studentAge\">Details</p>\n" +
            "                <p id=\"studentFieldOfStudy\">Details</p>\n" +
            "                <p id=\"studentTeachers\">List of assigned teachers:</p>\n" +
            "                <div class=\"col-lg-12\" id=\"list-puntate\">\n" +
            "                </div>");
    });

});

function changePageSize() {
    $("#studentForm").submit();
}

function openStudentViewModal(id) {
    $.ajax({
        url: "/api/students/"+ id,
        success: function(data){
            let myJSON = JSON.stringify(data);
            let tmpData = JSON.parse(myJSON);
            let formattedJson = JSON.stringify(tmpData, null, ' ');
            $('#studentViewModal .modal-title').html('Viewing student with ID: ' + data.id);
            $('#studentFirstName').html('First Name: ' + data.firstName);
            $('#studentLastName').html('Last Name: ' + data.lastName);
            $('#studentEmail').html('Email: ' + data.email);
            $('#studentAge').html('Age: ' + data.age);
            $('#studentFieldOfStudy').html('Field of study: ' + data.fieldOfStudy);

            let listDiv = document.getElementById('list-puntate');
            let ul = document.createElement('ul');
            for (let i = 0; i < tmpData.teachersList.length; i++) {
                let counter = tmpData.teachersList[i];
                let li = document.createElement('li');
                li.innerHTML = counter.firstName + ' ' + counter.lastName;   // Use innerHTML to set the text
                ul.appendChild(li);
            }
            listDiv.appendChild(ul);
        }
    });
}
