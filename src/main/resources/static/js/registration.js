function showToast(text, theme) {
    new Toast({
        title: false,
        text: text,
        theme: theme,
        autohide: true,
        interval: 2500
    })
}

function sendRegistrationData() {

    let response = grecaptcha.getResponse()

    $.ajax({
        type: "POST",
        url: "/registration?g-recaptcha-response=" + response,
        data: JSON.stringify({
            username: $("#username").val().trim(),
            email: $("#email").val().trim(),
            password: $("#password").val().trim(),
            passwordConfirm: $("#password-confirm").val().trim()
        }),
        dataType: "json",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        success: function () {
            $('#registration_accessed').modal('show')
        },
        error: function (data) {
            console.log(data)
            data.responseJSON.forEach(element => showToast(element, 'warning'))
        }
    })
}

function checkRegistrationData() {
    $.ajax({
        type: "POST",
        url: "/registration/checkUserData",
        data: JSON.stringify({
            username: $("#username").val().trim(),
            email: $("#email").val().trim(),
            password: $("#password").val().trim(),
            passwordConfirm: $("#password-confirm").val().trim()
        }),
        dataType: "json",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        success: function () {
            showToast('Данные корректны', 'info')
        },
        error: function (data) {
            data.responseJSON.forEach(element => showToast(element, 'warning'))
        }
    })
}

