const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});

function changePassword() {

    let passwordField = $("#password").val().trim()
    let passwordConfirmField = $("#passwordConfirm").val().trim()

    if(passwordField.length === 0 || passwordConfirmField.length === 0)
        showToast("Заполните все поля", "warning")
    else
        $.ajax({
            type: "POST",
            url: "/users/changePassword",
            cache: true,
            timeout: 600000,
            data: JSON.stringify({
                username: params.username,
                password: passwordField,
                passwordConfirm: passwordConfirmField,
                activateCode: params.activate_code
            }),
            dataType: "json",
            contentType: "application/json",
            processData: true,
            async: true,
            success: function () {
                showToast("Пароль успешно изменен", 'info')
                showToast("Вы будете перенаправлены на страницу авторизации через 5 секунд", 'info')
                window.setTimeout(function (){
                    window.location.href = '/login'
                }, 5000)
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

$(document).ready(function(){
    $('#password-checkbox').click(function(){
        let password = $('#password')
        let passwordConfirm = $('#passwordConfirm')

        if($(this).is(':checked')) {
            password.attr('type', 'text')
            passwordConfirm.attr('type', 'text')
        } else {
            password.attr('type', 'password')
            passwordConfirm.attr('type', 'password')
        }
    });
});