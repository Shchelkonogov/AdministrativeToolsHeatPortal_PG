package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.StatAggrSB;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;

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
 * Контроллер для формы статистические агрегаты
 *
 * @author Aleksey Sergeev
 */
@Named("statAggrMB")
@ViewScoped
public class StatAggrMB implements Serializable, AutoUpdate {

    private List<StatAggrTable> statsAggrTable = new ArrayList<>();
    private StatAggrTable selectedPartInSATable;
    private StatAggrTable addStatAggrTable = new StatAggrTable();

    private boolean disableRemoveBtn = true;

    @Inject
    private transient Logger logger;

    @EJB
    private StatAggrSB statAggrSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        statsAggrTable = statAggrSB.getSATableParam();

        disableRemoveBtn = true;

        PrimeFaces.current().executeScript("PF('statAggrTable').unselectAllRows()");
    }

    /**
     * Обработчик события выделения строки
     *
     * @param event событие
     */
    public void onRowSelect(SelectEvent<StatAggrTable> event) {
        logger.info("select link: " + event.getObject());
        disableRemoveBtn = false;
    }

    /**
     * Обработчик удаления статистического агрегата из таблицы, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveTableParam() {
        logger.info("remove param: " + selectedPartInSATable);

        try {
            statAggrSB.removeParamFromTable(selectedPartInSATable, utilMB.getLogin(), utilMB.getIp());

            selectedPartInSATable = null;
            disableRemoveBtn = true;

            statsAggrTable = statAggrSB.getSATableParam();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    public void onSaveChangesWrapper() {
        PrimeFaces.current().executeScript("saveStatAggregateWrapper()");
    }

    /**
     * Обработчик сохранения изменения нового параметра в таблицу, нажатие на копку сохранить
     */
    public void onSaveChanges() {
        try {
            statAggrSB.addStatAggr(addStatAggrTable, utilMB.getLogin(), utilMB.getIp());

            statsAggrTable = statAggrSB.getSATableParam();

            PrimeFaces.current().executeScript("PF('addNewParam').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
    }

    /**
     * Обработчик обновления диалогового окна в случае его закрытия
     */
    public void onAddStatAggrDialogClose() {
        addStatAggrTable = new StatAggrTable();
    }

    public List<StatAggrTable> getStatsAggrTable() {
        return statsAggrTable;
    }

    public void setStatsAggrTable(List<StatAggrTable> statsAggrTable) {
        this.statsAggrTable = statsAggrTable;
    }

    public StatAggrTable getSelectedPartInSATable() {
        return selectedPartInSATable;
    }

    public void setSelectedPartInSATable(StatAggrTable selectedPartInSATable) {
        this.selectedPartInSATable = selectedPartInSATable;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }

    public void setDisableRemoveBtn(boolean disableRemoveBtn) {
        this.disableRemoveBtn = disableRemoveBtn;
    }

    public StatAggrTable getAddStatAggrTable() {
        return addStatAggrTable;
    }

    public void setAddStatAggrTable(StatAggrTable addStatAggrTable) {
        this.addStatAggrTable = addStatAggrTable;
    }
}
