function changeColor(index) {
    $('#colorForm\\:colorTable\\:' + index + '\\:colorColumn')
        .css('background-color', $('#colorForm\\:colorTable\\:' + index + '\\:colorPicker_livePreview').css('background-color'));
}

function changeValue(index) {
    $('#colorForm\\:colorTable\\:' + index + '\\:colorPicker_livePreview')
        .text('#' + $('#colorForm\\:colorTable\\:' + index + '\\:colorPicker_input').val());
}

function changeColor2(index, color) {
    $('#colorForm\\:colorTable\\:' + index + '\\:colorColumn').css('background-color', color).css('height', '31px');
}
