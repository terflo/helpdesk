const emailRegex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

function checkEmail() {
    if (!emailRegex.test(String(document.getElementById("email").value))) {
        document.getElementById("email-error-picture").classList = null;
        document.getElementById("sign-up-button").disabled = true;
    } else {
        document.getElementById("email-error-picture").classList.add("hidden")
        document.getElementById("sign-up-button").disabled = false;
    }
}

function passwordConfirm() {
    var password1 = document.getElementById("password").value
    var password2 = document.getElementById("password-confirm").value;
    if(password1 === password2) {
        document.getElementById("password-error-picture").classList.add("hidden");
        document.getElementById("sign-up-button").disabled = false;
    } else {
        document.getElementById("password-error-picture").classList = null;
        document.getElementById("sign-up-button").disabled = true;
    }
}

//TODO: Переделать под метод POST
function checkUsername() {
    $.ajax({
        type: "GET",
        url: "/api/usernameIsExist/" + document.getElementById("username").value,
        cache: false,
        timeout: 600000,
        success: function (data) {
            if(!data) {
                document.getElementById("username-error-picture").classList.add("hidden");
                document.getElementById("sign-up-button").disabled = false;
            } else {
                document.getElementById("username-error-picture").classList = null;
                document.getElementById("sign-up-button").disabled = true;
            }
        }
    });
}

