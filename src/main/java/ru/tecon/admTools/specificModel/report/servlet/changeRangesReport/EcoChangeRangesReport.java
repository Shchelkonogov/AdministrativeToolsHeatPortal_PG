package ru.tecon.admTools.specificModel.report.servlet.changeRangesReport;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.specificModel.report.ejb.ChangeRangesLocal;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet для запроса отчета "Отчет по изменению тех границ" для конкретной модели экомониторинга.
 * URL запуска:
 * http://{host}:{port}/admTools/ecoSpecificModel/report/technicalLimitsChangeReport?
 *     id={("S" + structId) || ("O" + objectId)}&objectType={objectType}&date={dd.mm.yyyy}&sessionId={sessionId}
 */
@WebServlet("/ecoSpecificModel/report/technicalLimitsChangeReport")
public class EcoChangeRangesReport extends HttpServlet {

    @EJB
    private ChangeRangesLocal bean;

    @EJB
    private CheckUserSB checkBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReportWrapper.report(req, resp, checkBean, bean, true);
    }
}
