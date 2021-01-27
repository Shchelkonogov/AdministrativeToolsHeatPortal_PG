package ru.tecon.admTools.matrixProblems.report.ejb;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import ru.tecon.admTools.matrixProblems.report.model.ReportRequestModel;
import ru.tecon.admTools.matrixProblems.report.model.tSheetType;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Stateless bean для формирования отчета по матрице проблем
 */
@Stateless
@LocalBean
public class ReportBean {

    private static final Logger LOGGER = Logger.getLogger(ReportBean.class.getName());

    private static final String ALTER_SQL = "alter session set NLS_NUMERIC_CHARACTERS = '.,'";

    private static final String SELECT_T_DATA_CO = "select * from table(ul_report.sel_co_t(?, ?, ?, 'ADMIN', ?, to_date(?, 'dd.mm.yyyy')))";
    private static final String SELECT_T_DATA_GVS = "select * from table(ul_report.sel_gvs_t(?, ?, ?, 'ADMIN', ?, to_date(?, 'dd.mm.yyyy')))";
    private static final String SELECT_V_DATA = "select * from table(ul_report.sel_gvs_v(?, ?, ?, 'ADMIN', ?, to_date(?, 'dd.mm.yyyy')))";
    private static final String SELECT_G_DATA = "select * from table(ul_report.SEL_co_g(?, ?, ?, 'ADMIN', ?, to_date(?, 'dd.mm.yyyy')))";
    private static final String SELECT_Q_DATA_CO = "select * from table(ul_report.sel_co_qc(?, ?, ?, 'ADMIN', to_date(?, 'dd.mm.yyyy')))";
    private static final String SELECT_Q_DATA_GVS = "select * from table(ul_report.sel_gvs_qgvs(?, ?, ?, 'ADMIN', to_date(?, 'dd.mm.yyyy')))";

    private static final String SELECT_TNV = "select tnv from table(ul_report.get_tnv(?, to_date(?, 'dd.mm.yyyy'))) order by time_stamp";

    private static final String SELECT_RATIO = "select dt, dto, dgv, dgvo from dz_norm_koef where obj_type_id = 1";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Асинхронный метод для создания excel страницы (модицикация переданной в виде параметра)
     * @param requestModel модель данных для отчета
     * @param sheetType тип строищейся страницы
     * @param sheet excel страница
     * @param rowStyleCtp стиль ячейки цтп
     * @param rowStyleCtpError стиль ячейки цтп с ошибочными данными
     * @param rowStyleSum стиль ячейки сумма, итог
     * @param rowStyleSumError стиль ячейки сумма, итог с ошибочными данными
     * @param headerStyle стиль заголовка
     * @param errorStyle стиль ошибочных данных
     * @return ничего не возвращет (используется для ожидания выполнения)
     */
    @Asynchronous
    public Future<Void> createSheetT(ReportRequestModel requestModel, tSheetType sheetType, SXSSFSheet sheet,
                                     CellStyle rowStyleCtp, CellStyle rowStyleCtpError,
                                     CellStyle rowStyleSum, CellStyle rowStyleSumError,
                                     CellStyle headerStyle, CellStyle errorStyle) {
        // Переменные для определения времени выполнения
        long startTime = System.currentTimeMillis();
        long stopTime;

        sheet.trackAllColumnsForAutoSizing();

        Map<Integer, List<String>> tnvMap = new HashMap<>();

        LocalDate date = requestModel.getFirstDateAtMonth();
        int daysCount = requestModel.getFirstDateAtMonth().lengthOfMonth();

        // Выбираем нужный select
        String select = null;
        switch (sheetType) {
            case T3:
            case T4:
                select = SELECT_T_DATA_CO;
                break;
            case T7:
            case T13:
                select = SELECT_T_DATA_GVS;
                break;
            case V7:
            case V13:
                select = SELECT_V_DATA;
                break;
            case G3:
            case G4:
                select = SELECT_G_DATA;
                break;
            case Qco:
                select = SELECT_Q_DATA_CO;
                break;
            case Qgvs:
                select = SELECT_Q_DATA_GVS;
                break;
        }

        try (Connection connect = ds.getConnection();
             PreparedStatement alter = connect.prepareStatement(ALTER_SQL);
             PreparedStatement stm = connect.prepareStatement(select);
             PreparedStatement stmTnv = connect.prepareStatement(SELECT_TNV);
             PreparedStatement stmRatio = connect.prepareStatement(SELECT_RATIO)) {
            alter.executeQuery();

            // Заносим параметры в основной select получения данных
            stm.setInt(1, requestModel.getStructID());
            stm.setInt(2, requestModel.getFilterID());
            stm.setString(3, requestModel.getFilterValue());
            if (sheetType == tSheetType.Qco || sheetType == tSheetType.Qgvs) {
                stm.setString(4, requestModel.getDate());
            } else {
                stm.setString(4, sheetType.toString());
                stm.setString(5, requestModel.getDate());
            }

            ResultSet res = stm.executeQuery();
            res.setFetchSize(100);

            Cell cell;
            Row row = sheet.createRow(0);

            // Определяет коэффициент рассогласования и заносим его в первую ячейку
            ResultSet resRatio = stmRatio.executeQuery();
            if (resRatio.next()) {
                row.createCell(0).setCellValue("Коэффициент рассогласования: " + resRatio.getString(sheetType.getRatio()));
            }

            // Создаем строку заголовка с датами
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Объект");
            cell.setCellStyle(headerStyle);
            for (int j = 1; j <= daysCount; j++) {
                String value = date.format(ReportRequestModel.FORMATTER);
                date = date.plusDays(1);

                cell = row.createCell(2 * j - 1);
                cell.setCellValue(value);
                cell.setCellStyle(headerStyle);

                cell = row.createCell(2 * j);
                cell.setCellValue(value);
                cell.setCellStyle(headerStyle);
            }

            int i = 3;
            while (res.next()) {
                row = sheet.createRow(i);

                // Создаем ячейку имени объекта
                cell = row.createCell(0);
                cell.setCellValue(res.getString("obj_name"));
                switch (res.getInt("obj_type")) {
                    case 1:
                        cell.setCellStyle(rowStyleCtp);
                        break;
                    case -1:
                    case -2:
                        cell.setCellStyle(rowStyleSum);
                        break;
                }

                // Создаем ячейки со значениями
                for (int j = 1; j <= daysCount; j++) {
                    cell = row.createCell(2 * j - 1);
                    cell.setCellValue(res.getString("p" + j));
                    switch (res.getInt("obj_type")) {
                        case 1:
                            if (res.getInt("p" + j + "_col") == 1) {
                                cell.setCellStyle(rowStyleCtpError);
                            } else {
                                cell.setCellStyle(rowStyleCtp);
                            }
                            break;
                        case -1:
                        case -2:
                            if (res.getInt("p" + j + "_col") == 1) {
                                cell.setCellStyle(rowStyleSumError);
                            } else {
                                cell.setCellStyle(rowStyleSum);
                            }
                            break;
                        default:
                            cell.setCellStyle(errorStyle);
                    }

                    cell = row.createCell(2 * j);
                    cell.setCellValue(res.getString("v" + j));
                    switch (res.getInt("obj_type")) {
                        case 1:
                            if (res.getInt("v" + j + "_col") == 1) {
                                cell.setCellStyle(rowStyleCtpError);
                            } else {
                                cell.setCellStyle(rowStyleCtp);
                            }
                            break;
                        case -1:
                        case -2:
                            if (res.getInt("v" + j + "_col") == 1) {
                                cell.setCellStyle(rowStyleSumError);
                            } else {
                                cell.setCellStyle(rowStyleSum);
                            }
                            break;
                        default:
                            cell.setCellStyle(errorStyle);
                    }
                }

                // Если мы отрисовываем цтп и id полигона не 0, то добавляем строку с тнв
                if ((res.getInt("obj_type") == 1) && (res.getInt("poligon_id") != 0)) {
                    int polygonID = res.getInt("poligon_id");
                    List<String> tnv = new ArrayList<>();
                    i++;

                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue("Температура наружного воздуха");
                    cell.setCellStyle(rowStyleSum);

                    if (tnvMap.containsKey(polygonID)) {
                        tnv = tnvMap.get(polygonID);
                    } else {
                        stmTnv.setInt(1, polygonID);
                        stmTnv.setString(2, requestModel.getFirstDateAtMonthValue());

                        ResultSet resTnv = stmTnv.executeQuery();
                        while (resTnv.next()) {
                            tnv.add(resTnv.getString(1));
                        }

                        tnvMap.put(polygonID, tnv);
                    }

                    for (int j = 0; j < tnv.size(); j++) {
                        for (int k = 0; k < 2; k++) {
                            cell = row.createCell(2 * j + 1 + k);
                            cell.setCellValue(tnv.get(j));
                            cell.setCellStyle(rowStyleSum);
                        }
                    }
                }

                i++;
            }

            for (int j = 0; j < row.getLastCellNum(); j++) {
                sheet.autoSizeColumn(j);
            }

            sheet.createFreezePane(1, 0);

            stopTime = System.currentTimeMillis();
            LOGGER.info("Matrix problems report sheet execution time: " + (stopTime - startTime));
        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }
        return null;
    }
}
