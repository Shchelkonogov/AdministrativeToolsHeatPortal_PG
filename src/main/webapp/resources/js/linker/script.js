// Функции для скрытия окошек по кнопкам "Пересчет" и "Фильтр" в случае нажатия не в окошко или в кнопку
document.addEventListener('click', (event) => {
    const withinBoundaries = event.composedPath().includes(document.querySelector('#filterForm'))
        || event.composedPath().includes(document.querySelector('#filterForm\\:filterSelectOneMenu_panel'));
    const filterButton = event.composedPath().includes(document.querySelector('#linkerForm\\:linkerTabView\\:objectTabView\\:filterBtn'));

    if (!withinBoundaries && !filterButton) {
        hide('filterForm');
    }

    const withinBoundariesRecount = event.composedPath().includes(document.querySelector('#recountForm'));
    const recountBtn = event.composedPath().includes(document.querySelector('#linkerForm\\:linkerTabView\\:objectTabView\\:recountBtn'));

    if (!withinBoundariesRecount && !recountBtn) {
        hide('recountForm');
    }
})

// Изменения видимости для переданного элемента, используется для отображения и закрытия окошке по кнопкам "Пересчет" и "Фильтр"
function changeVisible(id) {
    const display = document.getElementById(id).style.display
    switch (display) {
        case 'block':
            document.getElementById(id).style.display = 'none';
            break;
        case 'none':
            document.getElementById(id).style.display = 'block';
            break;
    }
}

// Скрытие элемента если он отображается по его id
function hide(id) {
    if (document.getElementById(id).style.display === 'block') {
        document.getElementById(id).style.display = 'none';
    }
}

// Если в таблице нет строк и включена группировка строк, то там глюк на одну колонку
function updateEmptyRow() {
    const tag = $('.ui-datatable-empty-message').children()
    const colspan = tag.prop('colspan');
    if (colspan !== undefined) {
        tag.prop('colspan', colspan + 1);
    }
}

// Двойное нажатие на колонку объекты системы в "Линкованные объекты / Объекты"
function addOnDblClickEvents() {
    for (let i = 0; i < 200; i++) {
        let element = $('#linkerForm\\:linkerTabView\\:objectTabView\\:linkedDataTable\\:' + i + '\\:systemObjectName');
        if (element !== undefined) {
            let attr = element.attr('ondblclick');
            element.parent().attr('ondblclick', attr);
            element.removeAttr('ondblclick');
        }
    }
}