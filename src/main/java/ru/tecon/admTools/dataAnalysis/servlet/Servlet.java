package ru.tecon.admTools.dataAnalysis.servlet;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dataAnalysis")
public class Servlet extends HttpServlet {

//    @EJB
//    private CheckUserSB bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionID = req.getParameter("sessionID");

        // TODO модуль в стадии переработки под PostgreSQL
        req.getRequestDispatcher("/inWork.html").forward(req, resp);

//        if ((sessionID != null) && (bean.checkSession(sessionID))) {
//            req.getRequestDispatcher("/dataAnalysis.xhtml").forward(req, resp);
//        } else {
//            req.getRequestDispatcher("/error.html").forward(req, resp);
//        }
    }
}
