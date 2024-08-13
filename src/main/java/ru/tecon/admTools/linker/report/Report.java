package ru.tecon.admTools.linker.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maksim Shchelkonogov
 * 09.11.2023
 */
public class Report {

    private final ReportData reportData;
    private SXSSFWorkbook workbook;

    public Report(ReportData reportData) {
        this.reportData = reportData;
    }

    public void create() {
        workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = workbook.createSheet("Отчет по линковке");

        // Устанавливаем ширину колонок
        int[] columnWidth = {3, 5, 70, 20, 70, 80};
        for (int i = 0; i < columnWidth.length; i++) {
            sheet.setColumnWidth(i, columnWidth[i] * 256);
        }

        // Стили
        Map<String, CellStyle> styles = createStyles(workbook);

        // Заголовок
        createStyledCell(sheet.createRow(1), 1, CellType.STRING,
                "ПАО \"МОЭК\": АС \"ТЕКОН - Диспетчеризация\"", styles.get("headerBoldStyle"));
        createStyledCell(sheet.createRow(2), 1, CellType.STRING,
                "Отчет по линковке " + reportData.getHeader(), styles.get("headerBoldStyle"));
        for (int i = 1; i <= 2; i++) {
            sheet.addMergedRegion(new CellRangeAddress(i, i, 1, 5));
        }

        // Подпись
        String sing = "Отчет сформирован " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        createStyledCell(sheet.createRow(4), 1, CellType.STRING, sing, styles.get("style"));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 2));

        // Создание таблицы с данными
        // Шапка таблицы
        Row row = sheet.createRow(6);
        String[] header = {"№", "Имя параметра", "Сокращение", "Агрегат", "Объект автоматизации"};
        for (int i = 0; i < header.length; i++) {
            createStyledCell(row, i + 1, CellType.STRING, header[i], styles.get("boldBorderCenterStyle"));
        }

        // Данные таблицы
        for (int i = 7; i < reportData.getData().size() + 4; i++) {
            row = sheet.createRow(i);

            ReportRow reportRow = reportData.getData().get(i - 4);
            String[] rowData = {String.valueOf(i - 3), reportRow.getParamName(), reportRow.getParamMemo(), reportRow.getStatAggregate(), reportRow.getOpcItemName()};
            for (int j = 0; j < rowData.length; j++) {
                CellType cellType = j == 0 ? CellType.NUMERIC : CellType.STRING;
                createStyledCell(row, j + 1, cellType, rowData[j], styles.get("borderStyle"));
            }
        }
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    private void createStyledCell(Row row, int index, CellType cellType, String value, CellStyle style) {
        Cell cell = row.createCell(index, cellType);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> result = new HashMap<>();

        Font font;
        CellStyle style;


        // headerBoldStyle
        font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setFontName("Times New Roman");

        style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        result.put("headerBoldStyle", style);


        // headerStyle
        font = wb.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setFontName("Times New Roman");

        style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        result.put("headerStyle", style);


        // boldBorderCenterStyle
        font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Times New Roman");

        style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);

        result.put("boldBorderCenterStyle", style);


        // style borderStyle borderCenterStyle
        font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");

        style = wb.createCellStyle();
        style.setFont(font);

        result.put("style", style);

        style = wb.createCellStyle();
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        result.put("borderStyle", style);

        style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        result.put("borderCenterStyle", style);

        return result;
    }
}
