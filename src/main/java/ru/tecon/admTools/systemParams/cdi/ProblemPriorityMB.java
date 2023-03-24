package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.ProblemPrioritySB;
import ru.tecon.admTools.systemParams.model.ProblemPriority;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы приоритет проблем
 * @author Maksim Shchelkonogov
 */
@Named("problemPriority")
@ViewScoped
public class ProblemPriorityMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ProblemPriorityMB.class.getName());

    private List<ProblemPriority> problemPriorityList = new ArrayList<>();

    @EJB
    private ProblemPrioritySB problemPrioritySB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        problemPriorityList = problemPrioritySB.getProblemPriority();
    }

    /**
     * Обработка события нажатия кнопки сохранить
     */
    public void onSaveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        problemPriorityList.stream().filter(ProblemPriority::isChanged).forEach(problemPriority -> {
            LOGGER.info("update for login " + utilMB.getLogin() + " and ip " + utilMB.getIp() + " problem priority " + problemPriority);

            try {
                problemPrioritySB.updateProblemPriority(problemPriority.getId(), problemPriority.getPriority(), utilMB.getLogin(), utilMB.getIp());
                problemPriority.updatePriority();
            } catch (SystemParamException e) {
                problemPriority.revert();
                errorMessages.add(problemPriority.getName());
                LOGGER.warning(e.getMessage());
            }
        });

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
    }

    public List<ProblemPriority> getProblemPriorityList() {
        return problemPriorityList;
    }
}
