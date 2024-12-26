package ru.tecon.admTools.dataAnalysis.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

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
