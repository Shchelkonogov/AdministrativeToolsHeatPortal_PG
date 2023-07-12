package ru.tecon.admTools.specificModel.report.servlet;

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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet для запроса отчета "Режимная карта".
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/modeMap?objectID={objectID}&sessionID={sessionID}
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
        if (parameterMap.containsKey("sessionID") && parameterMap.containsKey("objectID")) {
            try (OutputStream output = resp.getOutputStream()) {

                int object = Integer.parseInt(req.getParameter("objectID"));

                if (checkBean.checkSession(req.getParameter("sessionID"))) {
                    resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Режимная карта.xlsx", "UTF-8") + "\"");
                    resp.setCharacterEncoding("UTF-8");

                    ModeMap.generateModeMap(object, bean).write(output);
                    output.flush();
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
