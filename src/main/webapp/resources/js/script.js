PrimeFaces.locales ['ru'] = {
    closeText: 'Закрыть',
    prevText: 'Назад',
    nextText: 'Вперёд',
    monthNames: ['Январь', 'Февраль' , 'Март' , 'Апрель' , 'Май' , 'Июнь' , 'Июль' , 'Август' , 'Сентябрь','Октябрь','Ноябрь','Декабрь' ],
    monthNamesShort: ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек' ],
    dayNames: ['Воскресенье', 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Суббота'],
    dayNamesShort: ['Воск','Пон' , 'Вт' , 'Ср' , 'Четв' , 'Пят' , 'Суб'],
    dayNamesMin: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
    weekHeader: 'Неделя',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix:'',
    timeOnlyTitle: 'Только время',
    timeText: 'Время',
    hourText: 'Час',
    minuteText: 'Минута',
    secondText: 'Секунда',
    currentText: 'Сегодня',
    ampm: false,
    month: 'Месяц',
    week: 'неделя',
    day: 'День',
    allDayText: 'Весь день'
}

// Функции для colorPicker в настроечной форме Расцветка параметров

function initColorPicker(index) {
    let colorPicker = $('#colorForm\\:colorTable\\:' + index + '\\:colorPicker_livePreview');

    $('#colorForm\\:colorTable\\:' + index + '\\:colorColumn').parent()
        .css('background-color', colorPicker.css('background-color'));
    colorPicker
        .text('#' + $('#colorForm\\:colorTable\\:' + index + '\\:colorPicker_input').val());
}

function initColorPickerRead(index, color) {
    $('#colorForm\\:colorTable\\:' + index + '\\:colorColumn').parent().css('background-color', color).css('height', '31px');
}

function changeColor(index) {
    initColorPicker(index);
    $('#colorForm\\:colorTable\\:' + index + '\\:descColumn').parent()
        .css('background-color', 'lightgrey');
}

// Функция для работы с кнопкой скрыть навигацию
function collapseButtonClick() {
    if (document.getElementById('leftMenuPanel').style.marginLeft === '-290px') {
        document.getElementById('leftMenuPanel').style.marginLeft = '';
        document.getElementById('collapseButtonIcon').style.display = '';
        document.getElementById('collapseButton').className = 'collapseButton';
        document.getElementById('collapseButton').title = 'Скрыть панель навигации';
    } else {
        document.getElementById('leftMenuPanel').style.marginLeft = '-290px';
        document.getElementById('collapseButtonIcon').style.display = 'none';
        document.getElementById('collapseButton').className = 'collapseButton collapsed';
        document.getElementById('collapseButton').title = 'Показать панель навигации';
    }
}

// Функция инициирует открытие блока правки для dataTable
// Первый аргумент это widgetVar
// Второй аргумент это индекс строки. Если второго аргумента нет, то берется последний индекс строки.
function switchToRowEdit() {
    let widget = PF(arguments[0]);
    let rowIndex = 0;

    if (arguments.length === 1) {
        rowIndex = widget.tbody[0].children.length - 1;
    }
    if (arguments.length === 2) {
        rowIndex = arguments[1];
    }

    widget.switchToRowEdit(widget.findRow(rowIndex));
}