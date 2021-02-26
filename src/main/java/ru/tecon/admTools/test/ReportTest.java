package ru.tecon.admTools.test;

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
import java.util.Arrays;

/**
 * Тестовый сервлет запуска отчета по матрице проблем
 * параметр ?report=matrixProblems||dataAnalysis
 */
//@WebServlet("/send")
public class ReportTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String report = req.getParameter("report");

        try {
            Client client = ClientBuilder.newClient();

//            WebTarget target = client.target("https://disp-app-jee.mipcnet.org:8181/admTools/" + report + "/report");
//            WebTarget target = client.target("https://172.16.4.18:8181/admTools/" + report + "/report");
            WebTarget target = client.target("http://localhost:7001/admTools/" + report + "/report");

            Response response;

            switch (report) {
                case "matrixProblems":
                    ReportRequestModel requestModel = new ReportRequestModel(1508, 0, "ТЕКОН", "05.02.2021");
                    GenericEntity<ReportRequestModel> modelWrapper = new GenericEntity<ReportRequestModel>(requestModel){};
                    response = target.request().post(Entity.entity(modelWrapper, MediaType.APPLICATION_JSON));
                    break;
                case "dataAnalysis":
                    ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel requestModel1 = new ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel("S1509", 0, "ТЕКОН", "01.11.2020", "u66//F9q4OzgUxX5EKyc2g==", Arrays.asList(1, 2, 3, 6, 5, 4), Arrays.asList(9, 10, 11, 13));
                    GenericEntity<ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel> modelWrapper1 = new GenericEntity<ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel>(requestModel1){};
                    response = target.request().post(Entity.entity(modelWrapper1, MediaType.APPLICATION_JSON));
                    break;
                default:
                    resp.sendError(500);
                    client.close();
                    return;
            }

            System.out.println("RestService.send status: " + response.getStatus());

            if(response.getStatus() != 200) {
                resp.sendError(500);
            } else {
                resp.setContentType("application/vnd.ms-excel; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode("Отчет.xlsx", "UTF-8") + "\"");
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
