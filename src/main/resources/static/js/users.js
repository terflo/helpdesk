function deleteUser(id) {
    $.ajax({
        type: "DELETE",
        url: "/admin/users/delete/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function (data) {
            if(data === "OK")
                $("#user_row_" + id).remove()
            else
                console.log(data)
        }
    });
}

function switchUserLock(id) {
    $.ajax({
        type: "POST",
        url: "/admin/users/switchLock/" + id,
        cache: false,
        timeout: 600000,
        async: true,
        success: function (data) {
            if(data === "OK") {
                let switchInput = $('input[name="switch_lock_' + id + '"]')
                if(switchInput.attr("src") === "/img/off.png")
                    switchInput.prop("src", "/img/on.png")
                else
                    switchInput.prop("src", "/img/off.png")
            } else
                console.log(data)
        }
    });
}