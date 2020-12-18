package ru.tecon.admTools.specificModel.report.servlet.changeRangesReport;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.specificModel.report.ejb.ChangeRangesLocal;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet для запроса отчета "Отчет по изменению тех границ" для конкретной модели.
 * URL запуска:
 * http://{host}:{port}/admTools/specificModel/report/technicalLimitsChangeReport?
 *     id={("S" + structID) || ("O" + objectID)}&objectType={objectType}&date={dd.mm.yyyy}&sessionID={sessionID}
 */
@WebServlet("/specificModel/report/technicalLimitsChangeReport")
public class ChangeRangesReport extends HttpServlet {

    @EJB
    private ChangeRangesLocal bean;

    @EJB
    private CheckUserSB checkBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReportWrapper.report(req, resp, checkBean, bean, false);
    }
}
