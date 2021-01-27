package ru.tecon.admTools.matrixProblems.report.servlet;

import ru.tecon.admTools.matrixProblems.report.model.ReportRequestModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Тестовый сервлет запуска отчета по матрице проблем
 */
//@WebServlet("/matrixProblems/send")
public class ReportTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:7001/admTools/matrixProblems/report");

            ReportRequestModel requestModel = new ReportRequestModel(1509, 0, "ТЕКОН", "01.10.2020");

            GenericEntity<ReportRequestModel> modelWrapper = new GenericEntity<ReportRequestModel>(requestModel){};

            Response response = target.request().post(Entity.entity(modelWrapper, MediaType.APPLICATION_JSON));

            System.out.println("RestService.send status: " + response.getStatus());

            if(response.getStatus() != 200) {
                resp.sendError(500);
            } else {
                resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Матрица проблем.xlsx", "UTF-8") + "\"");
                resp.setCharacterEncoding("UTF-8");

                InputStream inputStream = (InputStream) response.getEntity();
                OutputStream outputStream = resp.getOutputStream();

                copy(inputStream, outputStream);
                outputStream.flush();
            }

            client.close();
        } catch(IOException e) {
            System.out.println("RestService.send ERROR WHILE SEND");
            e.printStackTrace();
        }
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }
}
