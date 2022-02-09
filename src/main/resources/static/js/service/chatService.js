
function getListSingleChat(page, size) {                                // Get всех chats
    let query = '/api/chat/single?page=' + page + '&size=' + size;
    return fetch(query, {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    }).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            let error = new Error();
            error.response = response.text();
            throw error;
        }
    }).catch(error => error.response.then(message => console.log(message)));
}

function getAllMessageSingleChat(id, page, size) {                              // get списка сообщений пользователя
    let query = '/api/chat/' + id + '/message?page=' + page + '&size=' + size;
    return fetch(query, {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            let error = new Error();
            error.response = response.text();
            throw error;
        }
    }).catch(error => error.response.then(message => console.log(message)));
}


