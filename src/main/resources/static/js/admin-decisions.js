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
        success: function (data) {
            console.log(data)

            $("#tbody_decisions").append(
                "<tr id=\"decision_row_" + data.id + "\"  style=\"text-align: center; vertical-align: middle;\">\n" +
                "                                        <th>" + data.id + "</th>\n" +
                "                                        <th>\n" +
                "                                            <label>\n" +
                "                                                <input class=\"form-control\" value=\"" + data.name + "\" type=\"text\" readonly id=\"decision_name_" + data.id + "\">\n" +
                "                                            </label>\n" +
                "                                        </th>\n" +
                "                                        <th>\n" +
                "                                            <label>\n" +
                "                                                <textarea class=\"form-control\" rows=\"2\" style=\"resize: none;\" readonly id=\"decision_answer_" + data.id + "\">" + data.answer + "</textarea>\n" +
                "                                            </label>\n" +
                "                                        </th>\n" +
                "                                        <th>\n" +
                "                                            <a href=\"/user/" + data.author.username + "\">" + data.author.username + "</a>\n" +
                "                                        </th>\n" +
                "                                        <th>" + $.format.date(new Date(data.date), "dd.MM.yyyy HH:mm") + "</th>\n" +
                "                                        <th>\n" +
                "                                            <input onclick=\"editDecision(" + data.id + ");\" type=\"image\" id=\"decision_edit_" + data.id + "\" src=\"/img/edit.png\" style=\"width: 2vw\" alt=\"edit\">\n" +
                "                                        </th>\n" +
                "                                        <th>\n" +
                "                                            <input onclick=\"deleteDecision(" + data.id + ");\" type=\"image\" id=\"decision_delete_" + data.id + "\" src=\"/img/trash.png\" style=\"width: 2vw\" alt=\"delete\">\n" +
                "                                        </th>\n" +
                "                                    </tr>"
            )
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
            let decisionEdit = $("#decision_edit_" + id);
            decisionEdit.prop("src", "/img/edit.png");
            decisionEdit.attr("onclick", "editDecision(" + id + ")");
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}
