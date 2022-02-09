class AnswerService {

    getAnswerListByQuestionId(questionId) {
        let query = '/api/question/' + questionId + '/answer';
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
        }).catch(error => console.log(error.message));
    }


    getUpVoteAnswer(questionId, answerId, index) {
        fetch('/api/question/' + questionId + '/answer/' + answerId + '/upVote',
            {
                method: 'PATCH',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                })
            }).then(data => {
            let count = 0;
            let isHelpful = false;
            let aId;
            this.getAnswerListByQuestionId(questionId).then(response => {
                response.forEach(elem => {
                    if (elem.id === answerId) {
                        count = elem.countValuable;
                        isHelpful = elem.isHelpful;
                        aId = elem.id
                    }
                })
            }).then(function () {
                count = count == null ? 0 : count;
                document.getElementsByName('answer_downvote')[index].style.fill = "#000000"
                document.querySelectorAll('div.countAnswer')[index].innerHTML = '&nbsp;' + count;
                new AnswerService().getVoteById(aId).then(vote => {
                    if(vote === 0) {
                        document.getElementsByName('answer_upvote')[index].style.fill = "#000000"
                    } else if(vote === 1){
                        document.getElementsByName('answer_upvote')[index].style.fill = "#105ac7"
                    }
                })

                let html = '<path d="M6 14l8 8L30 6v8L14 30l-8-8v-8z"></path>\n';
                if (isHelpful === true) {
                    document.querySelectorAll('svg.isHelpful')[index].innerHTML = html;
                } else if (isHelpful === false) {
                     document.getElementsByClassName('isHelpful')[index].innerHTML = '';
                }
            })
        }).catch(error => console.log(error.message));
    }


    getDownVoteAnswer(questionId, answerId, index) {
        fetch('/api/question/' + questionId + '/answer/' + answerId + '/downVote',
            {
                method: 'PATCH',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                })
            }).then(data => {
            let count = 0;
            let isHelpful = false;
            let aId;
            this.getAnswerListByQuestionId(questionId).then(response => {
                response.forEach(elem => {
                    if (elem.id === answerId) {
                        count = elem.countValuable;
                        isHelpful = elem.isHelpful;
                        aId = elem.id;
                    }
                })
            }).then(function () {
                count = count == null ? 0 : count;
                document.getElementsByName('answer_upvote')[index].style.fill = "#000000"
                document.querySelectorAll('div.countAnswer')[index].innerHTML = '&nbsp;' + count;
                new AnswerService().getVoteById(aId).then(vote => {
                    if(vote === 0) {
                        document.getElementsByName('answer_downvote')[index].style.fill = "#000000"
                    } else if(vote === -1){
                        document.getElementsByName('answer_downvote')[index].style.fill = "#105ac7"
                    }
                })
                if (isHelpful === true) {
                    document.querySelectorAll('svg.isHelpful')[index].innerHTML = html;
                } else if (isHelpful === false) {
                    document.getElementsByClassName('isHelpful')[index].innerHTML = '';
                }
            })
        }).catch(error => console.log(error.message));
    }

    getCommentsByAnswerIdQuestionId(answerId, questionId) {
        let query = `/api/question/${questionId}/answer/${answerId}/comments`
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>{
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch(error => console.log(error.message))
    }

    setCommentByAnswerAndQuestionId(answerId, questionId) {
        let comment = {
            text: $(`#comment_summernote${answerId}`).summernote('code')
        }
        fetch(`/api/question/${questionId}/answer/${answerId}/comment`,
            {
                method: 'POST',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                }),
                body: JSON.stringify(comment)
            }).then(data => new QuestionPage().getAnswerCommentsById(answerId, questionId))
            .catch(error => console.log(error.message));
    }

    getVoteById(answerId) {
        let query = '/api/question/' + answerId + '/answer/vote';
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>{
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch(error => console.log(error.message))
    }
}