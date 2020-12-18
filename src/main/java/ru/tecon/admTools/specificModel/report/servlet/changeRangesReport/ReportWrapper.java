package ru.tecon.admTools.specificModel.report.servlet.changeRangesReport;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.specificModel.report.ChangeRanges;
import ru.tecon.admTools.specificModel.report.ejb.ChangeRangesLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

class ReportWrapper {

    private static Logger log = Logger.getLogger(ReportWrapper.class.getName());

    static void report(HttpServletRequest req, HttpServletResponse resp,
                       CheckUserSB checkBean, ChangeRangesLocal bean,
                       boolean eco) throws UnsupportedEncodingException {
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
                ChangeRanges.generateChangeRanges(objectType, structID, objID, 0, "", date, user, bean, eco).write(output);
                output.flush();
            } catch (IOException e) {
                log.log(Level.WARNING, "error send report", e);
            }
        }
    }
}
