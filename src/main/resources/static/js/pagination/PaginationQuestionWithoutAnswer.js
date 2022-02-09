class PaginationQuestionWithoutAnswer {

    constructor(page, size, type) {
        this.page = page;
        this.size = size;
        this.type = type;
        this.questionService = new QuestionService();

        if (this.type === 'noSort') {
            this.questionWithoutAnswers = this.questionService.getQuestionWithoutAnswers(this.page, this.size);
        } else if (this.type === 'tracked') {
            this.questionWithoutAnswers = this.questionService.getQuestionWithoutAnswersTrackedTag(this.page, this.size);
        } else if (this.type === 'new') {
            this.questionWithoutAnswers = this.questionService.getQuestionOrderedNew(this.page, this.size);
        } else if (this.type === 'votes') {
            this.questionWithoutAnswers = this.questionService.getQuestionWithoutAnswerSortedByVotes(this.page, this.size);
        } else if (this.type === 'noAnyAnswer') {
            this.questionWithoutAnswers = this.questionService.getQuestionWithoutAnswersNoAnyAnswer(this.page, this.size);
        } else {
            this.questionWithoutAnswers = this.questionService.getQuestionWithoutAnswers(this.page, this.size);
        }
    }

    writeQuestionWithoutAnswer() {

        $('.questionWithoutAnswers').children().remove();

        this.questionWithoutAnswers.then(function (response) {

            for (var i = 0; i < response.items.length; i++) {

                const date = new Date(response.items[i].persistDateTime)
                const stringDate = ('0' + date.getDate()).slice(-2) + "."
                    + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                    + date.getFullYear() + " " + ('0' + date.getHours()).slice(-2) + ":"
                    + ('0' + date.getMinutes()).slice(-2)

                let shuffledNames = response.items[i].listTagDto.map(i => i.name).sort(() => Math.random() - 0.5);
                let text = shuffledNames.map(i => `<a href="#" class="mb-1"> ${i} </a>`).join('');

                $('.questionWithoutAnswers').append(
                    "<div class=\"question-card d-flex\">" +
                    "<div class=\"container\">" +
                    "   <div class=\"row\">" +
                    "       <div class=\"col-sm-2\">" +
                    "           <div class=\"question-stats-container\">" +
                    "               <div class=\"stats\">" +
                    "                   <div class=\"vote\">" +
                    "                       <div class=\"vote-count\">" +
                    "                           <strong>" + response.items[i].countValuable + "</strong>" +
                    "                       </div>" +
                    "                       <div class=\"view-count-vote\">голосов</div>" +
                    "                       <div class=\"view-count-answer\">" +
                    "                           <div class=\"status-unanswered\"><strong>" + response.items[i].countAnswer + "</strong></div>" +
                    "                           <div class=\"view-count\">ответов</div>" +
                    "                       </div>" +
                    "                       <div class=\"views warm d-flex\">" +
                    "                           <div class=\"views-number\">" + response.items[i].viewCount + "</div>" +
                    "                           <div class=\"views-text\">показов</div>" +
                    "                       </div>" +
                    "                   </div>" +
                    "               </div>" +
                    "       </div>" +
                    "</div>" +

                    "<div class=\"col-sm-6\">" +
                    "   <div class=\"question-title\">" +
                    "       <a style='color:#0077cb; font-size: 125%' id=\"questionLink" + response.items[i].id + "\" href=\"/question/" + response.items[i].id + "\" onclick=\"openContent(id, 'question')\" >" +
                    response.items[i].title +
                    "       </a>" +
                    "   </div>" +
                    "   <div class=\"question-text\">" + response.items[i].description +
                    "   </div>" +
                    "   <div >" + text + "</div>" +
                    "</div>" +

                    "<div class=\"col-sm-4\">" +
                    "       <div class=\"user-info-change\">" +
                    "           <a href=\"#\" class=\"user-info-change-link\">изменён " +
                    "               <span class=\"user-info-change-time\"> " +
                    response.items[i].lastUpdateDateTime.substr(5, response.items[i].lastUpdateDateTime.indexOf("T") - 5)
                    + " " +
                    response.items[i].lastUpdateDateTime.substr(response.items[i].lastUpdateDateTime.indexOf("T") + 1, 5)
                    + "     </span>" +
                    "           </a>" +
                    "       </div>" +
                    "       <div class=\"user-info-gravatar d-flex\">" +
                    "           <a href=\"#\" class=\"user-info-gravatar-link\">" +
                    "           <div class=\"user-info-gravatar-wrapper\">" +
                    "               <img src=\"" + response.items[i].authorImage + "\" alt=\"\" width=\"32\" height=\"32\" class=\"user-info-img\">" +
                    "           </div>" +
                    "           </a>" +
                    "           <div class=\"user-info-details-wrapper\">" +
                    "           <a class=\"user-info-details-name\" href=\"api/user/" + response.items[i].authorId + "\">" + response.items[i].authorName + "</a>" +
                    "               <div class=\"user-info-stats\">" +
                    "                   <span class=\"user-reputation\">" + "!!!" + "</span>" +
                    "                   <span class=\"user-gold active-stats\">" + "###" + "</span>" +
                    "                   <span class=\"user-silver active-stats\">" + "###" + "</span>" +
                    "                   <span class=\"user-bronze active-stats\">" + "###" + "</span>" +
                    "               </div>" +
                    "           </div>" +
                    "       </div>" +
                    "</div>"
                );
            }
        })
        this.questWithoutAnswerPagesNavigation()
    }

    totalResultCountView() {

        this.questionWithoutAnswers.then(function (response) {
            var totalResultCount = response.totalResultCount;

            $("#totalResultCountView").empty()
            $("#totalResultCountView").append(
                "               <div class=\"totalResultCountView\">" +
                "                   <span class=\"totalResultCountView-count\">" + totalResultCount + "</span>" +
                "                   <span class=\"totalResultCountView-text\">вопросов без принятого ответа или без ответа, за который были отданы голоса</span>" +
                "               </div>"
            );

        })
    }

    questWithoutAnswerPagesNavigation() {
        var size = this.size;
        var type = this.type;

        this.questionWithoutAnswers.then(function (response) {
                var currentPageNumber = response.currentPageNumber;
                var nextPage = response.currentPageNumber + 1;
                var secondNextPage = response.currentPageNumber + 2;
                var totalPageCount = response.totalPageCount;
                var previousPage = response.currentPageNumber - 1;
                var startPageCount = 1;

                $('.questionWithoutAnswerPagination').children().remove();

                if (currentPageNumber != 1) {
                    $('.questionWithoutAnswerPagination').append(
                        "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + previousPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>Назад</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + startPageCount + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + startPageCount + "</a></li>"
                    );
                }

                if (currentPageNumber == totalPageCount) {
                    $('.questionWithoutAnswerPagination').append(
                        "<li class=\"page-item-element active\"><a class=\"page-element-link\" href=\"#\" >" + currentPageNumber + "</a></li>"
                    );
                }

                if (nextPage == totalPageCount) {
                    $('.questionWithoutAnswerPagination').append(
                        "<li class=\"page-item-element active\"><a class=\"page-element-link\" href=\"#\" >" + currentPageNumber + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + nextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + nextPage + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + nextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + "Далее" + "</a></li>"
                    );
                }


                if (secondNextPage == totalPageCount) {
                    $('.questionWithoutAnswerPagination').append(
                        "<li class=\"page-item-element active\"><a class=\"page-element-link\" href=\"#\" >" + currentPageNumber + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + nextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + nextPage + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + secondNextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + secondNextPage + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + nextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + "Далее" + "</a></li>"
                    );
                }


                if (secondNextPage < totalPageCount) {
                    $('.questionWithoutAnswerPagination').append(
                        "<li class=\"page-item-element active\"><a class=\"page-element-link\" href=\"#\" >" + currentPageNumber + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + nextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + nextPage + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + secondNextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + secondNextPage + "</a></li>"
                        + "<li class=\"page-item-element\"><span class='mr-2 ml-2'>" + "..." + "</span></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + totalPageCount + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + totalPageCount + "</a></li>"
                        + "<li class=\"page-item-element\"><a class=\"page-element-link\" href=\"#\" onclick='new PaginationQuestionWithoutAnswer(" + nextPage + "," + size + "," + "\"" + type + "\"" + ").writeQuestionWithoutAnswer()'>" + "Далее" + "</a></li>"
                    );
                }
            }
        )
    }
}