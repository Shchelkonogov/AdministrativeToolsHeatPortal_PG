package ru.tecon.admTools.linker.report;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 09.11.2023
 */
@WebServlet("/linker/report")
public class ReportServlet extends HttpServlet {

    @Inject
    private Logger logger;

    @EJB
    private ReportDataLoaderBean bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int objectId;
        try {
            objectId = Integer.parseInt(req.getParameter("objectId"));
        } catch (NumberFormatException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        logger.log(Level.INFO, "request linker report {0}", objectId);

        ReportData reportData = bean.getReportData(objectId);

        String reportName = reportData.getHeader().replaceAll("[<>:\"/|?*\\\\]", "_");

        resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" +
                        URLEncoder.encode("Отчет", "UTF-8") + " " +
                        URLEncoder.encode("по", "UTF-8") + " " +
                        URLEncoder.encode("линковке", "UTF-8") + " " +
                        URLEncoder.encode(reportName + ".xlsx", "UTF-8") +
                        "\"");
        resp.setCharacterEncoding("UTF-8");

        try (OutputStream output = resp.getOutputStream()) {
            Report report = new Report(reportData);
            report.create();

            try (Workbook workbook = report.getWorkbook()) {
                workbook.write(output);

                if (workbook instanceof SXSSFWorkbook) {
                    ((SXSSFWorkbook) workbook).dispose();
                }
            }

            output.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING, "error send created report", e);
        }
    }
}
