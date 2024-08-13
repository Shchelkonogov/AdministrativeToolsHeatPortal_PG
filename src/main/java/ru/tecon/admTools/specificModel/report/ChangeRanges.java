package ru.tecon.admTools.specificModel.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.tecon.admTools.specificModel.report.ejb.ChangeRangesLocal;
import ru.tecon.admTools.specificModel.report.model.ChangeRangesModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Класс формирует отчет "Отчет по изменению тех границ"
 */
public final class ChangeRanges {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Метод формирует excel файл отчета "Отчет по изменению тех границ"
     * @param objType тип объекта
     * @param structID id структуры
     * @param objID id объекта
     * @param filterType тип фильтра
     * @param filter текст фильтра
     * @param date дата в формате dd.MM.yyyy
     * @param user имя пользователя
     * @param bean класс для получения данных реализующий интерфейс {@link ChangeRangesLocal}
     * @return возвращает excel {@link Workbook}
     */
    public static Workbook generateChangeRanges(int objType, Integer structID, Integer objID, int filterType,
                                                String filter, String date, String user, ChangeRangesLocal bean) {
        return generateChangeRanges(objType, structID, objID, filterType, filter, date, user, bean, false);
    }

    /**
     * Метод формирует excel файл отчета "Отчет по изменению тех границ"
     * @param objType тип объекта
     * @param structID id структуры
     * @param objID id объекта
     * @param filterType тип фильтра
     * @param filter текст фильтра
     * @param date дата в формате dd.MM.yyyy
     * @param user имя пользователя
     * @param bean класс для получения данных реализующий интерфейс {@link ChangeRangesLocal}
     * @param eco обозначает использование данных для экомониторинга
     * @return возвращает excel {@link Workbook}
     */
    public static Workbook generateChangeRanges(int objType, Integer structID, Integer objID, int filterType,
                                                String filter, String date, String user, ChangeRangesLocal bean,
                                                boolean eco) {
        Workbook wb = new XSSFWorkbook();

        XSSFFont font = (XSSFFont) wb.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        font.setFontName("Times New Roman");

        XSSFFont font16Bold = (XSSFFont) wb.createFont();
        font16Bold.setBold(true);
        font16Bold.setFontHeight(16);
        font16Bold.setFontName("Times New Roman");

        XSSFFont font16 = (XSSFFont) wb.createFont();
        font16.setFontHeight(16);
        font16.setFontName("Times New Roman");

        XSSFFont font12 = (XSSFFont) wb.createFont();
        font12.setFontHeight(12);
        font12.setFontName("Times New Roman");

        CellStyle headerBoldStyle = wb.createCellStyle();
        headerBoldStyle.setFont(font16Bold);
        headerBoldStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(font16);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle style = wb.createCellStyle();
        style.setFont(font12);

        CellStyle boldBorderCenterStyle = wb.createCellStyle();
        boldBorderCenterStyle.setFont(font);
        boldBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        boldBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        boldBorderCenterStyle.setBorderTop(BorderStyle.MEDIUM);
        boldBorderCenterStyle.setBorderRight(BorderStyle.MEDIUM);
        boldBorderCenterStyle.setBorderBottom(BorderStyle.MEDIUM);
        boldBorderCenterStyle.setBorderLeft(BorderStyle.MEDIUM);

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setFont(font12);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle borderCenterStyle = wb.createCellStyle();
        borderCenterStyle.setFont(font12);
        borderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        borderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        borderCenterStyle.setBorderTop(BorderStyle.THIN);
        borderCenterStyle.setBorderRight(BorderStyle.THIN);
        borderCenterStyle.setBorderBottom(BorderStyle.THIN);
        borderCenterStyle.setBorderLeft(BorderStyle.THIN);

        Sheet sheet = wb.createSheet("Изменения тех границ");
        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 25 * 256);
        sheet.setColumnWidth(5, 20 * 256);
        sheet.setColumnWidth(6, 22 * 256);
        sheet.setColumnWidth(7, 22 * 256);
        sheet.setColumnWidth(8, 30 * 256);

        createStyledCell(sheet.createRow(1), 1, "ПАО \"МОЭК\": АС \"ТЕКОН - Диспетчеризация\"", headerBoldStyle);
        createStyledCell(sheet.createRow(2), 1, "Отчет по изменениям технологических границ", headerBoldStyle);
        createStyledCell(sheet.createRow(3), 1, bean.getPath(structID, objID), headerStyle);
        createStyledCell(sheet.createRow(4), 1, "Начальная дата: " + date, headerStyle);
        for (int i = 1; i <= 4; i++) {
            sheet.addMergedRegion(new CellRangeAddress(i, i, 1, 8));
        }

        String sing = "Отчет сформирован " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        createStyledCell(sheet.createRow(6), 1, sing, style);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 2));

        Row row = sheet.createRow(8);
        createStyledCell(row, 1, "Дата", boldBorderCenterStyle);
        createStyledCell(row, 2, "Объект", boldBorderCenterStyle);
        createStyledCell(row, 3, "Параметр", boldBorderCenterStyle);
        createStyledCell(row, 4, "Стат. Агрегат", boldBorderCenterStyle);
        createStyledCell(row, 5, "Тип границы", boldBorderCenterStyle);
        createStyledCell(row, 6, "Старое значение", boldBorderCenterStyle);
        createStyledCell(row, 7, "Новое значение", boldBorderCenterStyle);
        createStyledCell(row, 8, "Пользователь", boldBorderCenterStyle);

        List<ChangeRangesModel> data = bean.loadReportData(objType, structID, objID, filterType, filter, date, user, eco);

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(9 + i);
            createStyledCell(row, 1, data.get(i).getDate().format(formatter), borderCenterStyle);
            createStyledCell(row, 2, data.get(i).getObjectName(), borderStyle);
            createStyledCell(row, 3, data.get(i).getParMemo(), borderStyle);
            createStyledCell(row, 4, data.get(i).getStatAgrName(), borderCenterStyle);
            createStyledCell(row, 5, data.get(i).getDescription(), borderCenterStyle);
            createStyledCell(row, 6, data.get(i).getOldValue(), borderCenterStyle);
            createStyledCell(row, 7, data.get(i).getNewValue(), borderCenterStyle);
            createStyledCell(row, 8, data.get(i).getUserName(), borderCenterStyle);
        }

        return wb;
    }

    private static void createStyledCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
