package ru.tecon.admTools.linker.servlet;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet для запуска формы "Линковщик"
 *
 * @author Maksim Shchelkonogov
 * 07.07.2023
 */
@WebServlet("/linkerWS")
public class ServletWS extends HttpServlet {

    @Inject
    private Logger logger;

    @EJB
    private CheckUserSB checkUserBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("sessionId") && parameterMap.containsKey("ip")) {
            if (checkUserBean.checkSession(req.getParameter("sessionId"))) {
                req.getRequestDispatcher("/view/linker/linkerWS.xhtml").forward(req, resp);
            } else {
                // Авторизуйтесь в системе
                logger.log(Level.WARNING, "authorization error");
                req.getRequestDispatcher("/error.html").forward(req, resp);
            }
        } else {
            // Не хватает параметров
            logger.log(Level.WARNING, "missing parameters");
            req.getRequestDispatcher("/error.html").forward(req, resp);
        }
    }
}
