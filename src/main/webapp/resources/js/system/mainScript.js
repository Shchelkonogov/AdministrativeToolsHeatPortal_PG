/**
 * Функция проверяет запущенна ли страница в iframe
 * @returns {boolean} true - если iframe
 */
function inIframe() {
    try {
        return window.self !== window.top;
    } catch (e) {
        return true;
    }
}