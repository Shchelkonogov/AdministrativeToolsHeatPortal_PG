package ru.tecon.admTools.specificModel.report.servlet;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.specificModel.report.ModeControl;
import ru.tecon.admTools.specificModel.report.ejb.ModeControlLocal;

import javax.ejb.EJB;
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
 * Servlet для запроса отчета "Контроль режима".
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/modeControl?
 *     structID={structID}&paramID={paramID}&objectType={objectType}&sessionID={sessionID}
 */
@WebServlet("/specificModel/report/modeControl")
public class ModeControlReport extends HttpServlet {

    private static Logger log = Logger.getLogger(ModeControlReport.class.getName());

//    @EJB
//    private ModeControlLocal bean;

//    @EJB
//    private CheckUserSB checkBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO модуль в стадии переработки под PostgreSQL
        try {
            req.getRequestDispatcher("/inWork.html").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        }

//        String user = checkBean.getUser(req.getParameter("sessionID"));
//        if (user != null) {
//            int objectType = Integer.parseInt(req.getParameter("objectType"));
//            int structID = Integer.parseInt(req.getParameter("structID"));
//            int paramID = Integer.parseInt(req.getParameter("paramID"));
//
//            resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
//            resp.setHeader("Content-Disposition", "attachment; filename=\"" +
//                    URLEncoder.encode("Контроль режима.xlsx", "UTF-8") + "\"");
//            resp.setCharacterEncoding("UTF-8");
//
//            try (OutputStream output = resp.getOutputStream()) {
//                ModeControl.generateModeControl(objectType, 0, "", structID, paramID, user, bean).write(output);
//                output.flush();
//            } catch (IOException e) {
//                log.log(Level.WARNING, "error send report", e);
//            }
//        }
    }
}
