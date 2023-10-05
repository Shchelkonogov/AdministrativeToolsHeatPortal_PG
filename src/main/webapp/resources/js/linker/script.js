/**
 * Изменение видимости для переданного элемента, используется для отображения и закрытия окошек около кнопок
 * @param id окна
 * @param args для инициализации, id элементов нажатие на которых является нажатием на окно
 *             (что бы оно закрывалось при нажатии вне окна)
 */
function changeVisible(id, ...args) {
    const display = document.getElementById(id).style.display
    switch (display) {
        case 'block':
            $(document.body).off('click.' + id);

            document.getElementById(id).style.display = 'none';
            break;
        case 'none':
            $(document.body).on('click.' + id, (event) => {
                if (event.originalEvent !== undefined) {
                    let checkClick = event.originalEvent.composedPath().includes(document.querySelector('#' + id.replaceAll(':', '\\:')));

                    for (const arg of args) {
                        checkClick = checkClick || event.originalEvent.composedPath().includes(document.querySelector('#' + arg.replaceAll(':', '\\:')));
                    }

                    if (!checkClick) {
                        changeVisible(id);
                    }
                }
            });

            document.getElementById(id).style.display = 'block';
            break;
    }
}

/**
 * Функция изменения свойства стиля для элемента
 * @param id элемента
 * @param name имя свойства
 * @param value значение свойства
 */
function changeStyle(id, name, value) {
    document.getElementById(id).style.setProperty(name, value);
}

// Если в таблице нет строк и включена группировка строк, то там глюк на одну колонку
function updateEmptyRow() {
    const tag = $('#linkerForm\\:linkerTabView\\:objectTabView\\:linkedDataTable_data .ui-datatable-empty-message').children()
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