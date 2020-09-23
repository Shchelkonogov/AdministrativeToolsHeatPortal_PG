package ru.tecon.admTools.specificModel.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import ru.tecon.admTools.specificModel.report.ejb.ModeMapLocal;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс формирует отчет "Режимная карта"
 */
public final class ModeMap {

    private static final Pattern PATTERN = Pattern.compile(".*(?<value>\\{\\w*})");
    private static final Pattern PATTERN_ARRAY = Pattern.compile("^\\[(?<value>\\w*/\\d)]");
    private static final Pattern PATTERN_ARRAY_VALUE = Pattern.compile(".*(?<value>\\{\\w*\\[]})");

    /**
     * Метод формирует excel файл отчета "Режимная карта"
     * @param objectID id объекта
     * @param bean класс для загрузки данных {@link ModeMapLocal}
     * @return возвращает excel {@link Workbook}
     * @throws IOException в случае проблем с файлом шаблона
     */
    public static Workbook generateModeMap(int objectID, ModeMapLocal bean) throws IOException {
        try (InputStream in = ModeMap.class.getResourceAsStream("/templates/specificModel/excel/modeMapTemplate.xlsx")) {
            Workbook workbook = XSSFWorkbookFactory.create(in);

            Map<String, String> singleData = bean.loadSingleData(objectID);

            Map<String, List<Map<String, String>>> arrayData = bean.loadArrayData(objectID);

            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Row> arrayRows = new HashMap<>();

            for (Row row: sheet) {
                for (Cell cell: row) {
                    String value = cell.getStringCellValue();
                    changeValue(cell, singleData, PATTERN);

                    Matcher m = PATTERN_ARRAY.matcher(value);
                    if (m.find()) {
                        arrayRows.put(m.group("value"), row);
                        cell.setCellValue("");
                    }
                }
            }

            for (Map.Entry<String, Row> entry: arrayRows.entrySet()) {
                int index = entry.getValue().getRowNum();
                String key = entry.getKey().split("/")[0];
                int count = Integer.parseInt(entry.getKey().split("/")[1]);

                if (arrayData.containsKey(key)) {
                    for (Map<String, String> map: arrayData.get(key)) {

                        for (int i = 0; i < count; i++) {
                            moveRow(workbook, sheet, index + i, index + 2 * i);
                        }

                        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
                            for (int j = 0; j < count; j++) {
                                if (cellRangeAddress.getFirstRow() == index + count + j) {
                                    CellRangeAddress newCellRangeAddress = new CellRangeAddress(index + j,
                                            (index + j + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                                            cellRangeAddress.getFirstColumn(),
                                            cellRangeAddress.getLastColumn());
                                    sheet.addMergedRegion(newCellRangeAddress);
                                }
                            }
                        }

                        for (int i = 0; i < count; i++) {
                            for (Cell cell: sheet.getRow(index + i)) {
                                changeValue(cell, map, PATTERN_ARRAY_VALUE);
                            }
                        }

                        index += count;
                    }
                }

                for (int i = 0; i < count; i++) {
                    removeRow(sheet, index);
                }
            }
            return workbook;
        }
    }

    private static void changeValue(Cell cell, Map<String, String> map, Pattern pattern) {
        String value = cell.getStringCellValue();
        Matcher m = pattern.matcher(value);
        if (m.find()) {
            cell.setCellValue(value.replace(m.group("value"), map.getOrDefault(m.group("value"), "")));
        }
    }

    /**
     * Метод удаляет строку на странице
     * @param sheet страница
     * @param rowIndex индекс строки
     */
    private static void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();

        if ((rowIndex >= 0) && (rowIndex < lastRowNum)){
            for (CellRangeAddress cellAddresses: sheet.getMergedRegions()){
                if (cellAddresses.getFirstRow() == rowIndex) {
                    sheet.removeMergedRegion(sheet.getMergedRegions().indexOf(cellAddresses));
                }
            }
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }

        if (rowIndex == lastRowNum){
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null){
                sheet.removeRow(removingRow);
            }
        }
    }

    private static void moveRow(Workbook workbook, Sheet sheet, int destinationRowNum, int sourceRowNum) {
        Row sourceRow = sheet.getRow(sourceRowNum);

        sheet.shiftRows(destinationRowNum, sheet.getLastRowNum(), 1);

        Row newRow = sheet.createRow(destinationRowNum);

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);

            if (oldCell == null) {
                continue;
            }

            Cell newCell = newRow.createCell(i);

            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            if (oldCell.getCellType() == CellType.STRING) {
                newCell.setCellValue(oldCell.getStringCellValue());
            }
        }
    }
}
