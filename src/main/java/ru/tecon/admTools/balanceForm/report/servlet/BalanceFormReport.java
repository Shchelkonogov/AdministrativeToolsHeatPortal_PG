package ru.tecon.admTools.balanceForm.report.servlet;

import ru.tecon.admTools.balanceForm.report.Report;
import ru.tecon.admTools.balanceForm.report.ejb.BalanceFormReportSBLocal;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 */
@WebServlet("/loadPeriodReport")
public class BalanceFormReport extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(BalanceFormReport.class.getName());

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @EJB(name = "balanceFormReport")
    private BalanceFormReportSBLocal bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int object = Integer.parseInt(req.getParameter("object"));
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");

        LOGGER.info("load period report with params: objectID " + object + " startDate " + startDate + " endDate " + endDate);

        resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" +
                        URLEncoder.encode("Баланс", "UTF-8") + " " +
                        URLEncoder.encode("по", "UTF-8") + " " +
                        URLEncoder.encode("ЦТП", "UTF-8") + " " +
                        URLEncoder.encode("(месяц).xlsx", "UTF-8") +
                        "\"");
        resp.setCharacterEncoding("UTF-8");

        try (OutputStream output = resp.getOutputStream()) {
            Report.createMonthReport(object, LocalDate.parse(startDate, FORMATTER), bean).write(output);
            output.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "error send created report", e);
        }
    }
}
