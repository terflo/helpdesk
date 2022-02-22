let usernameField = null
let emailField = null
let descriptionField = null
let editProfileButton = null
let avatarFile = null

let avatarImage = null
let usernameProfile = null
let emailProfile = null

function init() {
    usernameField = $("#username")
    emailField = $("#eMail")
    descriptionField = $("#description")
    editProfileButton = $("#submit_edit")
    avatarFile = $("#avatar-file");

    avatarImage = $("#user-avatar");
    usernameProfile = $("#username-profile")
    emailProfile = $("#email-profile")
}

function editProfile() {
    usernameField.removeAttr("readonly")
    emailField.removeAttr("readonly")
    descriptionField.removeAttr("readonly")

    editProfileButton.html("Отправить")
    editProfileButton.attr("onclick", "updateProfile(" + profileID +")")
}

function updateProfile(id) {

    $.ajax({
        type: "POST",
        url: "/user/" + id + "/update",
        cache: true,
        timeout: 600000,
        data: JSON.stringify({
            username: usernameField.val(),
            email: emailField.val(),
            description: descriptionField.val()
        }),
        dataType: "json",
        contentType: "application/json",
        processData: true,
        async: true,
        success: function (data) {

            if(data === "logout")
                window.location.href = "/logout"
            else {
                usernameField.attr('readonly', true);
                emailField.attr('readonly', true);
                descriptionField.attr('readonly', true);
                editProfileButton.html("Изменить информацию")
                editProfileButton.attr("onclick", "editProfile()")

                let descriptionProfileLabel = $("p[name='description-profile']")[0];
                if (descriptionField.val() !== "")
                    descriptionProfileLabel.innerText = descriptionField.val()
                else
                    descriptionProfileLabel.innerText = "Пока тут пусто :("
            }
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}

function uploadAvatar(id) {

    if(document.getElementById("avatar-file").files.length === 0)
        return;

    let formData = new FormData()
    formData.append('file', $("#avatar-file")[0].files[0]);

    $.ajax({
        type: "POST",
        url: "/user/" + id + "/updateAvatar",
        cache: true,
        timeout: 600000,
        data: formData,
        contentType: false,
        processData: false,
        async: true,
        success: function () {
            setAvatar(id)
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}

function setAvatar(id) {
    $.ajax({
        type: "POST",
        url: "/user/" + id + "/getAvatar",
        cache: true,
        timeout: 600000,
        dataType: "json",
        contentType: false,
        processData: false,
        async: true,
        success: function (data) {
            $("#user-avatar").attr('src', data)
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}