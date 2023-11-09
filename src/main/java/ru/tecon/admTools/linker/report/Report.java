package ru.tecon.admTools.linker.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

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

        // Создание заголовка
        // Стиль для заголовка
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 16);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Заголовок
        SXSSFRow row = sheet.createRow(1);
        SXSSFCell cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Отчет по линковке " + reportData.getHeader());
        cell.setCellStyle(cellStyle);

        // Объединяем заголовок в одну ячейку
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));



        // Создание таблицы с данными
        // Стиль для шапки таблицы
        font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14);

        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        // Шапка таблицы
        row = sheet.createRow(3);
        String[] header = {"№", "Имя параметра", "Сокращение", "Агрегат", "Объект автоматизации"};
        for (int i = 0; i < header.length; i++) {
            cell = row.createCell(i + 1, CellType.STRING);
            cell.setCellValue(header[i]);
            cell.setCellStyle(cellStyle);
        }

        // Стиль для данных таблицы
        font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);

        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // Данные таблицы
        for (int i = 4; i < reportData.getData().size() + 4; i++) {
            row = sheet.createRow(i);

            ReportRow reportRow = reportData.getData().get(i - 4);
            String[] rowData = {String.valueOf(i - 3), reportRow.getParamName(), reportRow.getParamMemo(), reportRow.getStatAggregate(), reportRow.getOpcItemName()};
            for (int j = 0; j < rowData.length; j++) {
                cell = row.createCell(j + 1, j == 0 ? CellType.NUMERIC : CellType.STRING);
                cell.setCellValue(rowData[j]);
                cell.setCellStyle(cellStyle);
            }

            if (i == reportData.getData().size() + 3) {
                RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(i, i, 1, 5), sheet);
            }
        }
    }

    public Workbook getWorkbook() {
        return workbook;
    }
}
