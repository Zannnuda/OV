
class UserService {
    getUserById(id) {
        let query = '/api/user/' + id;
        return this.getResponse(query);
    }

    createUser(userRegistrationDto) {
        fetch('/api/user/registration', {
            method: 'POST',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userRegistrationDto)
        }).then(function(response) {
            if (!response.ok) {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
            return response.json();
        }).catch(error => error.response.then(message => console.log(message)));
    }

    getUserDtoPaginationByReputationOverMonth(page, size) {
        let query = '/api/user/order/reputation/month?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        })
            .then(response =>  {
                if (response.ok) {
                    return response.json()
                } else {
                    let error = new Error();
                    error.response = response.text();
                    throw error;
                }
            }).catch( error => error.response.then(message => console.log(message)));
    }

    getUserDtoPaginationByReputationOverWeek(page, size) {
        let query = '/api/user/order/reputation/week?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        })
            .then(response =>  {
                if (response.ok) {
                    return response.json()
                } else {
                    let error = new Error();
                    error.response = response.text();
                    throw error;
                }
            }).catch( error => error.response.then(message => console.log(message)));
    }

    getUserListByName(name, page, size) {
        let query = '/api/user/find?name=' + name + '&page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        })
            .then(response => {
                if (response.ok) {
                    return response.json()
                } else {
                    let error = new Error();
                    error.response = response.text();
                    throw error;
                }
            }).catch(error => error.response.then(message => console.log(message)));
    }

    getResponse(query) {
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        })
            .then(response =>  {
                if (response.ok) {
                    return response.json()
                } else {
                    let error = new Error();
                    error.response = response.text();
                    throw error;
                }
            })
            .catch( error => error.response.then(message => console.log(message)));
    }
}

// ----------------Functions------------------
document.getElementById("inputFilterUser").onkeyup = function (event) {
    const key = event.keyCode
    if (key >= 46 && key <= 90 || key >= 96 && key <= 105 || key === 8) {
        new PaginationUser(1, 20, 'search', document.getElementById('inputFilterUser').value).writeUsers()
    }
};
