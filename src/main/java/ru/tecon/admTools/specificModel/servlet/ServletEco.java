package ru.tecon.admTools.specificModel.servlet;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ecoSpecificModel")
public class ServletEco extends HttpServlet {

    @EJB
    private CheckUserSB bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionID = req.getParameter("sessionID");
        if ((sessionID != null) && (bean.checkSession(sessionID))) {
            req.getRequestDispatcher("/specificModel.xhtml?eco=true").forward(req, resp);
        } else {
            req.getRequestDispatcher("/error.html").forward(req, resp);
        }
    }
}
