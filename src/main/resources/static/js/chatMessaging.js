let stompClient;
let chatId;
let $lovelyMe = "хз";

function bindEvents() {
    $('#inputMessage').on('keyup', addMessageEnter.bind(this));
}


function connectToChat() {

    console.log("connecting to chat...")
    let socket = new SockJS("/message");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
    console.log("connected to: " + frame);
    });
}

function subscribeToChat(id) {
    chatId = id;
    $lovelyMe = $('#lovelyMe').attr('val');
    stompClient.subscribe("/chat/" + id + "/message", function (response) {
        let data = JSON.parse(response.body);
        if (data.userSender != $lovelyMe) {
        render(data.message);
        }
    });
}

function sendMsg(userSender, message, chatId) {
    stompClient.send("/app/message/" + chatId, {}, JSON.stringify({
        message: message,
        userSender: userSender,
        chat: chatId
    }));
}

function sendMessage(chatId) {
    let message = $('#inputMessage').val();
    $lovelyMe = $('#lovelyMe').attr('val');
    sendMsg($lovelyMe, message, chatId)
    showFirstPageMessage(chatId);                   // chatPage.js
}

function render(message) {
    scrollToBottom();
    // responses
    setTimeout(function () {

            $('#chatBox').append(`
                   <div class="media w-50 ml-0 mb-3">
                        <div class="media-body">
                            <div class="bg-light rounded py-2 px-3 mb-2">
                                <p class="text-small mb-0 text-dark">${message}</p>
                            </div>
                            <p class="small text-muted text-right">${getCurrentTime()}</p>
                        </div>
                   </div>
        `);
            scrollToBottom();

    }.bind(this), 1000);
}

function scrollToBottom() {
    $('#chatBox').scrollTop($('#chatBox')[0].scrollHeight);
}

function getCurrentTime() {
    return new Date().toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
}

function addMessageEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        sendMessage(chatId);
    }
}

bindEvents();

