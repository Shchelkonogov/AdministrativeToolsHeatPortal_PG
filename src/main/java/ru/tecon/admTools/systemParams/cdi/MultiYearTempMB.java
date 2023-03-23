package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.RowEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.MultiYearTempSB;
import ru.tecon.admTools.systemParams.model.MultiYearTemp;

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
 * Контроллер для формы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
@Named("multiYearTemp")
@ViewScoped
public class MultiYearTempMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MultiYearTempMB.class.getName());

    private List<MultiYearTemp> multiYearTemps = new ArrayList<>();

    @EJB
    private MultiYearTempSB multiYearTempSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    /**
     * Метод выполняется при загрузки формы
     */
    public void onFormLoad() {
        LOGGER.info("load form data");

        loadData();
    }

    private void loadData() {
        multiYearTemps = multiYearTempSB.getMultiTnv();
    }

    /**
     * Обработчик сохранения изменения строки
     * @param event событие
     */
    public void onRowEdit(RowEditEvent<MultiYearTemp> event) {
        LOGGER.info("update row " + event.getObject());

        MultiYearTemp temp = event.getObject();

        try {
            multiYearTempSB.updateMultiYearTemp(temp, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
            loadData();
        }
    }

    public List<MultiYearTemp> getMultiYearTemps() {
        return multiYearTemps;
    }
}
