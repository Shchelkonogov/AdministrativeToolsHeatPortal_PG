package ru.tecon.admTools.specificModel.servlet;

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
 * Сервлет обертка для запуска формы Конкретная модель объекта
 * @author Aleksey Sergeev
 */
@WebServlet("/specificModel")
public class Servlet extends HttpServlet {

    @Inject
    private Logger logger;

    @EJB
    private CheckUserSB bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("sessionID") && parameterMap.containsKey("formID") && parameterMap.containsKey("objectID")) {
        try {
            int ignore = Integer.parseInt(req.getParameter("formID"));

                if (bean.checkSession(req.getParameter("sessionID"))) {
            req.getRequestDispatcher("/specificModel.xhtml?eco=false").forward(req, resp);
                } else {
//             Авторизуйтесь в системе
                    logger.log(Level.WARNING, "authorization error");
                    req.getRequestDispatcher("/error.html").forward(req, resp);
                }
        } catch (NumberFormatException ex) {
            // Не корректный параметр formID
            logger.log(Level.WARNING, "invalid parameter \"formID\"");
            req.getRequestDispatcher("/error.html").forward(req, resp);
        }
    }
        else {
            // Не хватает параметров
            logger.log(Level.WARNING, "missing parameters");
            req.getRequestDispatcher("/error.html").forward(req, resp);
        }
    }
}