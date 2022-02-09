fetch('/api/user/currentUser/answers?page=1&size=3', {
    method: 'GET',
    headers: new Headers({
        'Content-Type': 'application/json',
        'Authorization': $.cookie("token")
    })
})
    .then(response => response.json())
    .then(function (dat) {
        for (let i = 0; i < 3; i++){
            console.log(dat.items[i].persistDate)
            // if (data.items[i] === undefined) {
            //     return;
            // }

            const date = new Date(dat.items[i].persistDate)
            const stringDate = ('0' + date.getDate()).slice(-2) + "."
                + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                + date.getFullYear() + " " + ('0' + date.getHours()).slice(-2) + ":"
                + ('0' + date.getMinutes()).slice(-2)

            $('#voices ' + (i + 1)).append(
                dat.items[i].countValuable + "<br/>" + " голосов"
            )
            $('#answer ' + (i + 1)).append(
                dat.items[i].body
            )
            $('#date ' + (i + 1)).append(
                "stringDate"
            )
        }
    })