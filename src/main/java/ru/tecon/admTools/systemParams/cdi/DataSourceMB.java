package ru.tecon.admTools.systemParams.cdi;

import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.DataSourceSB;
import ru.tecon.admTools.systemParams.model.DataSourceObject;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 24.09.2025
 */
@Named("dataSourceMB")
@ViewScoped
public class DataSourceMB implements Serializable, AutoUpdate {

    private List<DataSourceObject> sourceTable;
    private DataSourceObject selectedSource;
    private DataSourceObject newSource = new DataSourceObject();

    private boolean disableRemoveBtn = true;

    @Inject
    private transient Logger logger;

    @Inject
    private SystemParamsUtilMB utilMB;

    @EJB
    private DataSourceSB bean;

    @Override
    public void update() {
        try {
            sourceTable = bean.getSourceTable(utilMB.getSessionId(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка загрузки", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }

        selectedSource = null;
        disableRemoveBtn = true;
    }

    /**
     * Обработчик события выделения строки
     *
     * @param event событие
     */
    public void onRowSelect(SelectEvent<StatAggrTable> event) {
        logger.info("select source: " + event.getObject());
        disableRemoveBtn = false;
    }

    /**
     * Обработчик удаления источника данных из таблицы, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveTableSource() {
        logger.info("remove source: " + selectedSource);

        try {
            bean.removeSource(selectedSource, utilMB.getLogin(), utilMB.getIp());

            update();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    public void onSaveChangesWrapper() {
        PrimeFaces.current().executeScript("saveSourceWrapper()");
    }

    /**
     * Обработчик сохранения нового источника данных в таблицу, нажатие на копку сохранить
     */
    public void onSaveChanges() {
        logger.info("add source: " + newSource);
        try {
            bean.addSource(newSource, utilMB.getSessionId(), utilMB.getLogin(), utilMB.getIp());

            update();

            PrimeFaces.current().executeScript("PF('addNewSource').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * Обработчик обновления диалогового окна в случае его закрытия
     */
    public void onDialogClose() {
        newSource = new DataSourceObject();
    }

    public List<DataSourceObject> getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(List<DataSourceObject> sourceTable) {
        this.sourceTable = sourceTable;
    }

    public DataSourceObject getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(DataSourceObject selectedSource) {
        this.selectedSource = selectedSource;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }

    public DataSourceObject getNewSource() {
        return newSource;
    }

    public void setNewSource(DataSourceObject newSource) {
        this.newSource = newSource;
    }
}
