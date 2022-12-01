function blockRow(index) {
    var element;

    element = $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:graphCellEditor');
    element.parent().removeClass('ui-editable-column');
    element.parent().addClass('gray');
    $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:graphContext').remove();

    element = $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:tMinPanel');
    element.parent().removeClass('ui-editable-column');
    element.parent().addClass('gray');
    $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:tMinContext').remove();

    element = $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:tMaxPanel');
    element.parent().removeClass('ui-editable-column');
    element.parent().addClass('gray');
    $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:tMaxContext').remove();

    element = $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:aMinPanel');
    element.parent().removeClass('ui-editable-column');
    element.parent().addClass('gray');
    $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:aMinContext').remove();

    element = $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:aMaxPanel');
    element.parent().removeClass('ui-editable-column');
    element.parent().addClass('gray');
    $('#tabView\\:tableForm\\:tableData\\:' + index + '\\:aMaxContext').remove();
}