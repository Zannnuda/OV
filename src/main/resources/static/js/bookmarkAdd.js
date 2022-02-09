document.addEventListener('DOMContentLoaded',  function () {
    const url = window.location.href.split('/')
    BookmarkAdd.questionId = url[url.length - 1]
    BookmarkAdd.btnState(BookmarkAdd.getBookmark())
})

const BookmarkAdd = {
    questionId: 0,
    _url: '/api/user/bookmarks/',
    getBookmark: function () {
        return fetch(this._url + this.questionId, {
            method: 'GET',
            headers: {
                'Authorization': $.cookie("token")
            }
        })
            .then(response => response.ok)
            .catch(error => console.log(error.message))
    },
    add: function () {
        return fetch(this._url + this.questionId, {
            method: 'POST',
            headers: {
                'Authorization': $.cookie("token")
            }
        }).then(response => {
            this.btnState(response.ok)
        }).catch(error => console.log(error.message))
    },
    remove: function () {
        return fetch(this._url + this.questionId, {
            method: 'DELETE',
            headers: {
                'Authorization': $.cookie("token")
            }
        }).then(response => {
            this.btnState(!response.ok)
        }).catch(error => console.log(error.message))
    },
    btnState: async function (state) {
        const btn = document.getElementById('questionToBookmark')
        if ( await state) {
            $(btn)
                .text('Удалить из закладок')
                .attr('onClick', 'BookmarkAdd.remove()')
                .removeClass('btn-success')
                .addClass('btn-primary')
        } else {
            $(btn)
                .text('Добавить в закладки')
                .attr('onClick', 'BookmarkAdd.add()')
                .removeClass('btn-primary')
                .addClass('btn-success')
        }
        $(btn).blur()
    }
}

