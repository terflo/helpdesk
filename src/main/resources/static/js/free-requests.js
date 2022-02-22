function acceptRequest(id) {
    $.ajax({
        type: "POST",
        url: "/requests/accept/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function () {
            $("#request_row_" + id).remove()
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}