class QuestionService {

    getVoteById(questionId) {
        let query = '/api/question/vote/'+questionId;
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

    getQuestionById(questionId) {
        let query = '/api/question/'+questionId;
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

    deleteQuestionById(id) {
        fetch('/api/question/' + id + '/delete', {
            method: 'DELETE',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(function(response) {
            if (!response.ok) {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
            return response.json();
        }).catch(error => error.response.then(message => console.log(message)));
    }

    setTagForQuestion(id, tagDto) {
        fetch('/api/question/' + id + '/tag/add', {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            },
            body: JSON.stringify(tagDto)
        }).then(function(response) {
            if (!response.ok) {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
            return response.json();
        }).catch(error => error.response.then(message => console.log(message)));
    }

    findPagination(page, size) {
        let query = '/api/question/?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    findPaginationNew(page, size) {
        let query = '/api/question/order/new?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    findPaginationPopularOverPeriod(page, size, period='') {
        let query = '/api/question/popular/' + period + '?page=' + page + '&size=' + size;

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

    findPaginationPopular(page, size) {
        let query = '/api/question/popular/?page=' + page + '&size=' + size;
        return fetch(query,{
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

    getQuestionPopularTrackedTag(page, size) {
        let query = '/api/question/popular/trackedTag?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getQuestionPopularIgnoredTag(page, size) {
        let query = '/api/question/popular/ignoredTag?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getQuestionsWithGivenTags(page, size, id) {
        let query = '/api/question/withTags?page=' + page + '&size=' + size + '&tagIds=' + id;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getQuestionsWithoutAnswers(page, size) {
        let query = '/api/question/withoutAnswer?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getResponseQuestion(query) {
        let result = new Array();
        fetch(query, {
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
            })
            .then(entity => result.push.apply(result, entity.items))
            .catch(error => error.response.then(message => console.log(message)));
        return result;
    }

    getQuestionWithoutAnswers(page, size) {
        let query = '/api/question/withoutAnswer?page=' + page + '&size=' + size;
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
        return fetch(query,{
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


    makeUpVoteQuestion(questionId) {
        fetch('/api/question/' + questionId + '/upVote',
            {
                method: 'POST',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                })
            }).then(data => {
            let count = 0;
            this.getQuestionById(questionId).then(response => {
                count = response.countValuable;
            }).then(data => {
                document.getElementById('downvote_btn').style.fill = "#000000"
                document.getElementById('count_question').innerHTML ="&nbsp;" + count;
                this.getVoteById(questionId)
                    .then(vote => {
                        if(vote === 0) {
                            document.getElementById('upvote_btn').style.fill = "#000000"
                        } else if(vote === 1){
                            document.getElementById('upvote_btn').style.fill = "#105ac7"
                        }
                    })
            })
        }).catch(error => console.log(error.message));
    }

    getCommentsByQuestionId(questionId) {
        let query = '/api/question/' + questionId + '/comments';
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

    setCommentByQuestionId(questionId) {
        let comment = {
            text: $('#comment_summernote').summernote('code')
        }
        fetch('/api/question/' + questionId + '/comment',
            {
                method: 'POST',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                }),
                body: JSON.stringify(comment)
            }).then(data => new QuestionPage().getCommentsById(questionId))
            .catch(error => console.log(error.message));
    }




    makeDownVoteQuestion(questionId) {
        fetch('/api/question/' + questionId + '/downVote',
            {
                method: 'POST',
                headers: new Headers({
                    'Content-Type': 'application/json',
                    'Authorization': $.cookie("token")
                })
            }).then(data => {
            let count = 0;
            this.getQuestionById(questionId).then(response => {
               count = response.countValuable;
            }).then(data => {
                document.getElementById('upvote_btn').style.fill = "#000000"
                document.getElementById('count_question').innerHTML ="&nbsp;" + count;
                this.getVoteById(questionId)
                    .then(vote => {
                        if(vote === 0) {
                            document.getElementById('downvote_btn').style.fill = "#000000"
                        } else if(vote === -1){
                            document.getElementById('downvote_btn').style.fill = "#105ac7"
                        }
                    })
            })
        }).catch(error => console.log(error.message));
    }

    getQuestionWithoutAnswersTrackedTag(page, size) {
        let query = '/api/question/withoutAnswer/trackedTag?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getQuestionOrderedNew(page, size) {
        let query = '/api/question/withoutAnswer/new?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getQuestionWithoutAnswerSortedByVotes(page, size) {
        let query = '/api/question/withoutAnswer/votes?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }

    getQuestionWithoutAnswersNoAnyAnswer(page, size) {
        let query = '/api/question/withoutAnswer/noAnyAnswer?page=' + page + '&size=' + size;
        return fetch(query, {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        }).then(response =>  {
            if (response.ok) {
                return response.json()
            } else {
                let error = new Error();
                error.response = response.text();
                throw error;
            }
        }).catch( error => error.response.then(message => console.log(message)));
    }
}