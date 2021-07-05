package ru.tecon.admTools.dataAnalysis.cdi;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named("dataAnalysis")
@ViewScoped
public class DataAnalysisMB implements Serializable {

    private static final Logger LOG = Logger.getLogger(DataAnalysisSB.class.getName());

    private String sessionID;
    private int filterID;
    private String filterValue;
    private String id;

    private LocalDate date = LocalDate.now();

    private List<Criterion> dayCriterion = new ArrayList<>();
    private List<Criterion> monthCriterion = new ArrayList<>();

    private List<Integer> selectDayCriterionID = new ArrayList<>();
    private List<Integer> selectMonthCriterionID = new ArrayList<>();

    private StreamedContent file;

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

        LOG.info("report status " + response.getStatus());

        ec.responseReset();
        if(response.getStatus() != 200) {
            ec.responseSendError(500, "");
        } else {
            file = DefaultStreamedContent.builder()
                    .name("Анализ достоверности.xlsx")
                    .contentType("application/vnd.ms-excel; charset=UTF-8")
                    .stream(() -> (InputStream) response.getEntity())
                    .build();
        }

        client.close();

        fc.responseComplete();
    }

    public StreamedContent getFile() {
        return file;
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
