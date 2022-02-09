class QuestionPage {
    constructor(questionId) {
        this.questionId = questionId;
        this.questionService = new QuestionService();
        this.answerService = new AnswerService();
        this.indexMap = new Map()
    }

    populateQuestionPage() {
        this.questionService.getQuestionById(this.questionId)
            .then(response => {
                const date = new Date(response.persistDateTime)
                const stringDate = ('0' + date.getDate()).slice(-2) + "."
                    + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                    + date.getFullYear()
                const dateLastUpdate = new Date(response.persistDateTime)
                const stringDateLastUpdate = ('0' + dateLastUpdate.getDate()).slice(-2) + "."
                    + ('0' + (dateLastUpdate.getMonth() + 1)).slice(-2) + "."
                    + dateLastUpdate.getFullYear()

                document.getElementById('question-title').innerHTML = "<p>" + response.title + "</p>"

                $('#question-underheader').children().remove()
                $('#question-underheader').append(
                    "            <div class=\"col\">\n" +
                    "                <p>\n" +
                    "                    <span style=\"color: #6a737c\">Вопрос задан:</span> " + stringDate + "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                    "                    <span style=\"color: #6a737c\">Последняя активность:</span> " + stringDateLastUpdate + "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                    "                    <span style=\"color: #6a737c\">Просмотрен:</span> " + response.viewCount + " раза\n" +
                    "                </p>\n" +
                    "            </div>\n"
                )
                $('#question-area').children().remove()
                $('#question-area').append(
                    "                    <div class=\"row\">\n" +
                    "                        <div vote-area-question class=\"col-1\">\n" +
                    '                            <a class="btn  btn-sm m-0 p-0" onclick="new QuestionService().makeUpVoteQuestion(' + this.questionId + ')">' +
                    "                               <svg width=\"36\" height=\"36\" >\n" +
                    "                                  <path id='upvote_btn' d=\"M2 26h32L18 10 2 26z\"></path>\n" +
                    "                               </svg>\n" +
                    '                             </a>' +
                    '                            <div style=\"font-size: 200%\" id="count_question"> &nbsp;' + response.countValuable + '</div>\n' +
                    '                              <a class="btn  btn-sm m-0 p-0" onclick="new QuestionService().makeDownVoteQuestion(' + this.questionId + ');">' +
                    "                                 <svg  width=\"36\" height=\"36\" >\n" +
                    "                                    <path id='downvote_btn' d=\"M2 10h32L18 26 2 10z\"></path>\n" +
                    "                                  </svg>\n" +
                    '                               </a>' +
                    "                        </div>\n" +
                    "                        <div question-and-comments-area class=\"col-11\">\n" +
                    "                            <div question-area class=\"col\">\n" +
                    "                                <div id=\"question-describtion\" describlion class=\"s-prose js-post-body\" itemprop=\"text\">\n" +
                    "<p>" + response.description + "</p>" +
                    "                                </div>\n" +
                    "                                <div id='question-tags' class=\"tags\">\n" +

                    "                                </div>\n" +
                    "                                <div underquestion class=\"mb0 \">\n" +
                    "                                    <div class=\"row justify-content-between px-3\">\n" +
                    "                                        <div>\n" +
                    "                                            <a href=\"#\">Поделиться</a>\n" +
                    "                                            <a href=\"#\">Править</a>\n" +
                    "                                            <a href=\"#\">Отслеживать</a>\n" +
                    "                                        </div>\n" +
                    "                                        <div>\n" +
                    "                                            <div class=\"card\" style=\"width: 250px\">\n" +
                    "                                                <div class=\"card-body p-2\" style=\"background: #e0ebf3\">\n" +
                    "                                                    <div>\n" +
                    "                                                        <span>Задан " + stringDate + "</span>\n" +
                    "                                                    </div>\n" +
                    "                                                    <div class=\"row\">\n" +
                    "                                                        <div class=\"col-3\">\n" +
                    "                                                            <img width=\"48\" height=\"48\" src=\"" + response.authorImage + "\" alt=\"...\">\n" +
                    "                                                        </div>\n" +
                    "                                                        <div class=\"col-8\">\n" +
                    "                                                            <div>" + response.authorName + "</div>\n" +
                    "                                                            <div><b>17</b>&nbsp;&nbsp;&nbsp;12</div>\n" +
                    "                                                        </div>\n" +
                    "                                                    </div>\n" +
                    "                                                </div>\n" +
                    "                                            </div>\n" +
                    "                                        </div>\n" +
                    "\n" +
                    "                                    </div>\n" +
                    "                                </div>\n" +
                    "                            </div>\n" +
                    "                            <div comments class=\"post-layout--right\">\n" +
                    "                                <div id=\"comments-1237608\" class=\"comments js-comments-container bt bc-black-075 mt12  dno\" data-post-id=\"1237608\" data-min-length=\"15\">\n" +
                    "                                    <ul id='comments-list' class=\"comments-list js-comments-list\" data-remaining-comments-count=\"0\" data-canpost=\"false\" data-cansee=\"true\" data-comments-unavailable=\"false\" data-addlink-disabled=\"true\">\n" +
                    "                                    </ul>\n" +
                    "                                </div>\n" +
                    "\n" +
                    "                                <div id=\"comments-link-1237608\" data-rep=\"50\" data-reg=\"true\">\n" +
                    "                                    <a id=\"getcommentarea\" href=\"#\" onclick='event.preventDefault(); getSummernoteTo(\"\")'>добавить комментарий</a>\n" +
                    "                                    <span id=\"getcommentareasp\" >&nbsp;|&nbsp;</span></n>" +
                    "                                    <h6 id=\"summer_head\" class=\"card-title\" style=\"display: none\">Ваш комментарий</h6></n>" +
                    "                                    <div class=\"summernote\" id=\"comment_summernote\" placeholder=\"Введите данные\"></div>" +
                    '                                    <a id="addcomment" class="btn btn-primary" style="display: none; color: white" onclick="callQuestionService(event, ' + this.questionId + ')">Добавить коммент</a>' +
                    "                                </div>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>"
                )
                this.getCommentsById(response.id);
                response.listTagDto.forEach(tag => {
                    $('#question-tags').append(
                        "                                    <div class=\"mb-1\">\n" +
                        tag.name +
                        "                                    </div>")
                })
            })
        this.answerService.getAnswerListByQuestionId(this.questionId)
            .then(response => {
                $('#answer-area').children().remove()
                $('#answer-area').append(
                    "                    <div info class=\"row justify-content-between m-2 py-5\">\n" +
                    "                        <div style=\"font-size: 150%\">\n" +
                    "                            " + response.length + " Ответ\n" +
                    "                        </div>\n" +
                    "                        <div>\n" +
                    "                            <div class=\"btn-group\">\n" +
                    "                                <button type=\"button\" class=\"btn btn-outline-secondary\">Текущие</button>\n" +
                    "                                <button type=\"button\" class=\"btn btn-outline-secondary\">По дате побликации</button>\n" +
                    "                                <button type=\"button\" class=\"btn btn-outline-secondary active\">Голоса</button>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>"
                )


                let index = 0;
                response.forEach(elem => {
                    if(this.indexMap.has(elem.id) === false) {
                        this.indexMap.set(elem.id, index)
                    }
                    const date = new Date(elem.persistDate)
                    const stringDate = ('0' + date.getDate()).slice(-2) + "."
                        + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                        + date.getFullYear();

                    let count = elem.countValuable == null ? 0 : elem.countValuable;
                    let isHelpful = elem.isHelpful;

                    $('#answer-area').append(
                        "<div answer1 class=\"row\">\n" +
                        "    <div vote-area-answer class=\"col-1\">\n" +
                        '        <a  class="btn  btn-sm m-0 p-0" onclick="new AnswerService().getUpVoteAnswer(' + this.questionId + ',' + elem.id + ',' + index + ')">' +
                        '             <svg   width=\"36\" height=\"36\" >\n' +
                        "                  <path name='answer_upvote' d=\"M2 26h32L18 10 2 26z\"></path>\n" +
                        "              </svg>\n" +
                        '        </a>' +
                        '             <div class="countAnswer" style=\"font-size: 200%\">&nbsp;' + count + '</div>\n' +
                        '        <a  class="btn  btn-sm m-0 p-0" onclick="new AnswerService().getDownVoteAnswer(' + this.questionId + ',' + elem.id + ',' + index + ')"> ' +
                        "              <svg  width=\"36\" height=\"36\" >\n" +
                        "                   <path name='answer_downvote' d=\"M2 10h32L18 26 2 10z\"></path>\n" +
                        "               </svg>\n" +
                        '        </a>     ' +
                        '              <svg class="isHelpful" width="36" height="36">\n' + (isHelpful == true ?
                        '          <path d="M6 14l8 8L30 6v8L14 30l-8-8v-8z"></path>\n' : " ") +
                        '       </svg>' +
                        "     </div>\n" +
                        "                      <div answer-and-comments-area class=\"col-11\">\n" +
                        "                            <div>" +
                        "                                  <p>" + elem.body + "</p>" +
                        "                            </div>\n" +
                        "                            <div usderanswer class=\"mb0 \">\n" +
                        "                                <div class=\"row justify-content-between px-3\">\n" +
                        "                                    <div>\n" +
                        "                                        <a href=\"#\">Поделиться</a>\n" +
                        "                                        <a href=\"#\">Править</a>\n" +
                        "                                        <a href=\"#\">Отслеживать</a>\n" +
                        "                                    </div>\n" +
                        "                                    <div>\n" +
                        "                                        <div style=\"width: 250px\">\n" +
                        "                                            <div>\n" +
                        "                                                <div>\n" +
                        "                                                    <span>Ответ дан: " + stringDate + "</span>\n" +
                        "                                                </div>\n" +
                        "                                                <div class=\"row\">\n" +
                        "                                                    <div class=\"col-3\">\n" +
                        "                                                        <img width=\"48\" height=\"48\" src=\"" + elem.image + "\" alt=\"...\">\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"col\">\n" +
                        "                                                        <div>" + elem.nickName + "</div>\n" +
                        "                                                        <div><b>25</b>&nbsp;&nbsp;&nbsp;1</div>\n" +
                        "                                                    </div>\n" +
                        "                                                </div>\n" +
                        "                                            </div>\n" +
                        "                                        </div>\n" +
                        "                                    </div>\n" +
                        "                                </div>\n" +
                        "                            </div>\n" +
                        "                            <div comments class=\"post-layout--right\">\n" +
                        "                                <div>\n" +
                        "                                    <ul  id='comments-answer" + elem.id + "'>\n" +
                        "                                        <hr/>\n" +
                        "                                        <li>\n" +
                        "                                            <span>Что такое var_with_long_name ?</span>\n" +
                        "                                            –\n" +
                        "                                            <a href=\"#\">Alpensin</a>\n" +
                        "                                            <span>1 час назад</span>\n" +
                        "                                        </li>\n" +
                        "                                        <hr/>\n" +
                        "                                        <li>\n" +
                        "                                            <span>Возможное имя переменной.</span>\n" +
                        "                                            –\n" +
                        "                                            <a href=\"#\">Алексей</a>\n" +
                        "                                            <span>44 минуты назад</span>\n" +
                        "                                        </li>\n" +
                        "                                        <hr/>\n" +
                        "                                    </ul>\n" +
                        "                                </div>\n" +
                        "                                <div id=\"comments-link-1237608\" data-rep=\"50\" data-reg=\"true\">\n" +
                        "                                    <a id=\"getcommentarea" + elem.id + "\" href=\"#\" onclick='event.preventDefault(); getSummernoteTo(" + elem.id + ")'>добавить комментарий</a>\n" +
                        "                                    <span id=\"getcommentareasp" + elem.id + "\">&nbsp;|&nbsp;</span>\n" +
                        "                                    <h6 id=\"summer_head" + elem.id + "\" class=\"card-title\" style=\"display: none\">Ваш комментарий</h6></n>" +
                        "                                    <div class=\"summernote\" id=\"comment_summernote" + elem.id + "\" placeholder=\"Введите данные\"></div>" +
                        '                                    <a id="addcomment' + elem.id + '" class="btn btn-primary" style="display: none; color: white" onclick="callAnswerService(event, ' + elem.id + ', ' + this.questionId + ')">Добавить коммент</a>' +
                        "                                </div>\n" +
                        "                            </div>\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                    <hr/>")
                    this.getAnswerCommentsById(elem.id, elem.questionId);
                    index++;
                })
            })
    }

    getColorForButtons(id){
        this.questionService.getVoteById(id)
            .then(vote => {
                if(vote === 1) {
                    document.getElementById('upvote_btn').style.fill = "#105ac7"
                } else if(vote === -1){
                    document.getElementById('downvote_btn').style.fill = "#105ac7"
                }
            })
      this.answerService.getAnswerListByQuestionId(id).then(response => {
            response.forEach(answer => {
                this.answerService.getVoteById(answer.id).then(aVote =>{
                    if(aVote === 1){
                        document.getElementsByName('answer_upvote')[this.indexMap.get(answer.id)].style.fill = "#105ac7"
                    } else if(aVote === -1){
                        document.getElementsByName('answer_downvote')[this.indexMap.get(answer.id)].style.fill = "#105ac7"
                    }
                })
            })
        })
    }

    getCommentsById(id) {
        new QuestionService().getCommentsByQuestionId(id)
            .then(comments => {
                $('#comments-list').children().remove();
                comments.forEach(async (comment) => {
                        const date = new Date(comment.persistDate)
                        const stringDate = ('0' + date.getDate()).slice(-2) + "."
                            + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                            + date.getFullYear() + ' at ' + ('0' + date.getHours()).slice(-2)
                            + ":" + ('0' + date.getMinutes()).slice(-2);
                        let commentText;
                        try {
                            commentText = JSON.parse(comment.text).text;
                        } catch (e) {
                            //ignore
                        }
                        let user = await new UserService().getUserById(comment.userId);
                        $('#comments-list').append("<hr/>\n"+
                            "                                        <li style='display: flex'>\n" +
                            "                                            <span>" + commentText + "</span>\n" +
                            "                                            <span>&nbsp;&nbsp;</span>\n" +
                            "                                            –&nbsp;\n" +
                            "                                            <a href=\"#\">" + user.fullName + "</a>\n" +
                            "                                            <span>&nbsp;&nbsp;</span>\n" +
                            "                                            <span>" + stringDate + "</span>\n" +
                            "                                        </li>\n"
                        )
                    }
                )
            })
    }
    getAnswerCommentsById(answerId, questionId) {
        new AnswerService().getCommentsByAnswerIdQuestionId(answerId, questionId)
            .then(comments => {
                $(`#comments-answer${answerId}`).children().remove();
                comments.forEach(async (comment) => {
                        const date = new Date(comment.persistDate)
                        const stringDate = ('0' + date.getDate()).slice(-2) + "."
                            + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                            + date.getFullYear() + ' at ' + ('0' + date.getHours()).slice(-2)
                            + ":" + ('0' + date.getMinutes()).slice(-2);
                        let commentText;
                        try {
                            commentText = JSON.parse(comment.text).text;
                        } catch (e) {
                            //ignore
                        }
                        let user = await new UserService().getUserById(comment.userId);
                        $(`#comments-answer${answerId}`).append("<hr/>\n" +
                            "                                        <li style='display: flex'>\n" +
                            "                                            <span>" + commentText + "</span>\n" +
                            "                                            <span>&nbsp;&nbsp;</span>\n" +
                            "                                            –&nbsp;\n" +
                            "                                            <a href=\"#\">" + user.fullName + "</a>\n" +
                            "                                            <span>&nbsp;&nbsp;</span>\n" +
                            "                                            <span>" + stringDate + "</span>\n" +
                            "                                        </li>\n"
                        )
                    }
                )
            })
    }
}

function callAnswerService(e, answerId, questionId) {
    e.preventDefault();
    new AnswerService().setCommentByAnswerAndQuestionId(answerId, questionId);
    closeSummernote(answerId);
}

function callQuestionService(e, questionId) {
    e.preventDefault();
    new QuestionService().setCommentByQuestionId(questionId);
    closeSummernote("");
}

function closeSummernote(id) {
    $(`#comment_summernote${id}`).summernote('reset')
    $(`#comment_summernote${id}`).next().hide();
    $( `#addcomment${id}` ).toggle();
    $( `#getcommentarea${id}`).toggle();
    $( `#getcommentareasp${id}`).toggle();
    $( `#summer_head${id}`).toggle();
}

function getSummernoteTo(id) {
    $(`#comment_summernote${id}`).summernote({
        tabsize: 2,
        height: 200
    });
    $(`#comment_summernote${id}`).next().show();
    $( `#addcomment${id}` ).toggle();
    $( `#getcommentarea${id}`).toggle();
    $( `#getcommentareasp${id}`).toggle();
    $( `#summer_head${id}`).toggle();

}
