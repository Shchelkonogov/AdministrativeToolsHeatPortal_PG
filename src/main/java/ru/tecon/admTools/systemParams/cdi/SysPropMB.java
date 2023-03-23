package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.converter.MyConverter;
import ru.tecon.admTools.systemParams.ejb.MeasureSB;
import ru.tecon.admTools.systemParams.ejb.SysPropSB;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.SysProp;
import ru.tecon.admTools.systemParams.model.struct.PropValType;

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
 * Контроллер для работы с фирмой системные параметры
 * @author Maksim Shchelkonogov
 */
@Named("sysProp")
@ViewScoped
public class SysPropMB implements Serializable, MyConverter {

    private static final Logger LOGGER = Logger.getLogger(SysPropMB.class.getName());

    private List<SysProp> props = new ArrayList<>();
    private SysProp selectedProp;

    private List<PropValType> propValTypes = new ArrayList<>();
    private List<Measure> measures = new ArrayList<>();

    private boolean disableRemoveBtn = true;

    @EJB
    private StructSB structSB;

    @EJB
    private MeasureSB measureSB;

    @EJB
    private SysPropSB sysPropSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        props = sysPropSB.getSysProps();

        propValTypes = structSB.getPropValTypes();
        measures = measureSB.getMeasures();
    }

    /**
     * Обработчки нажатия кнопки удаления систменого параметра
     */
    public void onRemoveProp() {
        LOGGER.info("remove system property: " + selectedProp);

        try {
            sysPropSB.removeSysProp(selectedProp, utilMB.getLogin(), utilMB.getIp());

            selectedProp = null;
            disableRemoveBtn = true;

            props = sysPropSB.getSysProps();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик нажатия кнопки добавления системного параметра
     */
    public void onAddNewProp() {
        props.add(new SysProp("Новое свойство"));
    }

    /**
     * Обработчик изменения строки в таблице
     * @param event данные по изменяемой строке
     */
    public void onRowEdit(RowEditEvent<SysProp> event) {
        LOGGER.info("update row " + event.getObject());

        SysProp prop = event.getObject();

        try {
            prop.setId(sysPropSB.addSysProp(prop, utilMB.getLogin(), utilMB.getIp()));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
            props = sysPropSB.getSysProps();
        }

        selectedProp = null;
        disableRemoveBtn = true;
    }

    /**
     * Обработчик выделения строки
     * @param event данные выделенной строки
     */
    public void onRowSelect(SelectEvent<SysProp> event) {
        LOGGER.info("select prop: " + event.getObject());

        disableRemoveBtn = false;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }

    public List<SysProp> getProps() {
        return props;
    }

    public SysProp getSelectedProp() {
        return selectedProp;
    }

    public void setSelectedProp(SysProp selectedProp) {
        this.selectedProp = selectedProp;
    }

    public List<PropValType> getPropValTypes() {
        return propValTypes;
    }

    public List<Measure> getMeasures() {
        return measures;
    }
}
