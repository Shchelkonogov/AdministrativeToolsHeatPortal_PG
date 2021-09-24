package ru.tecon.admTools.dataAnalysis.report.ejb;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import ru.tecon.admTools.dataAnalysis.report.BorderUtil;
import ru.tecon.admTools.dataAnalysis.report.model.CellValueModel;
import ru.tecon.admTools.dataAnalysis.report.model.HeatSystem;
import ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Stateless bean для создания отчета по Анализ достоверности
 */
@Stateless
@LocalBean
public class DataAnalysisReportSB {

    private static final Logger LOGGER = Logger.getLogger(DataAnalysisReportSB.class.getName());

    private static final String SELECT_SUMMARY_DATA = "select * from table(ul_report.sel_month_crit(?, ?, ?, ?, to_date(?, 'dd.mm.yyyy'), ?))";
    private static final String SELECT_DAY_DATA = "select * from table(ul_report.sel_day_crit(?, ?, ?, ?, to_date(?, 'dd.mm.yyyy'), ?))";
    private static final String SELECT_ODPU_DATA = "select * from table(ul_report.sel_svod_crit(?, ?, ?, ?, to_date(?, 'dd.mm.yyyy')))";
    private static final String SELECT_CRITERION_NAME = "select alt_crit_name from dz_quality_criteria where crit_id = ?";
    private static final String SELECT_COUNTERS_DATA = "select * from table(ul_report.sel_counters(?, ?, ?, ?))";

    private static final List<String> TABLE_HEADER_NAMES = new ArrayList<>(Arrays.asList("№", "Объект"));

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Метод строит excel sheet Сводные данные
     * @param model данные для отчета
     * @param sheet страница
     * @param styles набор стилей
     * @return null используется для ожидания выполнения
     */
    @Asynchronous
    public Future<Void> createSummarySheet(ReportRequestModel model, SXSSFSheet sheet, Map<String, CellStyle> styles) {
        if (model.getProblemID().isEmpty() || (model.getUser() == null)) {
            return null;
        }

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_SUMMARY_DATA);
             PreparedStatement stmCountersData = connect.prepareStatement(SELECT_COUNTERS_DATA);
             PreparedStatement stmCriterion = connect.prepareStatement(SELECT_CRITERION_NAME)) {
            sheet.setColumnWidth(0, 2 * 256);

            List<HeatSystem> heatSystems = new ArrayList<>(Arrays.asList(HeatSystem.values()));
            heatSystems.removeIf(heatSystem -> heatSystem.getSelectName() == null);

            for (int i = 0; i < TABLE_HEADER_NAMES.size() + heatSystems.size(); i++) {
                sheet.trackColumnForAutoSizing(i + 1);
            }

            LocalDate startDate = model.getFirstDateAtMonth();
            LocalDate endDate = Stream.of(YearMonth.from(startDate).atEndOfMonth(), LocalDate.now()).min(LocalDate::compareTo).get();

            // Строка заголовка
            Row row = sheet.createRow(1);
            Cell cell = row.createCell(1);
            cell.setCellValue("Анализ достоверности измерений за " +
                    Month.of(model.getFirstDateAtMonth().getMonthValue())
                            .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))
                            .toLowerCase() +
                    " " + model.getFirstDateAtMonth().getYear());
            cell.setCellStyle(styles.get("header"));
            CellRangeAddress cellAddresses = new CellRangeAddress(1, 1, 1, 20);
            sheet.addMergedRegion(cellAddresses);

            // Строка названий проблем
            row = sheet.createRow(3);
            cell = row.createCell(3);
            cell.setCellValue("Тип / № счетчика");
            cell.setCellStyle(styles.get("tableHeader"));
            cellAddresses = new CellRangeAddress(3, 3, 3, TABLE_HEADER_NAMES.size() + heatSystems.size());
            sheet.addMergedRegion(cellAddresses);
            BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
            BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
            BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
            BorderUtil.createBorder("left", BorderStyle.THIN, cellAddresses, sheet);

            Map<Integer, String> criterion = new HashMap<>();
            for (int j = 0; j < endDate.getDayOfMonth(); j++) {
                for (int i = 0; i < model.getProblemID().size(); i++) {
                    if (!criterion.containsKey(model.getProblemID(i))) {
                        stmCriterion.setInt(1, model.getProblemID(i));
                        ResultSet res = stmCriterion.executeQuery();
                        if (res.next()) {
                            criterion.put(model.getProblemID(i), res.getString(1));
                        }
                    }
                    String name = criterion.get(model.getProblemID(i));

                    cell = row.createCell(i + TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + j * model.getProblemID().size());
                    cell.setCellValue(name);
                    cell.setCellStyle(styles.get("rotate"));
                }

                cellAddresses = new CellRangeAddress(3, 3, TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + j * model.getProblemID().size(), TABLE_HEADER_NAMES.size() + heatSystems.size() + (j + 1) * model.getProblemID().size());
                BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                BorderUtil.createBorder("left", BorderStyle.THIN, cellAddresses, sheet);
            }

            // Строка заголовка таблицы
            row = sheet.createRow(4);

            for (int i = 0; i < TABLE_HEADER_NAMES.size(); i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(TABLE_HEADER_NAMES.get(i));
                cell.setCellStyle(styles.get("tableHeader"));
            }

            for (int i = 0; i < heatSystems.size(); i++) {
                cell = row.createCell(i + TABLE_HEADER_NAMES.size() + 1);
                cell.setCellValue(HeatSystem.values()[i].getName());
                cell.setCellStyle(styles.get("tableHeader"));
            }

            int index = 0;
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                int position = TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + index * model.getProblemID().size();
                cell = row.createCell(position);
                cell.setCellValue(date.format(ReportRequestModel.FORMATTER));
                cell.setCellStyle(styles.get("tableHeader"));
                if (model.getProblemID().size() > 1) {
                    cellAddresses = new CellRangeAddress(4, 4, position, position + model.getProblemID().size() - 1);
                    sheet.addMergedRegion(cellAddresses);
                    BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("left", BorderStyle.THIN, cellAddresses, sheet);
                }
                index++;
            }

            // Создаем основные данные
            TreeMap<Integer, List<Integer>> ctpWithUuIndexes = new TreeMap<>();
            TreeMap<Integer, List<CellValueModel>> cellData = new TreeMap<>();
            int ctpIndex = 1;
            int uuIndex = 1;
            int ctpRowIndex = 0;
            for (int i = 0, rowIndex = 5; i < model.getProblemID().size(); i++, rowIndex = 5) {
                stm.setString(1, model.getObjectID());
                stm.setInt(2, model.getFilterID());
                stm.setString(3, model.getFilterValue());
                stm.setString(4, model.getUser());
                stm.setString(5, model.getFirstDateAtMonthValue());
                stm.setInt(6, model.getProblemID(i));

                ResultSet res = stm.executeQuery();
                while (res.next()) {
                    if (!cellData.containsKey(rowIndex)) {
                        cellData.put(rowIndex, new ArrayList<>());
                    }

                    if (i == 0) {
                        if (res.getInt("obj_type") == 1) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 1, String.valueOf(ctpIndex), "centerBold"));
                            ctpRowIndex = rowIndex;
                            ctpWithUuIndexes.put(ctpRowIndex, new ArrayList<>());
                            ctpIndex++;
                            uuIndex = 1;
                        } else {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 1, String.valueOf(uuIndex), "left"));
                            ctpWithUuIndexes.get(ctpRowIndex).add(rowIndex);
                            uuIndex++;
                        }

                        if (res.getInt("obj_type") == 1) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 2, res.getString("obj_name"), "centerBold"));
                        } else {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 2, res.getString("obj_name"), "left"));
                        }
                    }

                    for (int j = 0; j < endDate.getDayOfMonth(); j++) {
                        if (res.getString("p" + (j + 1)) != null) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + i + j * model.getProblemID().size(), res.getString("p" + (j + 1)), "centerWrap"));
                        }
                    }

                    rowIndex++;
                }
            }

            stmCountersData.setString(1, model.getObjectID());
            stmCountersData.setInt(2, model.getFilterID());
            stmCountersData.setString(3, model.getFilterValue());
            stmCountersData.setString(4, model.getUser());

            ResultSet res = stmCountersData.executeQuery();
            int rowIndex = 5;
            while (res.next()) {
                for (int j = 0; j < heatSystems.size(); j++) {
                    if (res.getString(heatSystems.get(j).getSelectName()) != null) {
                        cellData.get(rowIndex).add(new CellValueModel(rowIndex, j + TABLE_HEADER_NAMES.size() + 1, res.getString(heatSystems.get(j).getSelectName()), "center"));
                    }
                }
                rowIndex++;
            }

            // Удаление кустов в которых нет проблем
            cellData.forEach((integer, cellValueModels) -> Collections.sort(cellValueModels));

            List<Integer> removeCtpIndexes = new ArrayList<>();

            outer: for (Map.Entry<Integer, List<Integer>> ctpEntry: ctpWithUuIndexes.entrySet()) {
                if (cellData.get(ctpEntry.getKey()).stream()
                        .reduce((left, right) -> right)
                        .orElseThrow(NullPointerException::new)
                        .getColumn() <= (TABLE_HEADER_NAMES.size() + heatSystems.size())) {
                    for (Integer uuRowIndex: ctpEntry.getValue()) {
                        if (cellData.get(uuRowIndex).stream()
                                .reduce((left, right) -> right)
                                .orElseThrow(NullPointerException::new)
                                .getColumn() > (TABLE_HEADER_NAMES.size() + heatSystems.size())) {
                            continue outer;
                        }
                    }

                    removeCtpIndexes.add(ctpEntry.getKey());
                    removeCtpIndexes.addAll(ctpEntry.getValue());
                }
            }

            cellData.keySet().removeAll(removeCtpIndexes);

            rowIndex = 5;
            for (List<CellValueModel> rowData: cellData.values()) {
                for (CellValueModel celItem: rowData) {
                    celItem.setRow(rowIndex);
                }
                rowIndex++;
            }

            ctpIndex = 1;
            for (Integer ctpPosition: ctpWithUuIndexes.keySet()) {
                if (cellData.containsKey(ctpPosition)) {
                    cellData.get(ctpPosition).get(0).setValue(String.valueOf(ctpIndex));
                    ctpIndex++;
                }
            }

            // Отрисовываем данные
            for (List<CellValueModel> rowData: cellData.values()) {
                int rowindex = rowData.get(0).getRow();
                row = sheet.getRow(rowindex);
                if (row == null) {
                    row = sheet.createRow(rowindex);
                }

                for (CellValueModel cellValueModel: rowData) {
                    cell = row.createCell(cellValueModel.getColumn());
                    cell.setCellValue(cellValueModel.getValue());
                    cell.setCellStyle(styles.get(cellValueModel.getStyleName()));
                }

                if (rowData.get(0).getStyleName().equals("centerBold")) {
                    cellAddresses = new CellRangeAddress(rowindex, rowindex, 1, TABLE_HEADER_NAMES.size() + heatSystems.size() + model.getProblemID().size() * endDate.getDayOfMonth());
                    BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                }

                for (int i = 0; i < heatSystems.size(); i++) {
                    cellAddresses = new CellRangeAddress(rowindex, rowindex, TABLE_HEADER_NAMES.size() + 1 + i, TABLE_HEADER_NAMES.size() + 1 + i);
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                }

                for (int i = 0; i < endDate.getDayOfMonth(); i++) {
                    cellAddresses = new CellRangeAddress(rowindex, rowindex, TABLE_HEADER_NAMES.size() + heatSystems.size() + (i + 1) * model.getProblemID().size(), TABLE_HEADER_NAMES.size() + heatSystems.size() + (i + 1) * model.getProblemID().size());
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                }
            }

            if (!cellData.isEmpty()) {
                int lastRowIndex = cellData.lastEntry().getValue().get(0).getRow();
                cellAddresses = new CellRangeAddress(lastRowIndex, lastRowIndex, 1, TABLE_HEADER_NAMES.size() + heatSystems.size() + model.getProblemID().size() * endDate.getDayOfMonth());
                BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
            }


            // Устанавливаем размер колонок
            for (int i = 0; i < TABLE_HEADER_NAMES.size() + heatSystems.size(); i++) {
                sheet.autoSizeColumn(i + 1);
            }

            if (!model.getProblemID().isEmpty()) {
                int minMergedWidth = 15 * 256;
                int minWidth = 6 * 256;
                int width = minMergedWidth / model.getProblemID().size() + 1;

                for (int i = TABLE_HEADER_NAMES.size() + heatSystems.size() + 1; i < TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + endDate.getDayOfMonth() * model.getProblemID().size(); i++) {
                    if (width < minWidth) {
                        sheet.setColumnWidth(i, minWidth);
                    } else {
                        sheet.setColumnWidth(i, width);
                    }
                }
            }

            sheet.createFreezePane(3, 5);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error load summary sheet data", e);
        }
        return null;
    }

    /**
     * Метод строит excel sheet анализ достоверности измерений за сутки
     * @param date дата в формате dd.MM.yyyy
     * @param model данные для отчета
     * @param sheet страница
     * @param styles набор стилей
     * @return null используется для ожидания выполнения
     */
    @Asynchronous
    public Future<Void> createDaySheet(String date, ReportRequestModel model, SXSSFSheet sheet, Map<String, CellStyle> styles) {
        if (model.getProblemID().isEmpty() || (model.getUser() == null)) {
            return null;
        }

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_DAY_DATA);
             PreparedStatement stmCriterion = connect.prepareStatement(SELECT_CRITERION_NAME)) {
            sheet.setColumnWidth(0, 2 * 256);

            for (int i = 0; i < TABLE_HEADER_NAMES.size(); i++) {
                sheet.trackColumnForAutoSizing(i + 1);
            }

            // Строка заголовка
            Row row = sheet.createRow(1);
            Cell cell = row.createCell(1);
            cell.setCellValue("Анализ достоверности измерений за " + date);
            cell.setCellStyle(styles.get("header"));
            CellRangeAddress cellAddresses = new CellRangeAddress(1, 1, 1, 20);
            sheet.addMergedRegion(cellAddresses);

            // Строка с перечислением проблем
            row = sheet.createRow(3);
            for (int i = 0; i < model.getProblemID().size(); i++) {
                stmCriterion.setInt(1, model.getProblemID(i));
                ResultSet res = stmCriterion.executeQuery();

                cell = row.createCell(i + 3 + i * (HeatSystem.values().length - 1));
                if (res.next()) {
                    cell.setCellValue(res.getString(1));
                }
                cell.setCellStyle(styles.get("tableHeader"));
                cellAddresses = new CellRangeAddress(3, 3 , i + 3 + i * (HeatSystem.values().length - 1), i + 3 + (i + 1) * (HeatSystem.values().length - 1));
                BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                BorderUtil.createBorder("left", BorderStyle.THIN, cellAddresses, sheet);
                sheet.addMergedRegion(cellAddresses);
            }

            // Шапка таблицы
            row = sheet.createRow(4);

            for (int i = 0; i < TABLE_HEADER_NAMES.size(); i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(TABLE_HEADER_NAMES.get(i));
                cell.setCellStyle(styles.get("tableHeader"));
            }

            for (int i = 0; i < model.getProblemID().size(); i++) {
                for (int j = 0; j < HeatSystem.values().length; j++) {
                    cell = row.createCell(i + TABLE_HEADER_NAMES.size() + 1 + j + i * (HeatSystem.values().length - 1));
                    cell.setCellValue(HeatSystem.values()[j].getName());
                    cell.setCellStyle(styles.get("tableHeader"));
                }
            }

            // Создаем основные данные
            TreeMap<Integer, List<Integer>> ctpWithUuIndexes = new TreeMap<>();
            TreeMap<Integer, List<CellValueModel>> cellData = new TreeMap<>();
            int ctpIndex = 1;
            int uuIndex = 1;
            int ctpRowIndex = 0;
            // Загружаем данные
            for (int i = 0, rowIndex = 5; i < model.getProblemID().size(); i++, rowIndex = 5) {
                stm.setString(1, model.getObjectID());
                stm.setInt(2, model.getFilterID());
                stm.setString(3, model.getFilterValue());
                stm.setString(4, model.getUser());
                stm.setString(5, date);
                stm.setInt(6, model.getProblemID(i));

                ResultSet res = stm.executeQuery();
                while (res.next()) {
                    if (!cellData.containsKey(rowIndex)) {
                        cellData.put(rowIndex, new ArrayList<>());
                    }

                    if (i == 0) {
                        if (res.getInt("obj_type") == 1) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 1, String.valueOf(ctpIndex), "centerBold"));
                            ctpRowIndex = rowIndex;
                            ctpWithUuIndexes.put(ctpRowIndex, new ArrayList<>());
                            ctpIndex++;
                            uuIndex = 1;
                        } else {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 1, String.valueOf(uuIndex), "left"));
                            ctpWithUuIndexes.get(ctpRowIndex).add(rowIndex);
                            uuIndex++;
                        }

                        if (res.getInt("obj_type") == 1) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 2, res.getString("obj_name"), "centerBold"));
                        } else {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, 2, res.getString("obj_name"), "left"));
                        }
                    }

                    for (int j = 0; j < HeatSystem.values().length; j++) {
                        if (res.getString(HeatSystem.values()[j].name()) != null) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, TABLE_HEADER_NAMES.size() + 1 + j + i * HeatSystem.values().length, res.getString(HeatSystem.values()[j].name()), "centerWrap"));
                        }
                    }

                    rowIndex++;
                }
            }

            // Удаление кустов в которых нет проблем
            List<Integer> removeCtpIndexes = new ArrayList<>();

            outer: for (Map.Entry<Integer, List<Integer>> ctpEntry: ctpWithUuIndexes.entrySet()) {
                if (cellData.get(ctpEntry.getKey()).size() <= TABLE_HEADER_NAMES.size()) {
                    for (Integer uuRowIndex: ctpEntry.getValue()) {
                        if (cellData.get(uuRowIndex).size() > TABLE_HEADER_NAMES.size()) {
                            continue outer;
                        }
                    }

                    removeCtpIndexes.add(ctpEntry.getKey());
                    removeCtpIndexes.addAll(ctpEntry.getValue());
                }
            }

            cellData.keySet().removeAll(removeCtpIndexes);

            int rowIndex = 5;
            for (List<CellValueModel> rowData: cellData.values()) {
                for (CellValueModel celItem: rowData) {
                    celItem.setRow(rowIndex);
                }
                rowIndex++;
            }

            ctpIndex = 1;
            for (Integer ctpPosition: ctpWithUuIndexes.keySet()) {
                if (cellData.containsKey(ctpPosition)) {
                    cellData.get(ctpPosition).get(0).setValue(String.valueOf(ctpIndex));
                    ctpIndex++;
                }
            }

            // Отрисовываем данные
            for (List<CellValueModel> rowData: cellData.values()) {
                int rowindex = rowData.get(0).getRow();
                row = sheet.getRow(rowindex);
                if (row == null) {
                    row = sheet.createRow(rowindex);
                }

                for (CellValueModel cellValueModel: rowData) {
                    cell = row.createCell(cellValueModel.getColumn());
                    cell.setCellValue(cellValueModel.getValue());
                    cell.setCellStyle(styles.get(cellValueModel.getStyleName()));
                }

                if (rowData.get(0).getStyleName().equals("centerBold")) {
                    cellAddresses = new CellRangeAddress(rowindex, rowindex, 1, TABLE_HEADER_NAMES.size() + HeatSystem.values().length * model.getProblemID().size());
                    BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                }
                for (int i = 0; i < model.getProblemID().size(); i++) {
                    cellAddresses = new CellRangeAddress(rowindex, rowindex, TABLE_HEADER_NAMES.size() + 1 + i + (i + 1) * (HeatSystem.values().length - 1), TABLE_HEADER_NAMES.size() + 1 + i + (i + 1) * (HeatSystem.values().length - 1));
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                }
            }

            if (!cellData.isEmpty()) {
                int lastRowIndex = cellData.lastEntry().getValue().get(0).getRow();
                cellAddresses = new CellRangeAddress(lastRowIndex, lastRowIndex, 1, TABLE_HEADER_NAMES.size() + HeatSystem.values().length * model.getProblemID().size());
                BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
            }

            // Устанавливаем размер колонок
            for (int i = 0; i < TABLE_HEADER_NAMES.size(); i++) {
                sheet.autoSizeColumn(i + 1);
            }

            for (int i = 0; i < model.getProblemID().size() * HeatSystem.values().length; i++) {
                sheet.setColumnWidth(i + 1 + TABLE_HEADER_NAMES.size(), 20 * 256);
            }

            sheet.createFreezePane(3, 5);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error load day sheet data", e);
        }
        return null;
    }

    /**
     * Метод строит excel sheet Анализ ОДПУ
     * @param model данные для отчета
     * @param sheet страница
     * @param styles набор стилей
     * @return null используется для ожидания выполнения
     */
    @Asynchronous
    public Future<Void> createODPUSheet(ReportRequestModel model, SXSSFSheet sheet, Map<String, CellStyle> styles) {
        if (model.getProblemOdpuID().isEmpty() || (model.getUser() == null)) {
            return null;
        }

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_ODPU_DATA);
             PreparedStatement stmCountersData = connect.prepareStatement(SELECT_COUNTERS_DATA);
             PreparedStatement stmCriterion = connect.prepareStatement(SELECT_CRITERION_NAME)) {
            sheet.setColumnWidth(0, 2 * 256);

            List<HeatSystem> heatSystems = new ArrayList<>(Arrays.asList(HeatSystem.values()));
            heatSystems.removeIf(heatSystem -> !heatSystem.isOdpu());
            heatSystems.removeIf(heatSystem -> heatSystem.getSelectName() == null);

            for (int i = 0; i < TABLE_HEADER_NAMES.size() + heatSystems.size() + heatSystems.size() * model.getProblemOdpuID().size(); i++) {
                sheet.trackColumnForAutoSizing(i + 1);
            }

            // Строка заголовка
            Row row = sheet.createRow(1);
            Cell cell = row.createCell(1);
            cell.setCellValue("Анализ достоверности измерений за " +
                    Month.of(model.getFirstDateAtMonth().getMonthValue())
                            .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))
                            .toLowerCase() +
                    " " + model.getFirstDateAtMonth().getYear());
            cell.setCellStyle(styles.get("header"));
            CellRangeAddress cellAddresses = new CellRangeAddress(1, 1, 1, 20);
            sheet.addMergedRegion(cellAddresses);

            // Строка названий проблем
            row = sheet.createRow(3);
            cell = row.createCell(3);
            cell.setCellValue("Тип / № счетчика");
            cell.setCellStyle(styles.get("tableHeader"));
            cellAddresses = new CellRangeAddress(3, 3, 3, TABLE_HEADER_NAMES.size() + heatSystems.size());
            sheet.addMergedRegion(cellAddresses);
            BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
            BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
            BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
            BorderUtil.createBorder("left", BorderStyle.THIN, cellAddresses, sheet);

            for (int i = 0; i < model.getProblemOdpuID().size(); i++) {
                stmCriterion.setInt(1, model.getProblemOdpuID(i));
                ResultSet res = stmCriterion.executeQuery();
                if (res.next()) {
                    cell = row.createCell(TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + i * heatSystems.size());
                    cell.setCellValue(res.getString(1));
                    cell.setCellStyle(styles.get("centerBoldWrap"));

                    cellAddresses = new CellRangeAddress(3, 3, TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + i * heatSystems.size(), TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + (i + 1) * heatSystems.size() - 1);
                    sheet.addMergedRegion(cellAddresses);
                    BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("left", BorderStyle.THIN, cellAddresses, sheet);
                }
            }

            // Строка заголовка таблицы
            row = sheet.createRow(4);

            for (int i = 0; i < TABLE_HEADER_NAMES.size(); i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(TABLE_HEADER_NAMES.get(i));
                cell.setCellStyle(styles.get("tableHeader"));
            }

            for (int i = 0; i < heatSystems.size(); i++) {
                cell = row.createCell(i + TABLE_HEADER_NAMES.size() + 1);
                cell.setCellValue(heatSystems.get(i).getName());
                cell.setCellStyle(styles.get("tableHeader"));
            }

            for (int i = 0; i < model.getProblemOdpuID().size(); i++) {
                for (int j = 0; j < heatSystems.size(); j++) {
                    cell = row.createCell(TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + i * heatSystems.size() + j);
                    cell.setCellValue(heatSystems.get(j).getName());
                    cell.setCellStyle(styles.get("tableHeader"));
                }
            }

            // Создаем основные данные
            TreeMap<Integer, List<Integer>> ctpWithUuIndexes = new TreeMap<>();
            TreeMap<Integer, List<CellValueModel>> cellData = new TreeMap<>();
            int ctpIndex = 1;
            int uuIndex = 1;
            int rowIndex = 5;
            int ctpRowIndex = 0;

            stm.setString(1, model.getObjectID());
            stm.setInt(2, model.getFilterID());
            stm.setString(3, model.getFilterValue());
            stm.setString(4, model.getUser());
            stm.setString(5, model.getFirstDateAtMonthValue());

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                if (!cellData.containsKey(rowIndex)) {
                    cellData.put(rowIndex, new ArrayList<>());
                }

                if (res.getInt("obj_type") == 1) {
                    cellData.get(rowIndex).add(new CellValueModel(rowIndex, 1, String.valueOf(ctpIndex), "centerBold"));
                    ctpRowIndex = rowIndex;
                    ctpWithUuIndexes.put(ctpRowIndex, new ArrayList<>());
                    ctpIndex++;
                    uuIndex = 1;
                } else {
                    cellData.get(rowIndex).add(new CellValueModel(rowIndex, 1, String.valueOf(uuIndex), "left"));
                    ctpWithUuIndexes.get(ctpRowIndex).add(rowIndex);
                    uuIndex++;
                }

                if (res.getInt("obj_type") == 1) {
                    cellData.get(rowIndex).add(new CellValueModel(rowIndex, 2, res.getString("obj_name"), "centerBold"));
                } else {
                    cellData.get(rowIndex).add(new CellValueModel(rowIndex, 2, res.getString("obj_name"), "left"));
                }

                for (int i = 0; i < model.getProblemOdpuID().size(); i++) {
                    for (int j = 0; j < heatSystems.size(); j++) {
                        if (res.getString("v" + model.getProblemOdpuID(i) + heatSystems.get(j).name()) != null) {
                            cellData.get(rowIndex).add(new CellValueModel(rowIndex, TABLE_HEADER_NAMES.size() + heatSystems.size() + 1 + j + i * heatSystems.size(), res.getString("v" + model.getProblemOdpuID(i) + heatSystems.get(j).name()), "center"));
                        }
                    }
                }

                rowIndex++;
            }

            stmCountersData.setString(1, model.getObjectID());
            stmCountersData.setInt(2, model.getFilterID());
            stmCountersData.setString(3, model.getFilterValue());
            stmCountersData.setString(4, model.getUser());

            res = stmCountersData.executeQuery();
            rowIndex = 5;
            while (res.next()) {
                for (int j = 0; j < heatSystems.size(); j++) {
                    if (res.getString(heatSystems.get(j).getSelectName()) != null) {
                        cellData.get(rowIndex).add(new CellValueModel(rowIndex, j + TABLE_HEADER_NAMES.size() + 1, res.getString(heatSystems.get(j).getSelectName()), "center"));
                    }
                }
                rowIndex++;
            }

            // Удаление кустов в которых нет проблем
            cellData.forEach((integer, cellValueModels) -> Collections.sort(cellValueModels));

            List<Integer> removeCtpIndexes = new ArrayList<>();

            outer: for (Map.Entry<Integer, List<Integer>> ctpEntry: ctpWithUuIndexes.entrySet()) {
                if (cellData.get(ctpEntry.getKey()).stream()
                        .reduce((left, right) -> right)
                        .orElseThrow(NullPointerException::new)
                        .getColumn() <= (TABLE_HEADER_NAMES.size() + heatSystems.size())) {
                    for (Integer uuRowIndex: ctpEntry.getValue()) {
                        if (cellData.get(uuRowIndex).stream()
                                .reduce((left, right) -> right)
                                .orElseThrow(NullPointerException::new)
                                .getColumn() > (TABLE_HEADER_NAMES.size() + heatSystems.size())) {
                            continue outer;
                        }
                    }

                    removeCtpIndexes.add(ctpEntry.getKey());
                    removeCtpIndexes.addAll(ctpEntry.getValue());
                }
            }

            cellData.keySet().removeAll(removeCtpIndexes);

            rowIndex = 5;
            for (List<CellValueModel> rowData: cellData.values()) {
                for (CellValueModel celItem: rowData) {
                    celItem.setRow(rowIndex);
                }
                rowIndex++;
            }

            ctpIndex = 1;
            for (Integer ctpPosition: ctpWithUuIndexes.keySet()) {
                if (cellData.containsKey(ctpPosition)) {
                    cellData.get(ctpPosition).get(0).setValue(String.valueOf(ctpIndex));
                    ctpIndex++;
                }
            }

            // Отрисовываем данные
            for (List<CellValueModel> rowData: cellData.values()) {
                int index = rowData.get(0).getRow();
                row = sheet.getRow(index);
                if (row == null) {
                    row = sheet.createRow(index);
                }

                for (CellValueModel cellValueModel: rowData) {
                    cell = row.createCell(cellValueModel.getColumn());
                    cell.setCellValue(cellValueModel.getValue());
                    cell.setCellStyle(styles.get(cellValueModel.getStyleName()));
                }

                if (rowData.get(0).getStyleName().equals("centerBold")) {
                    cellAddresses = new CellRangeAddress(index, index, 1, TABLE_HEADER_NAMES.size() + heatSystems.size() + heatSystems.size() * model.getProblemOdpuID().size());
                    BorderUtil.createBorder("top", BorderStyle.THIN, cellAddresses, sheet);
                    BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
                }

                for (int i = 0; i < heatSystems.size(); i++) {
                    cellAddresses = new CellRangeAddress(index, index, TABLE_HEADER_NAMES.size() + 1 + i, TABLE_HEADER_NAMES.size() + 1 + i);
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                }

                for (int i = 0; i < model.getProblemOdpuID().size(); i++) {
                    cellAddresses = new CellRangeAddress(index, index, TABLE_HEADER_NAMES.size() + heatSystems.size() + (i + 1) * heatSystems.size(), TABLE_HEADER_NAMES.size() + heatSystems.size() + (i + 1) * heatSystems.size());
                    BorderUtil.createBorder("right", BorderStyle.THIN, cellAddresses, sheet);
                }
            }

            if (!cellData.isEmpty()) {
                int lastRowIndex = cellData.lastEntry().getValue().get(0).getRow();
                cellAddresses = new CellRangeAddress(lastRowIndex, lastRowIndex, 1, TABLE_HEADER_NAMES.size() + heatSystems.size() + heatSystems.size() * model.getProblemOdpuID().size());
                BorderUtil.createBorder("bottom", BorderStyle.THIN, cellAddresses, sheet);
            }

            // Устанавливаем размер колонок
            for (int i = 0; i < TABLE_HEADER_NAMES.size() + heatSystems.size() + heatSystems.size() * model.getProblemOdpuID().size(); i++) {
                sheet.autoSizeColumn(i + 1);
            }

            sheet.createFreezePane(3, 5);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error load summary sheet data", e);
        }
        return null;
    }
}
