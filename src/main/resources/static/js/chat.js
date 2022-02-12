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
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/requestMessages/' + requestID + '/queue/messages', function(messageOutput) {
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
    stompClient.send("/app/chat", {},
        JSON.stringify({
            'sender': userID,
            'message': textField.val(),
            'userRequest': requestID
        }));
    textField.val('')
}

/* Функция отображения полученого сообщения */
function showMessageOutput(messageOutput) {
    let message = getMessage(messageOutput['messageID']);
    console.log(message);

    let element;
    if(message['sender'] !== userID) {
        element = $('<div class="container">')
            .append('<img src="../img/support.png" alt="Avatar" style="width:100%;">')
            .append('<p>' + message['message'] + '</p>')
            .append('<span class="time-right">' + $.format.date(new Date(message['date']), "dd.MM.yyyy hh:mm") + '</span>');
    } else {
        element = $('<div class="container darker">')
            .append('<img src="../img/support.png" class="right" alt="Avatar" style="width:100%;">')
            .append('<p>' + message['message'] + '</p>')
            .append('<span class="time-left">' + $.format.date(new Date(message['date']), "dd.MM.yyyy hh:mm") + '</span>');
    }
    $("#message-container").append(element);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

/* Получение данных сообщения с сервера */
function getMessage(messageID) {
    let returnResult;
    $.ajax({
        type: "GET",
        url: "/messages/" + messageID,
        cache: false,
        async: false,
        timeout: 600000,
        success: function (data) {
            returnResult = data;
        }
    });
    return returnResult;
}