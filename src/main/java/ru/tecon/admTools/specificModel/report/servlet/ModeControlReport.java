package ru.tecon.admTools.specificModel.report.servlet;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.specificModel.report.ModeControl;
import ru.tecon.admTools.specificModel.report.ejb.ModeControlLocal;

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
 * Servlet для запроса отчета "Контроль режима".
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/modeControl?
 *     structId={structId}&paramId={paramId}&objectType={objectType}&sessionId={sessionId}
 */
@WebServlet("/specificModel/report/modeControl")
public class ModeControlReport extends HttpServlet {

    @EJB
    private ModeControlLocal bean;

    @EJB
    private CheckUserSB checkBean;

    @Inject
    private Logger logger;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("sessionId") && parameterMap.containsKey("objectType") &&
                parameterMap.containsKey("structId") && parameterMap.containsKey("paramId")) {
            String user = checkBean.getUser(req.getParameter("sessionId"));
            try (OutputStream output = resp.getOutputStream()) {
                int objectType = Integer.parseInt(req.getParameter("objectType"));
                int structID = Integer.parseInt(req.getParameter("structId"));
                int paramID = Integer.parseInt(req.getParameter("paramId"));

                if (checkBean.checkSession(req.getParameter("sessionId"))) {

                    resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                            URLEncoder.encode("Контроль режима.xlsx", "UTF-8") + "\"");
                    resp.setCharacterEncoding("UTF-8");

                    ModeControl.generateModeControl(objectType, 0, "", structID, paramID, user, bean).write(output);
                    output.flush();
                } else {
                    //             Авторизуйтесь в системе
                    logger.log(Level.WARNING, "authorization error");
                    req.getRequestDispatcher("/error.html").forward(req, resp);
                }
            } catch (IOException e) {
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
