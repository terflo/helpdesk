/*          ---===Инициализация===---        */
let stompClient = null;

$(document).ready(function() {
    $('#message').keydown(function(e) {
        if(e.keyCode === 13 && stompClient != null) {
            sendMessage();
        }
    });
});



/* Функция срабатывает при загрузке страницы и открывает соединение с сервером по протоколу STOMP поверх SockJS
 * а также подписывается на рассылку сообщений от сервера
 */
window.onload = function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.reconnect_delay = 1000;
    stompClient.connect({}, function() {
        stompClient.subscribe('/requestMessages/' + request.id + '/queue/messages', function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });
    });
}

/* Функция закрытия соединения */
function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

/* Функция отправки сообщения */
function sendMessage() {
    let textField = $("#message")
    if(textField.val().trim() !== "") {
        stompClient.send("/app/chat", {},
            JSON.stringify({
                'sender': user,
                'message': textField.val().trim(),
                'userRequest': request.id
            }));
        textField.val('')
    }
}

/* Функция отображения полученого сообщения */
function showMessageOutput(messageOutput) {
    let message = getMessage(messageOutput['messageID']);
    let avatar = getAvatar(message.sender.id)
    let element;
    if(message.sender.username === user.username) {
        element = $('<li class="chat-right">')
            .append('<div class="chat-hour">' + $.format.date(new Date(message['date']), "HH:mm") + '<span class="fa fa-check-circle"></span></div>')
            .append('<div class="chat-text">' + message['message'] + '</div>')
            .append('<div class="chat-avatar">\n' +
                '                                <img src="' + avatar + '" alt="Avatar">\n' +
                '                                <div class="chat-name">' + message.sender.username + '</div>\n' +
                '                            </div>')
    } else {
        element = $('<li class="chat-left">')
            .append('<div class="chat-avatar">\n' +
                '                                <img src="' + avatar + '" alt="Avatar">\n' +
                '                                <div class="chat-name">' + message.sender.username + '</div>\n' +
                '                            </div>')
            .append('<div class="chat-text">' + message['message'] + '</div>')
            .append('<div class="chat-hour">' + $.format.date(new Date(message['date']), "HH:mm") + '<span class="fa fa-check-circle"></span></div>')


    }
    $("#chat-box").append(element);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

/* Получение данных сообщения с сервера */
function getMessage(messageID) {
    let returnResult
    $.ajax({
        type: "GET",
        url: "/messages/" + messageID,
        cache: false,
        async: false,
        timeout: 600000,
        success: function (data) {
            returnResult = data
        }
    });
    return returnResult
}

function getAvatar(id) {
    let base64avatar
    $.ajax({
        type: "POST",
        url: "/user/" + id + "/getAvatar",
        cache: true,
        timeout: 600000,
        dataType: "json",
        contentType: false,
        processData: false,
        async: false,
        success: function (data) {
            base64avatar = data
        },
        error: function (data) {
            console.log(data.responseText)
        }
    });
    return base64avatar
}