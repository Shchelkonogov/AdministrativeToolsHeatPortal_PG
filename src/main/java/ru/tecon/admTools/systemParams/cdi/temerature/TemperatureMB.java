package ru.tecon.admTools.systemParams.cdi.temerature;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.temperature.TemperatureLocal;
import ru.tecon.admTools.systemParams.cdi.SystemParamsUtilMB;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureProp;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Суперкласс для контроллеров форм температурные графики и суточные снижения
 * @author Maksim Shchelkonogov
 */
public abstract class TemperatureMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TemperatureMB.class.getName());

    private List<Temperature> temperatureTypes = new ArrayList<>();
    private Temperature selectedTemperatureType;
    private TemperatureProp selectedTemperatureProp;

    private Temperature newTemperature = new Temperature();
    private TemperatureProp newTemperatureProp = new TemperatureProp();

    private boolean disableRemoveTemperatureBtn = true;
    private boolean disableRemovePropBtn = true;

    private TemperatureLocal temperatureBean;

    public abstract String getHeaderType();
    public abstract String getHeaderProp();
    public abstract String getHeaderAddDialog();

    @Inject
    private SystemParamsUtilMB utilMB;

    protected void init() {
        loadData();
    }

    /**
     * Метод загружает данные для формы
     */
    private void loadData() {
        temperatureTypes = temperatureBean.getTemperatures();

        selectedTemperatureType = null;
        selectedTemperatureProp = null;
        disableRemoveTemperatureBtn = true;
        disableRemovePropBtn = true;
    }

    /**
     * Обработчик нажатия кнопки удалить тип
     */
    public void onRemoveTemperature() {
        LOGGER.info("remove temperature: " + selectedTemperatureType);

        try {
            temperatureBean.removeTemperature(selectedTemperatureType, utilMB.getLogin(), utilMB.getIp());

            loadData();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * обработчик выбора типа
     * @param event событие выбора
     */
    public void onTemperatureTypeSelect(SelectEvent<Temperature> event) {
        LOGGER.info("select temperature type: " + event.getObject());

        selectedTemperatureType.setTemperatureProps(temperatureBean.loadTemperatureProps(selectedTemperatureType));

        LOGGER.info("load " + selectedTemperatureType.getTemperatureProps().size() + " prop for temperature: " + selectedTemperatureType);

        disableRemoveTemperatureBtn = false;

        selectedTemperatureProp = null;
        disableRemovePropBtn = true;
    }

    /**
     * обработчик удаления значения типа
     */
    public void onRemoveProp() {
        LOGGER.info("remove temperature property: " + selectedTemperatureProp);

        try {
            temperatureBean.removeTemperatureProp(selectedTemperatureType.getId(), selectedTemperatureProp, utilMB.getLogin(), utilMB.getIp());

            selectedTemperatureProp = null;
            disableRemovePropBtn = true;

            selectedTemperatureType.setTemperatureProps(temperatureBean.loadTemperatureProps(selectedTemperatureType));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик выбора значения типа
     * @param event событие выбора
     */
    public void onTemperaturePropSelect(SelectEvent<TemperatureProp> event) {
        LOGGER.info("select temperature property: " + event.getObject());

        disableRemovePropBtn = false;
    }

    /**
     * Обработчик закрытия всплывающего окна добавить значение типа
     */
    public void onAddPropDialogClose() {
        newTemperatureProp = new TemperatureProp();
    }

    public void savePropWrapper() {
        PrimeFaces.current().executeScript("savePropWrapper();");
    }

    public void saveStructWrapper() {
        PrimeFaces.current().executeScript("saveTypeWrapper();");
    }

    /**
     * обработчик создания нового значения типа
     */
    public void onCreateTemperatureProp() {
        LOGGER.info("create new temperature property: " + newTemperatureProp + " for temperature: " + selectedTemperatureType);

        try {
            temperatureBean.addTemperatureProp(selectedTemperatureType.getId(), newTemperatureProp, utilMB.getLogin(), utilMB.getIp());

            selectedTemperatureType.setTemperatureProps(temperatureBean.loadTemperatureProps(selectedTemperatureType));

            selectedTemperatureProp = null;
            disableRemovePropBtn = true;

            PrimeFaces.current().executeScript("PF('addPropDialog').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * Обработчик создания нового типа
     */
    public void onCreateTemperature() {
        LOGGER.info("create new temperature: " + newTemperature + " and temperature props " + newTemperature.getTemperatureProps());

        FacesContext context = FacesContext.getCurrentInstance();

        try {
            int tempGraphID = temperatureBean.createTemperature(newTemperature, utilMB.getLogin(), utilMB.getIp());

            if (!newTemperature.getTemperatureProps().isEmpty()) {
                newTemperature.getTemperatureProps().removeIf(TemperatureProp::check);

                for (TemperatureProp prop: newTemperature.getTemperatureProps()) {
                    try {
                        temperatureBean.addTemperatureProp(tempGraphID, prop, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
                    }
                }
            }

            loadData();

            PrimeFaces.current().executeScript("PF('addTypeDialog').hide();");
        } catch (SystemParamException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    public void onAddTypeDialogClose() {
        newTemperature = new Temperature();
    }

    public void onAddNew() {
        newTemperature.getTemperatureProps().add(new TemperatureProp());
    }

    public Temperature getNewTemperature() {
        return newTemperature;
    }

    public List<Temperature> getTemperatureTypes() {
        return temperatureTypes;
    }

    public Temperature getSelectedTemperatureType() {
        return selectedTemperatureType;
    }

    public void setSelectedTemperatureType(Temperature selectedTemperatureType) {
        this.selectedTemperatureType = selectedTemperatureType;
    }

    public TemperatureProp getSelectedTemperatureProp() {
        return selectedTemperatureProp;
    }

    public void setSelectedTemperatureProp(TemperatureProp selectedTemperatureProp) {
        this.selectedTemperatureProp = selectedTemperatureProp;
    }

    public boolean isDisableRemovePropBtn() {
        return disableRemovePropBtn;
    }

    public List<TemperatureProp> getTemperatureProps() {
        return selectedTemperatureType != null ? selectedTemperatureType.getTemperatureProps() : null;
    }

    public boolean isDisableRemoveTemperatureBtn() {
        return disableRemoveTemperatureBtn;
    }

    public TemperatureProp getNewTemperatureProp() {
        return newTemperatureProp;
    }

    public void setTemperatureBean(TemperatureLocal temperatureBean) {
        this.temperatureBean = temperatureBean;
    }
}
