package ru.tecon.admTools.specificModel.report.servlet;

import org.apache.poi.ss.usermodel.Workbook;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.specificModel.report.ModeMap;
import ru.tecon.admTools.specificModel.report.ejb.ModeMapLocal;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet для запроса отчета "Режимная карта".
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/modeMap?objectId={objectId}&sessionId={sessionId}
 */
@WebServlet("/specificModel/report/modeMap")
public class ModeMapReport extends HttpServlet {

    @Inject
    private Logger logger;
    @EJB
    private ModeMapLocal bean;

    @EJB
    private CheckUserSB checkBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("sessionId") && parameterMap.containsKey("objectId")) {
            try (OutputStream output = resp.getOutputStream()) {

                int object = Integer.parseInt(req.getParameter("objectId"));

                if (checkBean.checkSession(req.getParameter("sessionId"))) {
                    String curDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    String objName = bean.getName(object);

                    resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                            URLEncoder.encode("Режимная карта " + objName + " " + curDate + ".xlsx", "UTF-8") + "\"");
                    resp.setCharacterEncoding("UTF-8");

                    try (Workbook workbook = ModeMap.generateModeMap(object, bean)) {
                        workbook.write(output);
                        output.flush();
                    }
                } else {
                    //             Авторизуйтесь в системе
                    logger.log(Level.WARNING, "authorization error");
                    req.getRequestDispatcher("/error.html").forward(req, resp);
                }
            } catch (IOException e) {
                // Не корректный параметр formID
                logger.log(Level.WARNING, "error send report", e);
                req.getRequestDispatcher("/error.html").forward(req, resp);
            }
        } else {
            // Не хватает параметров
            logger.log(Level.WARNING, "missing parameters");
            req.getRequestDispatcher("/error.html").forward(req, resp);
        }
    }
}
