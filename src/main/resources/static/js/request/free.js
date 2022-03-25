let stompClient = null;

window.onload = function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.reconnect_delay = 1000;
    stompClient.connect({}, function() {
        stompClient.subscribe('/requests/free', function(messageOutput) {

            let data = JSON.parse(messageOutput.body)

            if(data["freeRequestStatus"] === "NEW") {
                showNewFreeRequest(data)
            } else if(data["freeRequestStatus"] === "ACCEPTED") {
                deleteFreeRequest(data)
            }

        });
    });
}

function showNewFreeRequest(messageOutput) {

    switch (messageOutput["userRequestDTO"]["priority"]) {
        case "LOW":
            messageOutput["userRequestDTO"]["priority"] = "Низкий"
            break
        case "NORMAL":
            messageOutput["userRequestDTO"]["priority"] = "Нормальный"
            break
        case "MAX":
            messageOutput["userRequestDTO"]["priority"] = "Максимальный"
            break
    }

    $("#tbody-free").append(
            "<tr id=\"request_row_" + messageOutput["userRequestID"] + "\">\n" +
            "                    <td>" + messageOutput["userRequestID"] + "</td>\n" +
            "                    <td style=\"width: 30vw\">" + messageOutput["userRequestDTO"]["name"] + "</td>\n" +
            "                    <td style=\"width: 30vw\">\n" +
            "                        <label style=\"width: 100%\">\n" +
            "                            <textarea class=\"form-control\" rows=\"2\" style=\"resize: none; overflow-y: scroll;\" readonly>" + messageOutput["userRequestDTO"]["description"] + "</textarea>\n" +
            "                        </label>\n" +
            "                    </td>\n" +
            "                    <td>\n" +
            "                        <label>" + messageOutput["userRequestDTO"]["priority"] +"</label>\n" +
            "                    </td>\n" +
            "                    <td>\n" +
            "                        <a href=\"/user/" + messageOutput["userRequestDTO"]["user"]["username"] + "\">" + messageOutput["userRequestDTO"]["user"]["username"] + "</a>\n" +
            "                    </td>\n" +
            "                    <td>\n" +
            "                        <a href=\"/requests/ + " + messageOutput["userRequestID"] + "\"><img src=\"/static/img/loupe.png\" style=\"height: 2vw\" alt=\"see details\"></a>\n" +
            "                    </td>\n" +
            "                    <td><input onclick=\"acceptRequest(" + messageOutput["userRequestID"] + ")\" style=\"width: 2vw\" class=\"panel-button\" type=\"image\" src=\"/static/img/ok.png\" alt=\"accept request\"></td>\n" +
            "                </tr>"
    )
}

function deleteFreeRequest(data) {
    $("#request_row_" + data["userRequestID"]).remove()
}

function acceptRequest(id) {
    $.ajax({
        type: "POST",
        url: "/requests/" + id + "/accept",
        cache: false,
        timeout: 600000,
        async: true,
        success: function () {
            $("#request_row_" + id).remove()
        },
        error: function (data) {
            alert(data.responseText)
        }
    });
}