function restorePassword() {

    let emailField = $("#email").val().trim()

    if(emailField.length === 0)
        showToast("Поле email не может быть пустым", "warning")
    else
        $.ajax({
            type: "POST",
            url: "/users/restorePassword",
            cache: true,
            timeout: 600000,
            data: emailField,
            dataType: "json",
            contentType: "application/json",
            processData: true,
            async: true,
            success: function () {
                let modal = new bootstrap.Modal(document.getElementById("infoModal"), {
                    keyboard: false,
                    backdrop: "static"
                });
                modal.show()
            },
            error: function (data) {
                showToast(data.responseJSON, 'warning')
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