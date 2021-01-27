package ru.tecon.admTools.matrixProblems.report.servlet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import ru.tecon.admTools.matrixProblems.report.ejb.ReportBean;
import ru.tecon.admTools.matrixProblems.report.model.ReportRequestModel;
import ru.tecon.admTools.matrixProblems.report.model.tSheetType;

import javax.ejb.EJB;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Servlet для запроса отчета "Анализ первичных измерений".
 * URL запуска:
 * http://{host}:{port}/admTools/matrixProblems/report
 * post запрос
 * парметры в теле в виде: {"date":"08.10.2020","filterID":0,"filterValue":"текон","structID":1508}
 */
@WebServlet("/matrixProblems/report")
public class Report extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());

    private static final byte[] GREEN = new byte[]{(byte)146, (byte)208, (byte)80};

    @EJB
    private ReportBean reportBean;

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

        LOGGER.info("Request data " + requestModel);

        try (SXSSFWorkbook wb = new SXSSFWorkbook()) {
            SXSSFSheet sheet;

            XSSFFont font = (XSSFFont) wb.createFont();
            font.setBold(true);
            font.setFontHeight(14);

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            XSSFFont colorFont = (XSSFFont) wb.createFont();
            colorFont.setBold(true);
            colorFont.setColor(new XSSFColor(Color.RED, null));

            CellStyle errorStyle = wb.createCellStyle();
            errorStyle.setFont(colorFont);

            XSSFCellStyle rowColorStyleCtp = (XSSFCellStyle) wb.createCellStyle();
            rowColorStyleCtp.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            rowColorStyleCtp.setFillForegroundColor(new XSSFColor(GREEN, null));

            XSSFCellStyle rowColorStyleCtpError = (XSSFCellStyle) wb.createCellStyle();
            rowColorStyleCtpError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            rowColorStyleCtpError.setFillForegroundColor(new XSSFColor(GREEN, null));
            rowColorStyleCtpError.setFont(colorFont);

            XSSFCellStyle rowColorStyleSum = (XSSFCellStyle) wb.createCellStyle();
            rowColorStyleSum.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            rowColorStyleSum.setFillForegroundColor(new XSSFColor(Color.YELLOW, null));

            XSSFCellStyle rowColorStyleSumError = (XSSFCellStyle) wb.createCellStyle();
            rowColorStyleSumError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            rowColorStyleSumError.setFillForegroundColor(new XSSFColor(Color.YELLOW, null));
            rowColorStyleSumError.setFont(colorFont);

            try {
                List<Future<Void>> futures = new ArrayList<>();
                for (tSheetType type: tSheetType.values()) {
                    sheet = wb.createSheet(type.toString());
                    futures.add(reportBean.createSheetT(requestModel, type, sheet,
                            rowColorStyleCtp, rowColorStyleCtpError , rowColorStyleSum, rowColorStyleSumError, headerStyle, errorStyle));
                }

                for (Future<Void> future: futures) {
                    future.get(10, TimeUnit.MINUTES);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.warning("Error create excel pages " + e.getMessage());
                resp.sendError(500);
            }

            resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Матрица проблем.xlsx", "UTF-8") + "\"");
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
}
