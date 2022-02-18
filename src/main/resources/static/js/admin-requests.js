function closeRequest(id) {
    $.ajax({
        type: "POST",
        url: "../requests/close/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function (data) {
            if(data === "OK") {
                $("#close_button_" + id).prop("disabled", true);
                $("#request_status_" + id).text("Закрыто");
            } else
                console.log(data)
        }
    });
}

function deleteRequest(id) {
    $.ajax({
        type: "DELETE",
        url: "/admin/requests/delete/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function (data) {
            if(data === "OK") {
                $("#request_row_" + id).remove()
            } else
                console.log(data)
        }
    });
}