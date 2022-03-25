function addRequest() {
    $.ajax({
        type: "POST",
        url: "/requests",
        data: JSON.stringify({
            name: $("#name").val(),
            description: $("#description").val(),
            priority: $("#priority").val()
        }),
        dataType: "json",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        success: function () {
                window.location.replace("/requests")
        },
        error: function (data) {
            data.responseJSON.forEach(element => showToast(element, 'warning'))
        }

    });
}

function showToast(text, theme) {
    new Toast({
        title: false,
        text: text,
        theme: theme,
        autohide: true,
        interval: 2500
    })
}