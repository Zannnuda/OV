const errorQuestion = document.getElementById('question-error')

async function addAnswerToQuestion() {

    let questionId = window.location.pathname.replace(/\D/g, '')

    let answerText = $('#textsummernote').summernote('code')

    let createAnswerDto = {
        htmlBody: answerText
    };

    const url = 'http://localhost:5557/api/question/' + questionId + '/answer';
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'content-type': 'application/json;charset=utf-8', 'Authorization': $.cookie("token")
            },
            body: JSON.stringify(createAnswerDto)
        });

        if (response.status >= 400 && response.status < 500) {
            errorQuestion.innerHTML = ''
            errorQuestion.innerHTML = '<div class="alert alert-danger m-1" role="alert">' +
                'Нельзя отвечать больше одного раза</div>'
        }

        const json = await response.json();

        //отрисовка страницы
        new QuestionPage(questionId).populateQuestionPage();

        $('#textsummernote').summernote('reset')

    } catch (error) {
        console.error('Ошибка:', error);
    }
}

document.getElementById('buttonAddAnswer').addEventListener('click', addAnswerToQuestion);
