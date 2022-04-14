/*          ---===Инициализация===---        */
let stompClient = null;

$(document).ready(function() {

    let textField = $('#message')

    textField.keydown(function(e) {
        if(e.keyCode === 13 && stompClient != null && textField.val().trim() !== "") {
            e.preventDefault();
            sendMessage().then();
        }
    });
});

function showToast(text, theme) {
    new Toast({
        title: false,
        text: text,
        theme: theme,
        autohide: true,
        interval: 2500
    })
}



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
    },
        function (err) {
            err.forEach(element => showToast(element, 'warning'))
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
async function sendMessage() {

    let textField = $("#message")
    let fileField = $("#image-file")[0]

    let imageBase64 = null
    if(fileField.files.length !== 0) {
        let file = $('#image-file')[0].files[0];
        await getBase64(file).then(
            data => imageBase64 = data)
    }

    if(textField.val().trim().length > 255) {
        showToast("Длина сообщения должна быть до 255 символов", "warning")
        return
    }

    if(textField.val().trim() !== "" || fileField.files.length !== 0)
        stompClient.send("/app/chat", {},
            JSON.stringify({
                'sender': user,
                'message': textField.val().trim(),
                'userRequest': request.id,
                'imageBase64': imageBase64
            }));

    textField.val("")
    fileField.value = ""
}

/* Функция отображения полученого сообщения */
function showMessageOutput(messageOutput) {
    let message = getMessage(messageOutput['messageID']);
    let avatar = getAvatar(message.sender.id)
    let element;
    if(message.sender.username === user.username) {
        element = $('<li class="chat-right">')
            .append('<div class="chat-hour">' + $.format.date(new Date(message['date']), "HH:mm") + '<span class="fa fa-check-circle"></span></div>')
            .append('<div class="chat-text">\n' +
                '                                <div style="text-align: left">' + message['message'] + '</div>\n' +
                (message['imageBase64'] != null ? '<img src="' + message['imageBase64'] + '" alt="message image" class="message-image minimized">\n' : '') +
                '                            </div>')
            .append('<div class="chat-avatar">\n' +
                '                                <img src="' + avatar + '" alt="Avatar" class="chat-avatar">\n' +
                '                                <div class="chat-name">' + message.sender.username + '</div>\n' +
                '                            </div>')
    } else {
        element = $('<li class="chat-left">')
            .append('<div class="chat-avatar">\n' +
                '                                <img src="' + avatar + '" alt="Avatar" class="chat-avatar">\n' +
                '                                <div class="chat-name">' + message.sender.username + '</div>\n' +
                '                            </div>')
            .append('<div class="chat-text">\n' +
                '                                <div style="text-align: left">' + message['message'] + '</div>\n' +
                (message['imageBase64'] != null ? '<img src="' + message['imageBase64'] + '" alt="message image" class="message-image minimized">\n' : '') +
                '                            </div>')
            .append('<div class="chat-hour">' + $.format.date(new Date(message['date']), "HH:mm") + '<span class="fa fa-check-circle"></span></div>')


    }
    $("#chat-box").append(element);
    messageContainer.scrollTop = messageContainer.scrollHeight;

    $(function(){
        $('.minimized').click(function() {
            let i_path = $(this).attr('src');
            $('body').append('<div id="overlay"></div><div id="magnify"><img src="'+i_path+'" alt="enlarged image"><div id="close-popup"><i></i></div></div>');

            let magnify = $('#magnify')

            magnify.css({
                left: ($(document).width() - magnify.outerWidth())/2,
                top: ($(window).height() - magnify.outerHeight())/2
            });
            $('#overlay, #magnify').fadeIn('fast');
        });

        $('body').on('click', '#close-popup, #overlay', function(event) {
            event.preventDefault();
            $('#overlay, #magnify').fadeOut('fast', function() {
                $('#close-popup, #magnify, #overlay').remove();
            });
        });
    });

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
        type: "GET",
        url: "/user/" + id + "/avatar",
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
 function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}

$(function(){
    $('.minimized').click(function() {
        let i_path = $(this).attr('src');
        $('body').append('<div id="overlay"></div><div id="magnify"><img src="'+i_path+'" alt="enlarged image"><div id="close-popup"><i></i></div></div>');

        let magnify = $('#magnify')

        magnify.css({
            left: ($(document).width() - magnify.outerWidth())/2,
            top: ($(window).height() - magnify.outerHeight())/2
        });
        $('#overlay, #magnify').fadeIn('fast');
    });

    $('body').on('click', '#close-popup, #overlay', function(event) {
        event.preventDefault();
        $('#overlay, #magnify').fadeOut('fast', function() {
            $('#close-popup, #magnify, #overlay').remove();
        });
    });
});