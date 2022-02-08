let stompClient = null;

window.onload = function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/requestMessages/' + requestID, function(messageOutput) {
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
            'date': new Date().getTime(),
            'userRequest': requestID,
            'status': 'RECEIVED'
        }));
    textField.val('')
}

/* Функция отображения полученого сообщения */
function showMessageOutput(messageOutput) {
    console.log(messageOutput);
}