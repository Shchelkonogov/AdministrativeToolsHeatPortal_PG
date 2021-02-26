package ru.tecon.admTools.dataAnalysis.cdi;

import ru.tecon.admTools.dataAnalysis.ejb.DataAnalysisSB;
import ru.tecon.admTools.dataAnalysis.model.Criterion;
import ru.tecon.admTools.dataAnalysis.report.model.ReportRequestModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
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
import java.io.Serializable;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named("dataAnalysis")
@ViewScoped
public class DataAnalysisMB implements Serializable {

    private String sessionID;
    private int filterID;
    private String filterValue;
    private String id;

    private LocalDate date = LocalDate.now();

    private List<Criterion> dayCriterion = new ArrayList<>();
    private List<Criterion> monthCriterion = new ArrayList<>();

    private List<Integer> selectDayCriterionID = new ArrayList<>();
    private List<Integer> selectMonthCriterionID = new ArrayList<>();

    @EJB
    private DataAnalysisSB bean;

    @PostConstruct
    private void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        sessionID = request.getParameter("sessionID");
        filterID = Integer.parseInt(request.getParameter("filterID"));
        filterValue = request.getParameter("filterValue");
        id = request.getParameter("id");

        dayCriterion = bean.getDayCriterion();
        monthCriterion = bean.getMonthCriterion();
    }

    public List<Criterion> getDayCriterion() {
        return dayCriterion;
    }

    public List<Criterion> getMonthCriterion() {
        return monthCriterion;
    }

    public void request() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ec.getRequest();

        String rootURL = request.getRequestURL().toString().replace(request.getServletPath(), "");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(rootURL + "/dataAnalysis/report");

        ReportRequestModel requestModel = new ReportRequestModel(id, filterID, filterValue, date.format(ReportRequestModel.FORMATTER),
                sessionID, selectDayCriterionID, selectMonthCriterionID);
        GenericEntity<ReportRequestModel> modelWrapper1 = new GenericEntity<ReportRequestModel>(requestModel){};
        Response response = target.request().post(Entity.entity(modelWrapper1, MediaType.APPLICATION_JSON));

        ec.responseReset();
        if(response.getStatus() != 200) {
            ec.responseSendError(500, "");
        } else {
            ec.setResponseContentType("application/vnd.ms-excel; charset=UTF-8");
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" +
                    URLEncoder.encode("Анализ", "UTF-8") + " " + URLEncoder.encode("достоверности.xlsx", "UTF-8") + "\"");
            ec.setResponseCharacterEncoding("UTF-8");

            try (InputStream inputStream = (InputStream) response.getEntity();
                 OutputStream outputStream = ec.getResponseOutputStream()) {
                copy(inputStream, outputStream);

                outputStream.flush();
            }
        }

        client.close();

        fc.responseComplete();
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Integer> getSelectDayCriterionID() {
        return selectDayCriterionID;
    }

    public void setSelectDayCriterionID(List<Integer> selectDayCriterionID) {
        this.selectDayCriterionID = selectDayCriterionID;
    }

    public List<Integer> getSelectMonthCriterionID() {
        return selectMonthCriterionID;
    }

    public void setSelectMonthCriterionID(List<Integer> selectMonthCriterionID) {
        this.selectMonthCriterionID = selectMonthCriterionID;
    }
}
