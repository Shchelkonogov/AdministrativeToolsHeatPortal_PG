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
        Workbook wb = new XSSFWorkbook();

        XSSFFont font = (XSSFFont) wb.createFont();
        font.setBold(true);
        font.setFontHeight(14);

        XSSFFont font14 = (XSSFFont) wb.createFont();
        font14.setFontHeight(14);

        XSSFFont font12 = (XSSFFont) wb.createFont();
        font12.setFontHeight(12);

        CellStyle style14 = wb.createCellStyle();
        style14.setFont(font14);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(font);

        CellStyle boldBorderCenterStyle = wb.createCellStyle();
        boldBorderCenterStyle.setFont(font);
        boldBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        boldBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        boldBorderCenterStyle.setBorderTop(BorderStyle.THIN);
        boldBorderCenterStyle.setBorderRight(BorderStyle.THIN);
        boldBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        boldBorderCenterStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle borderCenterStyle = wb.createCellStyle();
        borderCenterStyle.setFont(font12);
        borderCenterStyle.setBorderTop(BorderStyle.THIN);
        borderCenterStyle.setBorderRight(BorderStyle.THIN);
        borderCenterStyle.setBorderBottom(BorderStyle.THIN);
        borderCenterStyle.setBorderLeft(BorderStyle.THIN);

        Sheet sheet = wb.createSheet("Изменения тех границ");
        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 14 * 256);
        sheet.setColumnWidth(4, 25 * 256);
        sheet.setColumnWidth(5, 18 * 256);
        sheet.setColumnWidth(6, 20 * 256);
        sheet.setColumnWidth(7, 20 * 256);
        sheet.setColumnWidth(8, 20 * 256);

        createStyledCell(sheet.createRow(1), 1, "Отчет по изменениям технологических границ", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 10));

        Row row = sheet.createRow(3);
        createStyledCell(row, 1, "Структура:", style14);
        createStyledCell(row, 2, bean.getPath(structID, objID), boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 10));

        row = sheet.createRow(4);
        createStyledCell(row, 1, "Дата:", style14);
        createStyledCell(row, 2, date, boldStyle);

        row = sheet.createRow(6);
        createStyledCell(row, 1, "Дата", boldBorderCenterStyle);
        createStyledCell(row, 2, "Объект", boldBorderCenterStyle);
        createStyledCell(row, 3, "Параметр", boldBorderCenterStyle);
        createStyledCell(row, 4, "Стат. Агрегат", boldBorderCenterStyle);
        createStyledCell(row, 5, "Тип границы", boldBorderCenterStyle);
        createStyledCell(row, 6, "Старое значене", boldBorderCenterStyle);
        createStyledCell(row, 7, "Новое значение", boldBorderCenterStyle);
        createStyledCell(row, 8, "Пользователь", boldBorderCenterStyle);

        List<ChangeRangesModel> data = bean.loadReportData(objType, structID, objID, filterType, filter, date, user);

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(7 + i);
            createStyledCell(row, 1, data.get(i).getDate(), borderCenterStyle);
            createStyledCell(row, 2, data.get(i).getObjectName(), borderCenterStyle);
            createStyledCell(row, 3, data.get(i).getParMemo(), borderCenterStyle);
            createStyledCell(row, 4, data.get(i).getStatAgrName(), borderCenterStyle);
            createStyledCell(row, 5, data.get(i).getDescription(), borderCenterStyle);
            createStyledCell(row, 6, data.get(i).getOldValue(), borderCenterStyle);
            createStyledCell(row, 7, data.get(i).getNewValue(), borderCenterStyle);
            createStyledCell(row, 8, data.get(i).getUserName(), borderCenterStyle);
        }

        String sing = "Отчет сформирован " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        sheet.createRow(8 + data.size()).createCell(1).setCellValue(sing);
        sheet.addMergedRegion(new CellRangeAddress(8 + data.size(), 8 + data.size(), 1, 2));

        return wb;
    }

    private static void createStyledCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
