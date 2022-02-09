const formRegistration = document.getElementById('registration-page')
const registrationInfo = document.getElementById('registration-error')
let emailRegistration = document.getElementById('email')
let fullnameRegistration = document.getElementById('fullname')
let passwordRegistration = document.getElementById('password')
let passwordConfirmation = document.getElementById('password2')
let passwordButton = document.getElementById('show-password')
let confirmButton = document.getElementById('show-password2')
passwordRegistration.onchange = validatePassword;
passwordConfirmation.onkeyup = validatePassword;

formRegistration.addEventListener('submit', (event) => {
    event.preventDefault()
    if(validatePassword()){
        const data = {
            fullName: fullnameRegistration.value.toString(),
            email: emailRegistration.value.toString(),
            password: passwordRegistration.value.toString()
        }

        fetch('http://localhost:5557/api/auth/reg/registration', {
            method: 'post',
            body: JSON.stringify(data),
            headers: {
                'Content-Type': 'application/json;charset=utf-8',
            }
        }).then(promise => {
                if (promise.status >= 200 && promise.status < 300) {
                    emailRegistration.value = ''
                    fullnameRegistration.value = ''
                    passwordRegistration.value = ''
                    passwordConfirmation.value = ''
                    registrationInfo.innerHTML = ''
                    registrationInfo.innerHTML = '<div class="alert alert-success " role="alert">' +
                        'Регистрация прошла успешно! Cсылка для подтверждения регистрации отправлена на ваш email</div>'
                    return promise.json()

                } else {
                    emailRegistration.value = ''
                    fullnameRegistration.value = ''
                    passwordRegistration.value = ''
                    registrationInfo.innerHTML = ''
                    passwordConfirmation.value = ''
                    registrationInfo.innerHTML = '<div class="alert alert-danger" role="alert">' +
                        'Пользователь с таким email уже существует</div>'
                }
            }
    )}})

function validatePassword(){
    if(passwordRegistration.value.toString() !== passwordConfirmation.value.toString()) {
        passwordConfirmation.setCustomValidity("Пароли не совпадают");
        return false
    } else {
        passwordConfirmation.setCustomValidity('');
        return true
    }
}

function showPassword() {
    if (password.type === "password") {
        password.type = "text";
        passwordButton.innerHTML = ''
        passwordButton.innerHTML = '&#x2606;'
    } else {
        password.type = "password";
        passwordButton.innerHTML = ''
        passwordButton.innerHTML = '&#x2605;'
    }
}

function showPasswordConfirm() {
    if (passwordConfirmation.type === "password") {
        passwordConfirmation.type = "text";
        confirmButton.innerHTML = ''
        confirmButton.innerHTML = '&#x2606;'
    } else {
        passwordConfirmation.type = "password";
        confirmButton.innerHTML = ''
        confirmButton.innerHTML = '&#x2605;'
    }
}


