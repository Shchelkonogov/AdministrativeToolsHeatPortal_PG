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

    const withinBoundariesFilterParamTree = event.composedPath().includes(document.querySelector('#filterParamTreeForm'))
        || event.composedPath().includes(document.querySelector('#filterParamTreeForm\\:filterParamTreeSelectOneMenu_panel'));
    const filterParamTreeButton = event.composedPath().includes(document.querySelector('#linkerForm\\:linkerTabView\\:objectTabView\\:filterParamTreeBtn'));

    if (!withinBoundariesFilterParamTree && !filterParamTreeButton) {
        hide('filterParamTreeForm');
    }

    const withinBoundariesFilterParamOpcTree = event.composedPath().includes(document.querySelector('#filterParamOpcTreeForm'))
        || event.composedPath().includes(document.querySelector('#filterParamOpcTreeForm\\:filterParamOpcTreeSelectOneMenu_panel'));
    const filterParamOpcTreeButton = event.composedPath().includes(document.querySelector('#linkerForm\\:linkerTabView\\:objectTabView\\:filterParamOpcTreeBtn'));

    if (!withinBoundariesFilterParamOpcTree && !filterParamOpcTreeButton) {
        hide('filterParamOpcTreeForm');
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

/**
 * Доработка интерфейса для dragAndDrop (нам не требуются dropPoint)
 * @param widget
 */
function initDragDropForTree(widget) {
    PF(widget).jq.find('li.ui-tree-droppoint').remove();
    PF(widget).initDraggable();
    PF(widget).initDroppable();
}

/**
 * Перемещение scroll к выделенному элементу дерева
 * @param widgetVar имя виджета
 */
function scrollToSelectedNode(widgetVar) {
    const selectedElement = PF(widgetVar).jq.find('li .ui-state-highlight');
    if (selectedElement != null && selectedElement.position() !== undefined) {
        const scrollPanel = PF(widgetVar).jq.parent();
        scrollPanel.scrollTop(selectedElement.position().top - scrollPanel.height() / 2);
    }
}

/**
 * Обработчик ajaxCallBack в primeFaces
 * @param xhr
 * @param status
 * @param args
 */
function handleMsg(xhr, status, args) {
    switch (args.command) {
        case "updateAfterSelectWrapper":
            updateParamTrees([{name: 'widgetName', value: args.widgetName}, {name: 'rowKey', value: args.rowKey}]);
            break;
        case "updateAfterSelect":
            PF(args.widgetName).selectNode(PF(args.widgetName).jq.find('[data-rowkey="' + args.rowKey + '"]'), true);
            scrollToSelectedNode(args.widgetName);
            break;
    }
}

/**
 * Снятие выделения элемента дерева по данным из js объекта
 * @param widgetVar имя виджета
 * @param silent отправлять ли запрос в bean
 */
function unselectNode(widgetVar, silent) {
    if (PF(widgetVar).selections.length !== 0) {
        PF(widgetVar).unselectNode(PF(widgetVar).jq.find('[data-rowkey="' + PF(widgetVar).selections[0] + '"]'), silent === undefined ? true : silent);
    }
}

/**
 * Выделения элемента дерева по данным из js объекта
 * @param widgetVar имя виджета
 */
function selectNode(widgetVar) {
    if (PF(widgetVar).selections.length !== 0) {
        PF(widgetVar).selectNode(PF(widgetVar).jq.find('[data-rowkey="' + PF(widgetVar).selections[0] + '"]'), false);
    }
}