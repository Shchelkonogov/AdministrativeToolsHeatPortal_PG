package ru.tecon.admTools.dataAnalysis.report.servlet;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import ru.tecon.admTools.dataAnalysis.report.ejb.DataAnalysisReportSB;
import ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.ejb.EJB;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Servlet для запроса отчета "Анализ достоверности измерений".
 * URL запуска:
 * http://{host}:{port}/admTools/dataAnalysis/report
 * post запрос
 * Данные ввиде json {@link ReportRequestModel}
 */
@WebServlet("/dataAnalysis/report")
public class Report extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());

    @EJB
    private DataAnalysisReportSB reportBean;

    @EJB
    private CheckUserSB checkUserBean;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                sb.append(line);
        } catch (Exception e) {
            LOGGER.warning("Error read request message " + e.getMessage());
            resp.sendError(500);
        }

        Jsonb json = JsonbBuilder.create();
        ReportRequestModel requestModel = json.fromJson(sb.toString(), ReportRequestModel.class);

        requestModel.setUser(checkUserBean.getUser(requestModel.getSessionID()));

        LOGGER.info("Request data " + requestModel);

        try (SXSSFWorkbook wb = new SXSSFWorkbook()) {
            SXSSFSheet sheet;
            try {
                List<Future<Void>> futures = new ArrayList<>();

                Map<String, CellStyle> styles = createStyles(wb);

                if (!requestModel.getProblemID().isEmpty()) {
                    sheet = wb.createSheet("Сводные данные");
                    futures.add(reportBean.createSummarySheet(requestModel, sheet, styles));
                }

                if (!requestModel.getProblemOdpuID().isEmpty()) {
                    sheet = wb.createSheet("Анализ ОДПУ");
                    futures.add(reportBean.createODPUSheet(requestModel, sheet, styles));
                }

                if (!requestModel.getProblemID().isEmpty()) {
                    LocalDate startDate = requestModel.getFirstDateAtMonth();
                    LocalDate endDate = Stream.of(YearMonth.from(startDate).atEndOfMonth(), LocalDate.now()).min(LocalDate::compareTo).get();

                    for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                        sheet = wb.createSheet(date.format(ReportRequestModel.FORMATTER));
                        futures.add(reportBean.createDaySheet(date.format(ReportRequestModel.FORMATTER), requestModel, sheet, styles));
                    }
                }

                for (Future<Void> future: futures) {
                    future.get(30, TimeUnit.MINUTES);
                }

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.warning("Error create excel pages " + e.getMessage());
                resp.sendError(500);
            }

            resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Анализ качества.xlsx", "UTF-8") + "\"");
            resp.setCharacterEncoding("UTF-8");

            try (OutputStream outputStream = resp.getOutputStream()) {
                wb.write(outputStream);
                outputStream.flush();
            } catch (IOException e) {
                LOGGER.warning("Error send excel file " + e.getMessage());
                resp.sendError(500);
            }
        }
    }

    /**
     * Метод формирует набор стилей для excel документа
     * @param wb документ
     * @return набор стилей
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();

        XSSFFont font14 = (XSSFFont) wb.createFont();
        font14.setBold(true);
        font14.setFontHeight(14);

        XSSFFont font16 = (XSSFFont) wb.createFont();
        font16.setBold(true);
        font16.setFontHeight(16);

        XSSFFont font12Bold = (XSSFFont) wb.createFont();
        font12Bold.setBold(true);
        font12Bold.setFontHeight(12);

        XSSFFont font12 = (XSSFFont) wb.createFont();
        font12.setFontHeight(12);

        CellStyle style = wb.createCellStyle();
        style.setFont(font16);
        style.setAlignment(HorizontalAlignment.CENTER);

        styles.put("header", style);

        style = wb.createCellStyle();
        style.setFont(font14);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        styles.put("tableHeader", style);

        style = wb.createCellStyle();
        style.setFont(font12Bold);
        style.setRotation((short) 90);
        style.setAlignment(HorizontalAlignment.CENTER);

        styles.put("rotate", style);

        style = wb.createCellStyle();
        style.setFont(font12Bold);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        styles.put("centerBoldWrap", style);

        style = wb.createCellStyle();
        style.setFont(font12Bold);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        styles.put("centerBold", style);

        style = wb.createCellStyle();
        style.setFont(font12);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        styles.put("center", style);

        style = wb.createCellStyle();
        style.setFont(font12);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        styles.put("centerWrap", style);

        style = wb.createCellStyle();
        style.setFont(font12);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        styles.put("left", style);

        return styles;
    }
}
