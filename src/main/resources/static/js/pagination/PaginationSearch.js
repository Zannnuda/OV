$(document).ready(function () {
    let searchButton = document.getElementById("buttonSearch");
    let searchQuery = document.getElementById("site-search");
    let paginationNav = document.getElementById('search-pagination');
    let sortButtons = document.querySelector('.btn-group.sort');
    let searchHelpBlock = document.getElementById("searchHelp");
    let searchButtonsSort = document.getElementById('search-buttons-sort')

    searchQuery.addEventListener('focusin', (event) => {
        searchHelpBlock.classList.remove("d-none");
        let searchInputCoords = getCoords(searchQuery);
        searchHelpBlock.style.width = searchQuery.offsetWidth + "px";
        searchHelpBlock.style.top = searchInputCoords.bottom + "px";
        searchHelpBlock.style.left = searchInputCoords.left + "px";
    })

    searchQuery.addEventListener('focusout', (event) => {
        searchHelpBlock.classList.add("d-none");
    })

    function getCoords(elem) {
        let box = elem.getBoundingClientRect();

        return {
            bottom: box.bottom + pageYOffset,
            left: box.left + pageXOffset
        };
    }

    searchButton.addEventListener('click', (event) => {
        event.preventDefault();

        openContent("searchResult", "searchResult");

        // если поле поиска не заполнено, то покажем список поисковых операторов
        if (searchQuery.value.trim().length === 0) {
            new SearchService().getDescriptionSearchOperators()
                .then(items => {
                    let resultTable = document.querySelector('.search-result-table');
                    let pagination = document.getElementById('search-pagination').firstElementChild;
                    let searchButtonsSort = document.getElementById('search-buttons-sort')

                    console.log(items)

                    pagination.innerHTML = '';
                    searchButtonsSort.classList.add('invisible')
                    resultTable.innerHTML = descriptionSearchOperatorsCard(items);
                })

            return false; // если ничего нет, то и незачем выполнять остальную часть метода
        }

        new PaginationSearch(searchQuery.value, 1, 10).populateSearchResults();

        searchQuery.blur();
    });

    paginationNav.addEventListener('click', (event) => {
        event.preventDefault();
        let target = event.target;
        let p = target.closest('.page-link').textContent;

        new PaginationSearch(searchQuery.value, p, 10).populateSearchResults();
    });

    sortButtons.addEventListener('click', (event) => {
        event.preventDefault();
        let target = event.target;
        toggleButton(target);
    });

    function toggleButton(button) {
        if (button.classList.contains('active')) {
            button.classList.toggle('desc');
            button.classList.toggle('asc');
            return;
        }

        for (let i = 0; i < sortButtons.children.length; i++) {
            sortButtons.children[i].classList.remove('active', 'desc', 'asc');
            sortButtons.children[i].classList.replace('btn-primary', 'btn-secondary');
        }

        button.classList.replace('btn-secondary', 'btn-primary');
        button.classList.add('active', 'desc');
        console.log(button.dataset.sort);
        new PaginationSearch(searchQuery.value, 1, 10, button.dataset.sort).populateSearchResults();
    }

    function openContent(evt, contentName) {
        var i, tabcontent, tablinks;

        tabcontent = document.getElementsByClassName("tabcontent");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }

        tablinks = document.getElementsByClassName("tablinks");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }

        document.getElementById(contentName).style.display = "block";
        document.getElementById(evt).className += " active";
    }
});

function descriptionSearchOperatorsCard(items) {

    const listItems = Object.values(items)
        .map(item => {
            return `<tr>
                        <th>${item[0]}</th>
                        <td>${item[1]}</td>
                    </tr>`
        }).join('')

    return `<table class="table table-striped">
                      <thead>
                            <tr>
                              <th scope="col">Тип поиска</th>
                              <th scope="col">Синтаксис поиска</th>
                            </tr>
                      </thead>
                      <tbody>
                            ${listItems}
                      </tbody>
                </table>`
}

class PaginationSearch {
    constructor(searchQuery, page, size, sort = "score", order = "desc") {
        this.searchQuery = searchQuery;
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.order = order;

        this.searchService = new SearchService();
        this.searchResults = this.searchService.getSearchResults(this.searchQuery, this.page, this.size, this.sort, this.order);
    }

    async populateSearchResults() {
        let resultTable = document.querySelector('.search-result-table');
        let pagination = document.getElementById('search-pagination').firstElementChild;

        let pages = await this.searchResults;

        resultTable.innerHTML = '';
        pagination.innerHTML = '';

        let items = pages.items;

        items.forEach(item => {
            if (item.type === "QUESTION") {
                resultTable.insertAdjacentHTML('beforeend', (this.questionCard(item)));
            } else if (item.type === "ANSWER") {
                resultTable.insertAdjacentHTML('beforeend', (this.answerCard(item)));
            }
        });

        pagination.insertAdjacentHTML('beforeend', this.paginate(pages.currentPageNumber, pages.totalPageCount))

    }

    paginate(currentPage, totalPages) {

        let paginationHtml = '';

        for (let i = 1; i <= totalPages; i++) {
            if (i === currentPage) {
                paginationHtml += `<li class="page-item active" aria-current="page">
                                        <span class="page-link">${i}</span>
                                    </li>`
            } else {
                paginationHtml += `<li class="page-item"><a class="page-link" href="#">${i}</a></li>`;
            }
        }

        return paginationHtml;

    }

    questionCard(item) {
        let tagsHtml = this.questionCardTags(item.tags);

        let date = this.getPersistDate(item.persistDate);

        let body = item.description.replace(/<\/?("[^"]*"|'[^']*'|[^>])*(>|$)/g, "");

        let questionCardHtml =
            `<div class="question-card d-flex">
            <div class="container">
                <div class="row">
                    <div class="col-sm-2">
                        <div class="question-stats-container">
                            <div class="stats">
                                <div class="vote">
                                    <div class="vote-count"><strong>${item.votesCount}</strong></div>
                                    <div class="view-count-vote">голосов</div>
                                    <div class="view-count-answer">
                                        <div class="status-unanswered"><strong>${item.answersCount}</strong></div>
                                        <div class="view-count">ответов</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-9">
                        <div class="question-title"><a style="color: #0077cb; font-size: 125%" id="questionLink1"
                                                       href="/question/${item.questionId}" onclick="openContent(id, 'question')">Q: ${item.title} </a></div>
                        <div class="question-text">${body} </div>
                        <div class="d-flex justify-content-between text-height-1">
                            <div class="question-card-tags">
                                ${tagsHtml}
                            </div>
                            <div class="question-card-author">
                                <div class="mt-1">
                                    <div class="user-info-change">
                                        <a href="#" class="user-info-change-link">спросил ${date} </a>
                                    </div>
                                    <div class="ml-0">
                                        <a class="ml-0 small" href="#">
                                            ${item.authorName} 
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>`

        return questionCardHtml;
    }

    answerCard(item) {
        let tagsHtml = this.questionCardTags(item.tags);

        let date = this.getPersistDate(item.persistDate);

        let body = item.description.replace(/<\/?("[^"]*"|'[^']*'|[^>])*(>|$)/g, "");

        let questionCardHtml =
            `<div class="question-card d-flex">
            <div class="container">
                <div class="row">
                    <div class="col-sm-2">
                        <div class="question-stats-container">
                            <div class="stats">
                                <div class="vote">
                                    <div class="vote-count"><strong>${item.votesCount}</strong></div>
                                    <div class="view-count-vote">голосов</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-9">
                        <div class="question-title"><a style="color: #0077cb; font-size: 125%" id="questionLink1"
                                                       href="/question/${item.questionId}" onclick="openContent(id, 'question')">A: ${item.title} </a></div>
                        <div class="question-text">${body} </div>
                        <div class="d-flex justify-content-between text-height-1">
                            <div class="question-card-tags">
                                ${tagsHtml}
                            </div>
                            <div class="question-card-author">
                                <div class="mt-1">
                                    <div class="user-info-change">
                                        <a href="#" class="user-info-change-link">ответил ${date} </a>
                                    </div>
                                    <div class="ml-0">
                                        <a class="ml-0 small" href="#">
                                            ${item.authorName} 
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>`

        return questionCardHtml;
    }

    getPersistDate(persistDateTime) {
        const date = new Date(persistDateTime);
        const stringDate = ('0' + date.getDate()).slice(-2) + "."
            + ('0' + (date.getMonth() + 1)).slice(-2) + "."
            + date.getFullYear() + " " + ('0' + date.getHours()).slice(-2) + ":"
            + ('0' + date.getMinutes()).slice(-2);

        return stringDate;
    }

    questionCardTags(tags) {
        let tagsHtml = '';

        tags.forEach(tag => {
            tagsHtml += `<a href="#" class="mb-1 mt-3"> ${tag.name} </a>`
        });

        return tagsHtml;
    }

}