function deleteUser(id) {
    $.ajax({
        type: "DELETE",
        url: "/admin/users/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function () {
            $("#user_row_" + id).remove()
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}

function switchUserLock(id) {
    $.ajax({
        type: "POST",
        url: "/admin/users/" + id + "/switchLock",
        cache: false,
        timeout: 600000,
        async: true,
        success: function () {
            let switchInput = $('input[name="switch_lock_' + id + '"]')
            if(switchInput.attr("src") === "/img/off.png")
                switchInput.prop("src", "/img/on.png")
            else
                switchInput.prop("src", "/img/off.png")
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}