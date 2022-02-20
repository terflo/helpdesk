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

    editProfileButton.attr("value", "Обновить")
    editProfileButton.attr("onclick", "updateProfile()")
}

function updateProfile() {

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
        dataType: "json",
        contentType: false,
        processData: false,
        async: true,
        success: function (data) {
            if(data !== "OK") {
                console.log(data)
            } else {
                setAvatar(id)
            }
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
        }
    });
}