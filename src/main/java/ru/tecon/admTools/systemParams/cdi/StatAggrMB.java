package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.StatAggrSB;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы статистические агрегаты
 * @author Aleksey Sergeev
 */
@Named("statAggrMB")
@ViewScoped
public class StatAggrMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(StatAggrMB.class.getName());

    private List<StatAggrTable> statsAggrTable = new ArrayList<>();
    private StatAggrTable selectedPartInSATable;
    private StatAggrTable addStatAggrTable= new StatAggrTable();

    private String login;
    private String ip;

    private boolean disableRemoveBtn = true;
    private boolean write=false;

    @EJB
    StatAggrSB statAggrSB;

    @PostConstruct
    private void init() {
        statsAggrTable = statAggrSB.getSATableParam();

        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");
    }

    /**
    * Обработчик события выделения строки
    * @param event событие
    */
    public void onRowSelect(SelectEvent<StatAggrTable> event) {
        LOGGER.info("select link: " + event.getObject());
        disableRemoveBtn = false;
    }

    /**
     * Обработчик удаления статистического агрегата из таблицы, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveTableParam() {
        LOGGER.info("remove param: " + selectedPartInSATable);

        try {
            statAggrSB.removeParamFromTable(selectedPartInSATable, login, ip);

            selectedPartInSATable = null;
            disableRemoveBtn = true;

            statsAggrTable=statAggrSB.getSATableParam();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик добавления нового параметра в таблицу, нажатие на копку добавить статистический агрегат (+)
     */
    public void onAddNew(){
        statsAggrTable.add(new StatAggrTable());
    }

    /**
     * Обработчик сохранения изменения нового параметра в таблицу, нажатие на копку сохранить
     */
    public void onSaveChanges() {

        try {
            statAggrSB.addStatAggr(addStatAggrTable, login, ip);

            PrimeFaces.current().executeScript("PF('addNewParam').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
        statsAggrTable = statAggrSB.getSATableParam();
    }

    /**
     * Обработчик обновления диалогового окна в случае его закрытия
     */

    public void onAddStatAggrDialogClose(){
        addStatAggrTable=new StatAggrTable();
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

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public StatAggrTable getAddStatAggrTable() {
        return addStatAggrTable;
    }

    public void setAddStatAggrTable(StatAggrTable addStatAggrTable) {
        this.addStatAggrTable = addStatAggrTable;
    }
}
