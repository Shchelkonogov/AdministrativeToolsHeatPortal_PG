package ru.tecon.admTools.mobile;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 21.04.2025
 */
@WebServlet("/m.techParam")
public class TechParamServlet extends HttpServlet {

    @Inject
    private Logger logger;

    @EJB
    private CheckUserSB bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("sessionId")) {
            if (bean.checkSession(req.getParameter("sessionId"))) {
                req.getRequestDispatcher("/view/mobile/techParams.xhtml").forward(req, resp);
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
