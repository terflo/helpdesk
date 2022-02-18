function acceptRequest(id) {
    $.ajax({
        type: "POST",
        url: "/requests/accept/" + id,
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