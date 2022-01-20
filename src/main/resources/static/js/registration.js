function checkRegistrationData() {
    $.ajax({
        type: "POST",
        url: "/api/checkRegistrationData",
        data: JSON.stringify({
            username: $("#username").val(),
            email: $("#email").val(),
            password: $("#password").val(),
            passwordConfirm: $("#password-confirm").val()
        }),
        dataType: "json",
        contentType: "application/json",
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log(data)

            let img = document.getElementById("username-status-picture")
            if(data.usernameStatus === "OK") {
                img.classList = null
                img.src = "img/ok.png"
            } else if(data.usernameStatus !== null) {
                img.classList = null
                img.src = "img/cross-mark.png"
            } else {
                img.classList.add("hidden")
            }

            img = document.getElementById("email-status-picture")
            if(data.emailStatus === "OK") {
                img.classList = null
                img.src = "img/ok.png"
            } else if(data.emailStatus !== null) {
                img.classList = null
                img.src = "img/cross-mark.png"
            } else {
                img.classList.add("hidden")
            }

            img = document.getElementById("password-status-picture")
            if(data.passwordStatus === "OK") {
                img.classList = null
                img.src = "img/ok.png"
            } else if(data.passwordStatus !== null) {
                img.classList = null
                img.src = "img/cross-mark.png"
            } else {
                img.classList.add("hidden")
            }
        }
    });
}

