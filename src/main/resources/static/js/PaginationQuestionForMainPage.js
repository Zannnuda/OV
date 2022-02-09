class PaginationQuestionForMainPage {

    constructor(page, size, type, id) {
        this.page = page;
        this.size = size;
        this.type = type;
        this.id = id;

        this.questionService = new QuestionService();

        if (this.type == 'new') {
            this.questions = this.questionService.findPaginationNew(this.page, this.size);
        } else if (this.type == 'popular') {
            this.questions = this.questionService.findPaginationPopularOverPeriod(this.page, this.size);
        } else if (this.type == 'popularTracked') {
            this.questions = this.questionService.getQuestionPopularTrackedTag(this.page, this.size);
        } else if (this.type == 'popularIgnored') {
            this.questions = this.questionService.getQuestionPopularIgnoredTag(this.page, this.size);
        } else if (this.type == 'popularWeek') {
            this.questions = this.questionService.findPaginationPopularOverPeriod(this.page, this.size, "week");
        } else if (this.type == 'popularMonth') {
            this.questions = this.questionService.findPaginationPopularOverPeriod(this.page, this.size, "month");
        } else if (this.type == 'withTags') {
            this.questions = this.questionService.getQuestionsWithGivenTags(this.page, this.size, this.id);
        } else {
            this.questions = this.questionService.findPagination(this.page, this.size);
        }

        // this.questions = this.questionService.findPaginationNew(this.page, this.size);

    }

    setQuestions() {
        $('#questionsAll').children().remove()
        $('#buttonToQuestionArea').children().remove()
        let url = '/question/123'
        console.log(/^\/question\//.test(url))

        this.questions.then(function (response) {
            for (var i = 0; i < response.items.length; i++) {
                const date = new Date(response.items[i].persistDateTime)
                const stringDate = ('0' + date.getDate()).slice(-2) + "."
                                 + ('0' + (date.getMonth() + 1)).slice(-2) + "."
                                 + date.getFullYear()
                let tagsView = response.items[i].listTagDto.map(i => i.name);
                let allTagsView  = tagsView.map(i => `<a href="#" class="mb-1"> ${i} </a>`).join('');

                $('#questionsAll').append(
                    "        <div class=\"list-group-item list-group-item-action h-100\">\n" +
                    "            <div class=\"row align-items-center h-100\">\n" +
                    "                <div style='width: 180px;' class='pl-3'>\n" +
                    "                    <a style='color: #6a737c; font-size:115%;' class=\"row\" id=\"questionLink"+ response.items[i].id +"\" href=\"/question/"+ response.items[i].id +"\" onclick=\"openContent(id, 'question')\">\n" +
                    "                        <div class=\"col-sm-4 p-0\">\n" +
                    "                            <div style=\"text-align: center;\">\n" +
                    "                                <small style=\"display: inline-block;\">" + response.items[i].countValuable +"<br /> голосов</small>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                        <div class=\"col-sm-4 p-0\">\n" +
                    "                            <div style=\"text-align: center;\">\n" +
                    "                                <small style=\"display: inline-block;\">" + response.items[i].countAnswer +"<br /> ответов</small>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                        <div class=\"col-sm-4 p-0\">\n" +
                    "                            <div style=\"text-align: center;\">\n" +
                    "                                <small style=\"display: inline-block;\">" + response.items[i].viewCount +"<br /> показов</small>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </a>\n" +
                    "                </div>\n" +
                    "                <div class=\"col-9\">\n" +
                    "                    <a class=\"d-flex w-100 justify-content-between\" id=\"questionLink"+ response.items[i].id +"\" href=\"/question/"+ response.items[i].id +"\" onclick=\"openContent(id, 'question')\" >\n" +
                    "                        <p style=\"color:#0077cb; font-size: 125% \" >" + response.items[i].title + "</p>\n" +
                    "                        <small style='color: #6a737c'> задан "+ stringDate + "</small>\n" +
                    "                    </a>\n" +
                    "                    <div class=\"nav-col btn-group  btn-block mr-0   \">\n" +
                                            allTagsView +
                    "                    </div>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>"
                )
            }
            $('#buttonToQuestionArea').append(
                "            <div class=\"text-right\">\n" +
                "                <a type=\"button\" class=\"btn btn-primary\" href=\"/questionAria\" id=\"otherQuest\">Посмотреть другие вопросы</a>\n" +
                "            </div>"
            )
        })
    }
}
