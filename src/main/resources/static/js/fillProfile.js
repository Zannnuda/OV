
$(document).ready(function () {
    fillProfile()
});

function showPrincipalProperties(){
    $('#mainBar').append(`
        <a href="/user/edit-profile" class="s-navigation--item" data-shortcut="A">Редактировать</a>
    `)
    $('#userInfoAbout').append(`
     <p>
        <a href="/users/edit/{id}">Нажмите здесь для редактирования</a>
     </p>
    `)
    $('#profileButton').remove()

}

function fillProfile() {
    let url = new URL(window.location.href)
    let id = Number(url.pathname.split("/")[3])
    if(id == 0){
        showPrincipalProperties()
    }
    fetch('/api/user/userProfile/' + id, {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    })
        .then(response => response.json())
        .then(data => {
            $('.reputation-number').append(
                data['reputation']
            )
            $('.avatar').append(
                "<img src=\"" + data['linkImage'] + "\" alt=\"\" width=\"164\" height=\"164\" class=\"avatar-user\">"
            )
            $('#name').append(
                data['fullName']
            )
            $('#city').append(
                "<div class=\"grid--cell fl1\">" + "Город: " + data['city'] + "</div>"
            )

            fetch('http://localhost:5557/api/user/reputation/history/' + data['id'] + '?page=1&size=1', {
                method: 'GET',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                })
            })
                .then(response => response.json())
                .then(function (data2) {
                    if (data2.items.length < 4) {
                        document.querySelector('#reputationMore').innerHTML = '';
                    }
                    for (var i = 0; i < data2.items.length; i++) {

                        $('#countRep' + (i + 1)).append(
                            "+" + data2.items[i].count + " <br/> репутации"
                        )
                        $('#reasonRep' + (i + 1)).append(
                            data2.items[i].type
                        )

                    }
                })
            fillAnswers(data['id'])
            fillQuestions(data['id'])
            fillTags(data['id'])
            fillBookmarks(data['id'])
        })
}

function fillAnswers(id) {
    fetch('/api/user/userAnswers/' + id + '?page=1&size=3', {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    })
        .then(response => response.json())
        .then(function (dat) {
            $('#countAnswers').append(
                dat.totalResultCount
            )
            if (dat.items.length < 4) {
                document.querySelector('#answersMore').innerHTML = '';
            }
            for (let i = 0; i < dat.items.length; i++){

                $('#voices' + (i + 1)).append(
                    dat.items[i].countValuable + "<br/>" + " голосов"
                )
                $('#answer' + (i + 1)).append(
                    dat.items[i].body
                )

            }
        })
}

function fillQuestions(id) {
    fetch('/api/user/userQuestions/'+ id +'?page=1&size=3', {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    })
        .then(response => response.json())
        .then(function (dat) {

            if (dat.items.length < 4) {
                document.querySelector('#questionMore').innerHTML = '';
            }
            $('#countQuestions').append(
                dat.totalResultCount
            )
            for (let i = 0; i < dat.items.length; i++){
                document.getElementById("questionLink" + (i + 1)).href="/question/" + dat.items[i].id

                $('#voicesQ' + (i + 1)).append(
                    dat.items[i].countValuable + "<br/>" + " голосов"
                )
                $('#question' + (i + 1)).append(
                    dat.items[i].title
                )

            }

        })
}

function fillTags(id) {
    fetch('/api/tag/tracked/' + id, {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    })
        .then(response => response.json())
        .then(function (data) {
            if (data.length < 6) {
                document.querySelector('#tagsMore').innerHTML = '';
            }
            for (var i=0; i < data.length; i++) {
                $('#tags').append(
                    "<button type=\"button\" class=\"btn btn-sm\">" + data[i].name + "</button>"
                )
            }
        })
}

function fillBookmarks(id) {
    fetch('/api/user/bookmarks/' + id, {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    })
        .then(response => response.json())
        .then(function (data) {
            document.querySelector('#bookmarksMore').innerHTML = '';
            for (let i = 0; i < data.length; i++) {
                $('#bmVotes' + (i + 1)).append(
                    data[i].questionVotes + " <br/> голосов"
                )
                $('#bmQuestion' + (i + 1)).append(
                    data[i].questionTitle
                )
            }
        })
}



