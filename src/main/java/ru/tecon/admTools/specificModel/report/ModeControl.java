package ru.tecon.admTools.specificModel.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.tecon.admTools.specificModel.report.ejb.ModeControlLocal;
import ru.tecon.admTools.specificModel.report.model.ModeControlModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Класс формирует отчет "Контроль режима"
 */
public final class ModeControl {

    /**
     * Метод формирует excel файл отчета "Контроль режима"
     * @param objType тип объекта
     * @param filterType тип фильтра
     * @param filter текст фильтра
     * @param structID id структуры
     * @param paramID id параметра
     * @param user имя пользователя
     * @param bean класс для получения данных реализующий интерфейс {@link ModeControlLocal}
     * @return возвращает excel {@link Workbook}
     */
    public static Workbook generateModeControl(int objType, int filterType, String filter, int structID, int paramID,
                                               String user, ModeControlLocal bean) {
        Workbook wb = new XSSFWorkbook();

        XSSFFont fontBold14 = (XSSFFont) wb.createFont();
        fontBold14.setFontName("Times New Roman");
        fontBold14.setBold(true);
        fontBold14.setFontHeight(14);

        XSSFFont font12 = (XSSFFont) wb.createFont();
        fontBold14.setFontName("Times New Roman");
        font12.setFontHeight(12);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(fontBold14);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setFont(font12);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle borderCenterStyle = wb.createCellStyle();
        borderCenterStyle.setFont(font12);
        borderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        borderCenterStyle.setBorderTop(BorderStyle.THIN);
        borderCenterStyle.setBorderRight(BorderStyle.THIN);
        borderCenterStyle.setBorderBottom(BorderStyle.THIN);
        borderCenterStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle boldBorderCenterStyle = wb.createCellStyle();
        boldBorderCenterStyle.setFont(fontBold14);
        boldBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        boldBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        boldBorderCenterStyle.setBorderTop(BorderStyle.THIN);
        boldBorderCenterStyle.setBorderRight(BorderStyle.THIN);
        boldBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        boldBorderCenterStyle.setBorderLeft(BorderStyle.THIN);

        Sheet sheet = wb.createSheet("Контроль режима");

        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 18 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 21 * 256);
        sheet.setColumnWidth(4, 21 * 256);
        sheet.setColumnWidth(5, 16 * 256);
        sheet.setColumnWidth(6, 16 * 256);
        sheet.setColumnWidth(7, 16 * 256);
        sheet.setColumnWidth(8, 16 * 256);
        sheet.setColumnWidth(9, 17 * 256);

        Row row = sheet.createRow(1);
        createStyledCell(row, 1, "Контроль заданного режима", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 10));

        row = sheet.createRow(2);
        createStyledCell(row, 1, bean.getParamName(paramID), headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 10));

        row = sheet.createRow(3);
        createStyledCell(row, 1, bean.getStructPath(structID), headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 10));

        row = sheet.createRow(6);
        createStyledCell(row, 5, "Нижняя", boldBorderCenterStyle);
        createStyledCell(row, 6, "Верхняя", boldBorderCenterStyle);
        createStyledCell(row, 7, "Нижняя", boldBorderCenterStyle);
        createStyledCell(row, 8, "Верхняя", boldBorderCenterStyle);

        row = sheet.createRow(5);

        CellRangeAddress cellAddresses = new CellRangeAddress(5, 6, 1, 1);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 1, "№ Абонента", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(5, 6, 2, 2);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 2, "Адрес", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(5, 6, 3, 3);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 3, "График/Значение", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(5, 6, 4, 4);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 4, "Снижение", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(5, 5, 5, 6);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 5, "Технологические границы", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(5, 5, 7, 8);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 7, "Аварийные границы", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(5, 6, 9, 9);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 9, "Стат Агрегат", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        List<ModeControlModel> data = bean.getData(objType, structID, filterType, filter, paramID, user);
        int index = 7;
        for (ModeControlModel item: data) {
            row = sheet.createRow(index);
            createStyledCell(row, 1, item.getObjectName(), borderStyle);
            createStyledCell(row, 2, item.getAddress(), borderStyle);
            createStyledCell(row, 3, item.getGraph(), borderCenterStyle);
            createStyledCell(row, 4, item.getDecrease(), borderCenterStyle);
            createStyledCell(row, 5, item.gettMin(), borderCenterStyle);
            createStyledCell(row, 6, item.gettMax(), borderCenterStyle);
            createStyledCell(row, 7, item.getaMin(), borderCenterStyle);
            createStyledCell(row, 8, item.getaMax(), borderCenterStyle);
            createStyledCell(row, 9, item.getAgrName(), borderCenterStyle);
            index++;
        }

        String sing = "Отчет сформирован " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        sheet.createRow(index + 1).createCell(1).setCellValue(sing);
        sheet.addMergedRegion(new CellRangeAddress(index + 1, index + 1, 1, 2));

        return wb;
    }

    private static void createStyledCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void addAllBorders(CellRangeAddress cellAddresses,Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
    }
}
