class SearchService {
    _searchApiUrl = '/api/search'
    async getSearchResults(searchQuery, page, size, sort="score", order="desc") {
        let query = `${this._searchApiUrl}/?query=${searchQuery}&page=${page}&size=${size}&sort=${sort}&order=${order}`;

        let response = await fetch(encodeURI(query), {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        });

        if (response.ok) {
            return await response.json();
        } else {
            console.error("Ошибка Http: " + response.status)
            return response.error();
        }
    }

    async getDescriptionSearchOperators() {
        const query = `${this._searchApiUrl}/search-operators`
        const response = await fetch(encodeURI(query), {
            method: 'GET',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': $.cookie("token")
            })
        })

        if (response.ok) {
            return response.json()
        } else {
            console.error("Ошибка Http: " + response.status)
            return response.error();
        }
    }
}