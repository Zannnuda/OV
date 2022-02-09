$( document ).ready(function() {
    $('#inputFilter').on('keyup', delay(function (e) {
        new PaginationTag(1,12,'search',document.getElementById('inputFilter').value).writeTags()
    }, 500));
    checkRedirect()
    new PaginationTag(1, 12, 'popular').writeTags()
    new PaginationTag(1, 10, 'popular').writeTopTenTags()
    new PaginationUser(1,20,'week').writeUsers()
    new PaginationQuestionWithoutAnswer(1,10, 'noSort').writeQuestionWithoutAnswer()
    new PaginationQuestionWithoutAnswer(1,10).totalResultCountView()
    if(/^\/question\//.test(window.location.pathname)){
        let tabcontent
        tabcontent = document.getElementsByClassName("tabcontent");
        for(let i=0; i<tabcontent.length; i++){
            tabcontent[i].style.display = "none";
        }
        document.getElementById("question").style.display = "block";
        let questionId = window.location.pathname.replace(/\D/g, '')
        let questionPage = new QuestionPage(questionId)
        questionPage.populateQuestionPage()
        questionPage.getColorForButtons(questionId)
    } else {
        switch (location.pathname) {
            case "/users": openContent("areaUsersLink", "areaUsers")
                break;
            case "/site": openContent("mainPageLink", "mainPage")
                break;
            case "/tagsAria": openContent("areaTagLink", "areaTag")
                break;
            case "/questionAria": openContent("areaQuestionLink", "areaQuestion")
                break;
            case "/unansweredAria": openContent("areaUnansweredLink", "areaUnanswered")
                break;
            case "/search": openContent("searchResult", "searchResult")
                break;
        }
    }

    function delay(callback, ms) {
        var timer = 0;
        return function() {
            var context = this, args = arguments;
            clearTimeout(timer);
            timer = setTimeout(function () {
                callback.apply(context, args);
            }, ms || 0);
        };
    }

    function openContent(evt, contentName){
        var i, tabcontent, tablinks;

        tabcontent = document.getElementsByClassName("tabcontent");
        for(i=0; i<tabcontent.length; i++){
            tabcontent[i].style.display = "none";
        }

        tablinks = document.getElementsByClassName("tablinks");
        for(i=0; i<tablinks.length; i++){
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }

        document.getElementById(contentName).style.display = "block";
        document.getElementById(evt).className += " active";
    }

    function checkRedirect(){
        let url = new URL(window.location.href)
        let tagId = url.searchParams.get("tagId")
        if(tagId != null){
            console.log(tagId)
            new PaginationQuestion(1,10, "withTags", tagId).setQuestions()
        } else {
            new PaginationQuestion(1,10,'normal').setQuestions()
            new PaginationQuestionForMainPage(1,10, 'new').setQuestions()
        }
    }
})