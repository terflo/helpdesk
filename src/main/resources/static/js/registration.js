function showToast(text) {
    new Toast({
        title: false,
        text: text,
        theme: 'light',
        autohide: true,
        interval: 5000
    })
}

function checkRegistrationData() {
    $.ajax({
        type: "POST",
        url: "/registration/checkUserData",
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

            if(data.usernameStatus !== null && data.usernameStatus !== 'OK') {
                if(data.usernameStatus === 'INCORRECT USERNAME')
                    showToast('Некорректное имя пользователя')
                else if(data.usernameStatus === 'ALREADY EXISTS')
                    showToast('Пользователь с таким именем уже существует')
            }

            if(data.emailStatus !== null && data.emailStatus !== 'OK') {
                if(data.emailStatus === 'INCORRECT EMAIL')
                    showToast('Некорректный email')
                else if(data.emailStatus === 'ALREADY EXISTS')
                    showToast('Пользователь с таким email уже существует')
            }

            if(data.passwordStatus !== null && data.passwordStatus !== 'OK') {
                if(data.passwordStatus === 'TOO SHORT')
                    showToast('Пароль слишком короткий')
                else if(data.passwordStatus === 'NOT MATCH')
                    showToast('Пароли не совпадают')
            }
        }
    });
}

