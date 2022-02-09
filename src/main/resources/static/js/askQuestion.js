let buttonAskQuestion = document.getElementById('buttonAskQuestion');
let buttonCreateTag = document.getElementById('create-tag');
let tagBlock = document.getElementById('tags-block');
let createTagBlock = document.getElementById('tag-add-block');
let tagsInInput = document.querySelector('.input-group-prepend');
let tagsSearchInput = document.getElementById('tags');
let tagDescriptionElement = document.getElementById('new-tag-description');
let tempTags = [];
let tags = [];
let newQuestionId = 0;
let userId;

$(document).ready(function () {
    getUserId();
});

function addTagToInput(tagName) {
    let tagAddon = document.createElement('span');
    tagAddon.classList.add('tag-addon', 'input-group-text', 'bg-info', 'text-light');
    tagAddon.innerHTML = `<span class="tag-title">${tagName}</span><a href="#" class="delete-button badge badge-secondary ml-1">x</a>`;
    tagsInInput.append(tagAddon);
}
function checkTitle() {
    let title = $('#questionTitle').val();
    if (title.length != 0) {
        checkDescription()
    } else if (title.length == 0) {
        buttonAskQuestion.setAttribute('disabled', 'true');
        checkTagsCount()
    }
}

function checkDescription() {
    let description = $('#summernote').summernote('code');
    if (description.length != 0) {
        buttonAskQuestion.removeAttribute('disabled');
        checkTagsCount()
    } else if (description.length == 0) {
        buttonAskQuestion.setAttribute('disabled', 'true');
        checkTagsCount()
    }
}

function checkTagsCount() {
    if (tags.length > 0) {
        checkTitle()
        checkDescription()
    } else if (tags.length === 0) {
        buttonAskQuestion.setAttribute('disabled', 'true');
    }
    if (tags.length === 5) {
        tagsSearchInput.setAttribute('disabled', 'true');
        tagsSearchInput.classList.add('d-none');
    } else if (tags.length < 5) {
        tagsSearchInput.removeAttribute('disabled');
        tagsSearchInput.classList.remove('d-none');
    }

}

tagBlock.addEventListener('click', (event) => {
    let target = event.target;
    let tagName = target.closest('.tag').querySelector('.tag-name').textContent;

    addTagToInput(tagName);

    tags.push(tempTags.find(tag => tag.name === tagName.trim()));

    tagBlock.innerHTML = '';
    tagsSearchInput.value = '';
    checkTagsCount();
    tagsSearchInput.focus();
});

tagsInInput.addEventListener('click', (event) => {
    event.preventDefault();
    let target = event.target;
    if (target.closest('.delete-button')) {
        let tagName = target.closest('.tag-addon').querySelector('.tag-title').textContent.trim();
        tags = tags.filter(tag => tag.name !== tagName);
        target.closest('.tag-addon').remove();
        checkTagsCount();
    }
});

tagsSearchInput.addEventListener('input', async () => {
    let tagName = tagsSearchInput.value;
    if (tagName === '') {
        tagBlock.innerHTML = '';
        createTagBlock.classList.add('d-none');
        return
    }
    let foundTags = await getTags(tagName);
    if (foundTags.totalResultCount === 0) {
        createTagBlock.classList.remove('d-none');
    } else {
        createTagBlock.classList.add('d-none');
    }
    tempTags = foundTags.items.filter(tag => !tags.find(t => t.id === tag.id));
    populateTagBlock(tempTags);
})

function populateTagBlock(tags) {
    tagBlock.innerHTML = '';
    tags.forEach(item => {
        let oneTagBlock = document.createElement('div');
        oneTagBlock.className = 'tag';
        oneTagBlock.innerHTML = `<div class="tag-header">
                                    <span class="tag-name badge badge-primary">
                                        ${item.name}
                                    </span>
                                    <span class="tag-count badge badge-info">
                                        ${item.countQuestion}
                                    </span>
                                 </div>
                                 <div class="tag-description">
                                    ${item.description}
                                 </div>`;
        tagBlock.append(oneTagBlock);
    })

}

async function getTags(name) {
    let query = `/api/tag/name/?name=${name}&page=1&size=6`
    let response = await fetch(query, {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    });

    let tags = await response.json();
    return tags;
}

buttonCreateTag.addEventListener('click', (event) => {
    event.preventDefault();
    let tagDto = {
        name: tagsSearchInput.value,
        description: tagDescriptionElement.value
    };

    fetch('/api/tag/add', {
        method: 'POST',
        headers: {
            'content-type': 'application/json;charset=utf-8', 'Authorization': $.cookie("token")
        },
        body: JSON.stringify(tagDto)
    })
        .then(response => response.json())
        .then(newTag => {
            addTagToInput(newTag.name);
            tags.push(newTag);
            tagDescriptionElement.value = '';
            tagsSearchInput.value = '';
            tagsSearchInput.focus();
            createTagBlock.classList.add('d-none');
            checkTagsCount();
        })
})

tagDescriptionElement.addEventListener('input', event => {
    if (tagDescriptionElement.value === '') {
        buttonCreateTag.setAttribute('disabled', 'true');
    } else {
        buttonCreateTag.removeAttribute('disabled');
    }
})


document.querySelector('.tip-button-1').onclick = function () {
    document.querySelector('.tip-text-1').classList.toggle('d-none');
}

document.querySelector('.tip-button-2').onclick = function () {
    document.querySelector('.tip-text-2').classList.toggle('d-none');
}

document.querySelector('.tip-button-3').onclick = function () {
    document.querySelector('.tip-text-3').classList.toggle('d-none');
}

document.querySelector('.tip-button-4').onclick = function () {
    document.querySelector('.tip-text-4').classList.toggle('d-none');
}

buttonAskQuestion.onclick = function (e) {
    e.preventDefault();

    let description = $('#summernote').summernote('code');

    let questionCreateDto = {
        title: $('#questionTitle').val(),
        userId: userId,
        description: description,
        tags: tags
    };

    if (!fetch('http://localhost:5557/api/question/add', {
        method: 'POST',
        headers: {
            'content-type': 'application/json;charset=utf-8', 'Authorization': $.cookie("token")
        },
        body: JSON.stringify(questionCreateDto)
    }).then(response => response.json()
    ).then(question => {
        newQuestionId = question.id;
        window.location.href = '/question/'+question.id;
    })) {
        alert('Вопрос не был добавлен');
    }
}

function getUserId() {
    fetch('/api/auth/principal', {
        method: 'GET',
        headers: new Headers({
            'Content-Type': 'application/json',
            'Authorization': $.cookie("token")
        })
    })
        .then(response => response.json())
        .then(principal => userId = principal['id'])
}

