package ru.tecon.admTools.systemParams.cdi;

import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.ejb.JobsSB;
import ru.tecon.admTools.systemParams.model.job.JobHistory;
import ru.tecon.admTools.systemParams.model.job.Jobs;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы Фоновые процессы
 *
 * @author Maksim Shchelkonogov
 * 06.06.2025
 */
@Named("jobs")
@ViewScoped
public class JobsMB implements Serializable, AutoUpdate {

    private List<Jobs> jobsList;
    private Jobs selectedJobType;

    private List<JobHistory> jobHistories;

    @Inject
    private transient Logger logger;

    @EJB
    private JobsSB bean;

    @Override
    public void update() {
        jobsList = bean.getJobs();
        jobHistories = null;
        selectedJobType = null;
    }

    /**
     * обработчик выбора справочника
     * @param event событие выбора
     */
    public void onJobTypeSelect(SelectEvent<Jobs> event) {
        logger.info("select job type: " + event.getObject() + " " + selectedJobType);

        jobHistories = bean.getJobHistory(selectedJobType.getJobNme());
    }

    public String getJobHistoryHeader() {
        return selectedJobType == null ? "" : " (" + selectedJobType.getComment() + ")";
    }

    public List<Jobs> getJobsList() {
        return jobsList;
    }

    public List<JobHistory> getJobHistories() {
        return jobHistories;
    }

    public Jobs getSelectedJobType() {
        return selectedJobType;
    }

    public void setSelectedJobType(Jobs selectedJobType) {
        this.selectedJobType = selectedJobType;
    }
}
