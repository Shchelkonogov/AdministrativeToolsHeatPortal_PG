package ru.tecon.admTools.report.servlet;

import ru.tecon.admTools.ejb.CheckUserSB;
import ru.tecon.admTools.report.ModeMap;
import ru.tecon.admTools.report.ejb.ModeMapLocal;

import javax.ejb.EJB;
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
 * Servlet для запроса отчета "Режимная карта".
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/modeMap?objectID={objectID}&sessionID={sessionID}
 */
@WebServlet("/specificModel/report/modeMap")
public class ModeMapReport extends HttpServlet {

    private static Logger log = Logger.getLogger(ModeMapReport.class.getName());

    @EJB
    private ModeMapLocal bean;

    @EJB
    private CheckUserSB checkBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (checkBean.checkSession(req.getParameter("sessionID"))) {
            int object = Integer.parseInt(req.getParameter("objectID"));

            resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Режимная карта.xlsx", "UTF-8") + "\"");
            resp.setCharacterEncoding("UTF-8");

            try (OutputStream output = resp.getOutputStream()) {
                ModeMap.generateModeMap(object, bean).write(output);
                output.flush();
            } catch (IOException e) {
                log.log(Level.WARNING, "error send report", e);
            }
        }
    }
}
