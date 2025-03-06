package ru.tecon.admTools.balanceForm.report.servlet;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import ru.tecon.admTools.balanceForm.report.Report;
import ru.tecon.admTools.balanceForm.report.ejb.BalanceFormReportSBLocal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 */
@WebServlet("/loadPeriodReport")
public class BalanceFormReport extends HttpServlet {

    @Inject
    private Logger logger;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @EJB(beanName = "balanceFormReport")
    private BalanceFormReportSBLocal bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int object = Integer.parseInt(req.getParameter("object"));
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");

        logger.info("load period report with params: objectID " + object + " startDate " + startDate + " endDate " + endDate);

        resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" +
                        URLEncoder.encode("Баланс", StandardCharsets.UTF_8) + " " +
                        URLEncoder.encode("по", StandardCharsets.UTF_8) + " " +
                        URLEncoder.encode("ЦТП", StandardCharsets.UTF_8) + " " +
                        URLEncoder.encode("(период).xlsx", StandardCharsets.UTF_8) +
                        "\"");
        resp.setCharacterEncoding("UTF-8");

        try (OutputStream output = resp.getOutputStream()) {
            if (startDate.equals(endDate)) {
                try (Workbook wb = Report.createDayReport(object, LocalDate.parse(startDate, FORMATTER), bean)) {
                    wb.write(output);
                }
            } else {
                try (Workbook wb = Report.createMonthReport(object, LocalDate.parse(startDate, FORMATTER), LocalDate.parse(endDate, FORMATTER), bean)) {
                    wb.write(output);
                }
            }
            output.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING, "error send created report", e);
        }
    }
}
