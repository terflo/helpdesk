function addRequest() {
    $.ajax({
        type: "POST",
        url: "/requests/add",
        data: JSON.stringify({
            name: $("#name").val(),
            description: $("#description").val(),
            priority: $("#priority").val()
        }),
        dataType: "json",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        success: function (data) {
            if(data === "OK")
                window.location.replace("/requests")
            else
                console.log(data)

        }
    });
}