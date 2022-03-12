function closeRequest(id) {
    $.ajax({
        type: "POST",
        url: "/requests/" + id + "/close",
        cache: false,
        timeout: 600000,
        async: true,
        success: function () {
            $("#close_button_" + id).prop("disabled", true);
            $("#request_status_" + id).text("Закрыто");
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}