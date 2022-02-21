function addDecision() {
    $.ajax({
        type: "POST",
        url: "/admin/decisions/add",
        cache: false,
        timeout: 600000,
        data: JSON.stringify({
            name: $("#name").val(),
            answer: $("#answer").val()
        }),
        dataType: "json",
        contentType: "application/json",
        async: true,
        success: function () {
                window.location.reload();
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}

function deleteDecision(id) {
    $.ajax({
        type: "DELETE",
        url: "/admin/decisions/delete/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function () {
            $("#decision_row_" + id).remove();
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}

function editDecision(id) {
    $("#decision_name_" + id).prop("readonly", false);
    $("#decision_answer_" + id).prop("readonly", false);
    let edit = $("#decision_edit_" + id).prop("src", "/img/ok.png");
    edit.attr("onclick", "updateDecision(" + id + ")");
}

function updateDecision(id) {
    $.ajax({
        type: "POST",
        url: "/admin/decisions/update",
        cache: true,
        timeout: 600000,
        data: JSON.stringify({
            id: id,
            name: $("#decision_name_" + id).val(),
            answer: $("#decision_answer_" + id).val()
        }),
        dataType: "json",
        contentType: "application/json",
        async: true,
        success: function () {
            $("#decision_name_" + id).prop("readonly", true);
            $("#decision_answer_" + id).prop("readonly", true);
            $("#decision_edit_" + id).prop("src", "/img/edit.png");
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}
