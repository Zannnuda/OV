document.addEventListener('DOMContentLoaded', function () {

    const form = $('.form-edit-profile')

    EditProfileService.getInfo().then(info => {
        EditProfileView.renderInfo(info)
    })

    $(form).submit(function (event) {
        event.preventDefault()
        EditProfileService.updateInfo(this)
    })
})

const EditProfileService = {
    _url: '/api/user/public-info',
    getInfo: function () {
        return fetch(this._url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': $.cookie('token')
            }
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    },
    updateInfo: function (form) {
        const formData = $(form).serializeArray()
        const data = {};
        $.map(formData, function(n, i){
            data[n['name']] = n['value'];
        });

        data.id = + $(form).find('.user-id').text()

        console.log(data)

        fetch(this._url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': $.cookie('token')
            },
            body: JSON.stringify(data)
        }).then(response =>  {
            if (response.ok) {
                alert('Изменения успешно сохранены')
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }
}

const EditProfileView = {
    renderInfo: function (info) {
        const form = $('.form-edit-profile')
        Object.keys(info).forEach(inputName => {
            $(form).find(`[name=${inputName}]`).val(info[inputName])
        })
        $(form).find('.user-id').text(info.id)
    }
}