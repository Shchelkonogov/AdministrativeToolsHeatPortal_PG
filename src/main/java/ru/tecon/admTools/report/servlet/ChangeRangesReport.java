package ru.tecon.admTools.report.servlet;

import ru.tecon.admTools.ejb.CheckUserSB;
import ru.tecon.admTools.report.ChangeRanges;
import ru.tecon.admTools.report.ejb.ChangeRangesLocal;

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
 * Servlet для запроса отчета "Отчет по изменению тех границ".
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/technicalLimitsChangeReport?
 *     id={("S" + structID) || ("O" + objectID)}&objectType={objectType}&date={dd.mm.yyyy}&sessionID={sessionID}
 */
@WebServlet("/specificModel/report/technicalLimitsChangeReport")
public class ChangeRangesReport extends HttpServlet {

    private static Logger log = Logger.getLogger(ChangeRangesReport.class.getName());

    @EJB
    private ChangeRangesLocal bean;

    @EJB
    private CheckUserSB checkBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = checkBean.getUser(req.getParameter("sessionID"));
        if (user != null) {
            int objectType = Integer.parseInt(req.getParameter("objectType"));
            String date = req.getParameter("date");
            String id = req.getParameter("id");
            Integer structID = null;
            Integer objID = null;

            if (id.contains("O")) {
                objID = Integer.parseInt(id.replace("O", ""));
            }

            if (id.contains("S")) {
                structID = Integer.parseInt(id.replace("S", ""));
            }

            resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode("Отчет по изменению тех границ.xlsx", "UTF-8") + "\"");
            resp.setCharacterEncoding("UTF-8");

            try (OutputStream output = resp.getOutputStream()) {
                ChangeRanges.generateChangeRanges(objectType, structID, objID, 0, "", date, user, bean).write(output);
                output.flush();
            } catch (IOException e) {
                log.log(Level.WARNING, "error send report", e);
            }
        }
    }
}
