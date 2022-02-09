let currentPageNumberMessage;
let totalPageMessage;
let itemsOnPageMessage;
let chatIdMessage;
let showNextPageMessage = false;

$(document).ready(function () {
    showListChat(1,10);
    connectToChat();  //chatMessaging.js
})

function showListChat(page, size){                                        // показываем все чаты пользователя
    getListSingleChat(page, size).then(function (json) {
        $('#listChats').children().remove();
        if(json.totalResultCount != 0) {
            let nowDate = new Date();
            for (let i = 0; i < json.items.length; i++) {               // parse date
                let date = new Date(json.items[i].lastRedactionDate);
                if (nowDate.getFullYear() == date.getFullYear() && nowDate.getMonth() == date.getMonth() && nowDate.getDate() == date.getDate()){
                    date = ('0' + date.getHours()).slice(-2) + ':'  + ('0' + date.getMinutes()).slice(-2);
                } else if(nowDate.getFullYear() == date.getFullYear() && nowDate.getMonth() == date.getMonth() && (Math.abs(nowDate.getDate() - date.getDate()) == 1)) {
                    date = "Вчера";
                } else {
                    date = ('0' + date.getDate()).slice(-2) + '.' + ('0' + (date.getMonth()+1)).slice(-2) + '.' + date.getFullYear();
                }
                $('#listChats').append(`
                    <a href="#" class="list-group-item list-group-item-action list-group-item-light singleChat rounded-0 sh" 
                    id="sh${json.items[i].id}" onclick="showFirstPageMessage(${json.items[i].id})">
                    <div class="media">
                    <img src="${json.items[i].imageLink}" alt="${json.items[i].nickname}" width="50" class="rounded-circle">
                        <div class="media-body ml-4">
                            <div class="d-flex align-items-center justify-content-between mb-1">
                                 <h6 class="mb-0">${json.items[i].nickname}</h6><small class="small font-weight">${date}</small>
                            </div>
                           <p class="font-italic text-muted mb-0 text-small">${cutMessage(json.items[i].message)}</p>
                        </div>
                    </div>
                    </a>
                `)
            }
        } else{
            $('#listChats').append(`
                <div class="mb-3">
                    <h5 class= "text-center">У вас нет чатов</h5>
                </div>
            `)
            return;
        }
        $('#sh' + json.items[0].id).click().addClass('active');        // выбираем 1 чат из списка
        if(json.totalPageCount != 1) {                                      // если страницы больше 1
            $('#questionsPagesNavigation').children().remove();             // пагинация
            var currentPageNumber = json.currentPageNumber;
            var nextPage = json.currentPageNumber + 1;
            var secondNextPage = json.currentPageNumber + 2;
            var totalPageCount = json.totalPageCount;
            var previousPage = json.currentPageNumber - 1;
            var startPageCount = 1;
            if (currentPageNumber != 1) {
                $('#questionsPagesNavigation').append(
                    '<li class=page-item><a class=page-link href=# onclick=showListChat(' + previousPage + ',' + size + ')>Назад</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + startPageCount + ',' + size + ')>' + startPageCount + '</a></li>'
                );
            }

            if (currentPageNumber == totalPageCount) {
                $('#questionsPagesNavigation').append(
                    '<li class=page-item active><a class=page-link href=# >' + currentPageNumber + '</a></li>'
                );
            }

            if (nextPage == totalPageCount) {
                $('#questionsPagesNavigation').append(
                    '<li class=page-item active><a class=page-link href=# >' + currentPageNumber + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + nextPage + ',' + size + ')>' + nextPage + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + nextPage + ',' + size + ')>Далее</a></li>'
                );
            }

            if (secondNextPage == totalPageCount) {
                $('#questionsPagesNavigation').append(
                    '<li class=page-item active><a class=page-link href=# >' + currentPageNumber + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + nextPage + ',' + size + ')>' + nextPage + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + secondNextPage + ',' + size + ')>' + secondNextPage + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + nextPage + ',' + size + ')>Далее</a></li>'
                );
            }

            if (secondNextPage < totalPageCount) {
                $('#questionsPagesNavigation').append(
                    '<li class=page-item active><a class=page-link href=# >' + currentPageNumber + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + nextPage + ',' + size + ')>' + nextPage + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + secondNextPage + ',' + size + ')>' + secondNextPage + '</a></li>'
                    + '<li class=page-item><span class=mr-2 ml-2>' + "..." + '</span></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + totalPageCount + ',' + size + ')>' + totalPageCount + '</a></li>'
                    + '<li class=page-item><a class=page-link href=# onclick=showListChat(' + nextPage + ',' + size + ')>Далее</a></li>'
                );
            }
        }
    });
}

function showFirstPageMessage(id){                                                            // показываем все сообщения
    getAllMessageSingleChat(id, 1, 20).then(function (response){
        let principal = response.meta[0];
        let nowDate = new Date();
        currentPageNumberMessage = response.currentPageNumber;
        totalPageMessage = response.totalPageCount;
        chatIdMessage = response.chatId;
        itemsOnPageMessage = response.itemsOnPage;
        if(totalPageMessage>1){
            showNextPageMessage = true
        }
        $('#chatBox').children().remove();
        $('#lovelyMe').attr('val', principal);
        console.log(response);
        for(let i = (response.items.length - 1); i >= 0; i--){
            let date = new Date(response.items[i].lastRedactionDate);
            if (nowDate.getFullYear() == date.getFullYear() && nowDate.getMonth() == date.getMonth() && nowDate.getDate() == date.getDate()){
                date = ('0' + date.getHours()).slice(-2) + ':'  + ('0' + date.getMinutes()).slice(-2);
            } else {
                date = ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2) + ' ' + ('0' + date.getDate()).slice(-2) + '.' + ('0' + (date.getMonth()+1)).slice(-2) + '.' + date.getFullYear();
            }
            if(principal == response.items[i].userSenderId){
                $('#chatBox').append(`
                   <div class="media w-50 ml-auto mb-3" id="mes${response.items[i].id}">
                        <div class="media-body">
                           <div class="bg-primary rounded py-2 px-3 mb-2">
                              <p class="text-small mb-0 text-white">${response.items[i].message}</p>
                           </div>
                            <p class="small text-muted text-right">${date}</p>
                        </div>
                    </div>
                `)
            } else {
                $('#chatBox').append(`
                    <div class="media w-50 mb-3" id="mes${response.items[i].id}">
                    <img src="${response.items[i].imageLink}" alt="user" width="50" class="rounded-circle">
                          <div class="media-body ml-3">
                              <div class="bg-light rounded py-2 px-3 mb-2">
                                 <p class="text-small mb-0 text-muted">${response.items[i].message}</p>
                              </div>
                              <p class="small text-muted">${date}</p>
                          </div>
                    </div>
                `)
            }
        }
        scrollToBottom(); //chatMessaging.js
        subscribeToChat(id); //chatMessaging.js
        $('#chatBox-footer').empty();
        $('#chatBox-footer').append(`
                <form id="form ${id}" class="bg-light">
                    <div class="input-group">
                        <input id="inputMessage" type="text" placeholder="Type a message" aria-describedby="sendBtn" class="form-control rounded-0 border-0 py-4 bg-light"/>
                        <div class="input-group-append">
                            <button id="sendBtn" onclick="sendMessage(${id});return false" type="submit" class="btn btn-link"> <i class="bi bi-chat-dots"></i></button>
                        </div>
                    </div>
                </form>
            `)
    })
    $('.sh').removeClass('active');
    $('#sh' + id).addClass('active');
}

document.getElementById("chatBox").addEventListener('scroll', function () { // message pagination
    if(($('#chatBox').scrollTop() < 300) && (showNextPageMessage == true)){                // запрос get при 200 px от верхней границы
        showNextPageMessage = false;
        currentPageNumberMessage += 1;
        getAllMessageSingleChat(chatId, currentPageNumberMessage, itemsOnPageMessage).then(function (response){
            let principal = response.meta[0];
            let nowDate = new Date();
            currentPageNumberMessage = response.currentPageNumber;
            totalPageMessage = response.totalPageCount;
            console.log(response)
            for(let i =0; i < response.items.length; i++){
                if(document.getElementById("mes" + response.items[i].id)){
                    continue;
                }
                let date = new Date(response.items[i].lastRedactionDate);
                if (nowDate.getFullYear() == date.getFullYear() && nowDate.getMonth() == date.getMonth() && nowDate.getDate() == date.getDate()){
                    date = ('0' + date.getHours()).slice(-2) + ':'  + ('0' + date.getMinutes()).slice(-2);
                } else {
                    date = ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2) + ' ' + ('0' + date.getDate()).slice(-2) + '.' + ('0' + (date.getMonth()+1)).slice(-2) + '.' + date.getFullYear();
                }
                if(principal == response.items[i].userSenderId){
                    $('#chatBox').prepend(`
                        <div class="media w-50 ml-auto mb-3" id="mes${response.items[i].id}">
                            <div class="media-body">
                               <div class="bg-primary rounded py-2 px-3 mb-2">
                                  <p class="text-small mb-0 text-white">${response.items[i].message}</p>
                               </div>
                                <p class="small text-muted text-right">${date}</p>
                            </div>
                        </div>
                    `)
                } else {
                    $('#chatBox').prepend(`
                        <div class="media w-50 mb-3" id="mes'${response.items[i].id}">
                        <img src="${response.items[i].imageLink}" alt="user" width="50" class="rounded-circle">
                            <div class="media-body ml-3">
                                  <div class="bg-light rounded py-2 px-3 mb-2">
                                      <p class="text-small mb-0 text-muted">${response.items[i].message}</p>
                                  </div>
                                  <p class="small text-muted">${date}</p>
                             </div>
                        </div>
                    `)
                }
            }
            if(currentPageNumberMessage < totalPageMessage ){
                showNextPageMessage = true;
            }
        })
    }
})

function cutMessage(message){                       // если сообщение в chat box  больше определенного кол ва символов
    if(message.length > 20 ) {                      // обрезаем
        return message.substring(0,20) + '...';
    } else {
        return message;
    }
}

function openNav() {
    document.getElementById("chatSidenav").style.width = "250px";
}

function closeNav() {
    document.getElementById("chatSidenav").style.width = "0";
}
