let stompClient = null;

/* Функция срабатывает при загрузке страницы и открывает соединение с сервером по протоколу STOMP поверх SockJS */
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
            'sender':senderID,
            'message': textField.val(),
            'userRequest': requestID
        }));
    textField.val('')
}

/* Функция отображения полученого сообщения */
function showMessageOutput(messageOutput) {
    console.log(messageOutput);
}