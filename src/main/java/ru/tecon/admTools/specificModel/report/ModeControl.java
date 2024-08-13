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

        Sheet sheet = wb.createSheet("Контроль режима");

        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 42 * 256);
        sheet.setColumnWidth(3, 25 * 256);
        sheet.setColumnWidth(4, 25 * 256);
        sheet.setColumnWidth(5, 18 * 256);
        sheet.setColumnWidth(6, 18 * 256);
        sheet.setColumnWidth(7, 18 * 256);
        sheet.setColumnWidth(8, 18 * 256);
        sheet.setColumnWidth(9, 18 * 256);

        createStyledCell(sheet.createRow(1), 1, "ПАО \"МОЭК\": АС \"ТЕКОН - Диспетчеризация\"", headerBoldStyle);
        createStyledCell(sheet.createRow(2), 1, "Контроль заданного режима", headerBoldStyle);
        createStyledCell(sheet.createRow(3), 1, bean.getStructPath(structID), headerStyle);
        createStyledCell(sheet.createRow(4), 1, bean.getParamName(paramID), headerStyle);
        for (int i = 1; i <= 4; i++) {
            sheet.addMergedRegion(new CellRangeAddress(i, i, 1, 9));
        }

        String sing = "Отчет сформирован " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        createStyledCell(sheet.createRow(6), 1, sing, style);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 2));

        Row row = sheet.createRow(9);
        createStyledCell(row, 5, "Нижняя", boldBorderCenterStyle);
        createStyledCell(row, 6, "Верхняя", boldBorderCenterStyle);
        createStyledCell(row, 7, "Нижняя", boldBorderCenterStyle);
        createStyledCell(row, 8, "Верхняя", boldBorderCenterStyle);

        row = sheet.createRow(8);

        CellRangeAddress cellAddresses = new CellRangeAddress(8, 9, 1, 1);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 1, "№ Абонента", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(8, 9, 2, 2);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 2, "Адрес", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(8, 9, 3, 3);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 3, "График/Значение", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(8, 9, 4, 4);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 4, "Снижение", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(8, 8, 5, 6);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 5, "Технологические границы", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(8, 8, 7, 8);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 7, "Аварийные границы", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        cellAddresses = new CellRangeAddress(8, 9, 9, 9);
        sheet.addMergedRegion(cellAddresses);
        createStyledCell(row, 9, "Стат Агрегат", boldBorderCenterStyle);
        addAllBorders(cellAddresses, sheet);

        List<ModeControlModel> data = bean.getData(objType, structID, filterType, filter, paramID, user);
        int index = 10;
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
