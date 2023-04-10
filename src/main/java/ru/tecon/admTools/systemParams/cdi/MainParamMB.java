package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.DefaultValuesSB;
import ru.tecon.admTools.systemParams.ejb.MainParamSB;
import ru.tecon.admTools.systemParams.model.ObjectType;
import ru.tecon.admTools.systemParams.model.mainParam.MPTable;
import ru.tecon.admTools.systemParams.model.mainParam.TechProc;
import ru.tecon.admTools.systemParams.model.mainParam.TechProcParam;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Контроллер для формы основные параметры
 *
 * @author Aleksey Sergeev
 */
@Named("mainParamMB")
@ViewScoped
public class MainParamMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MainParamMB.class.getName());

    private ObjectType leftOneLine;
    private List<ObjectType> leftPartSelectOneMenuParam = new LinkedList<>();

    private TechProc adaptRightPartSelectOneMenu;
    private final TechProc all = new TechProc(0, 0, "Все", "Все");
    private List<TechProc> rightPartSelectOneMenuParam = new LinkedList<>();

    private List<MPTable> tableParam = new ArrayList<>();
    private MPTable selectedPartInTable;

    private List<TechProcParam> paramsOfTechProcessList = new ArrayList<>();
    private TechProcParam adaptParamsOfTechProc;

    private boolean disableRemoveBtn = true;
    private boolean techProcParamString = false;

    @EJB
    MainParamSB allDao;
    @EJB
    DefaultValuesSB defaultValuesBean;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        leftPartSelectOneMenuParam = defaultValuesBean.getObjectTypes();

        try {
            int defaultObjectTypeID = defaultValuesBean.getDefaultObjectTypeID();

            ObjectType defaultObjectType = leftPartSelectOneMenuParam.stream()
                    .filter(objectType -> objectType.getId() == defaultObjectTypeID)
                    .findFirst()
                    .orElseThrow(SystemParamException::new);

            leftPartSelectOneMenuParam.remove(defaultObjectType);
            leftPartSelectOneMenuParam.add(0, defaultObjectType);
        } catch (SystemParamException e) {
            LOGGER.log(Level.WARNING, "error load object type", e);

        }

        leftOneLine = leftPartSelectOneMenuParam.get(0);
        rightPartSelectOneMenuParam = allDao.getRightPartSelectOneMenuParam(leftOneLine.getId());
        adaptRightPartSelectOneMenu = all;
        tableParam = allDao.getTableParam(leftOneLine.getId(), 0);
    }

    /**
     * Обработчик изменения списка техпроцессов
     */
    public void rightPartListUpdateAfterEvent(final AjaxBehaviorEvent event) {
        LOGGER.info("rightPartListUpdateAfterEvent " + event);

        tableParam = allDao.getTableParam(leftOneLine.getId(), 0);
        rightPartSelectOneMenuParam = allDao.getRightPartSelectOneMenuParam(leftOneLine.getId());
        adaptRightPartSelectOneMenu = all;

        if (adaptRightPartSelectOneMenu.getId() == 0) {
            techProcParamString = false;
        }
        if (adaptRightPartSelectOneMenu != null && adaptRightPartSelectOneMenu.getId() != 0) {
            techProcParamString = true;
        }
    }

    /**
     * Обработчик изменения списка параметров техпроцессов отображаемых в таблице
     */
    public void tableUpdateAfterEvent(final AjaxBehaviorEvent event) {
        LOGGER.info("tableUpdateAfterEvent: " + event);

        if (adaptRightPartSelectOneMenu != null && adaptRightPartSelectOneMenu.getId() != 0) {
            tableParam = allDao.getTableParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
            techProcParamString = true;
        } else {
            tableParam = allDao.getTableParam(leftOneLine.getId(), 0);
            adaptRightPartSelectOneMenu = all;
            techProcParamString = false;
        }
    }

    /**
     * Обработчик изменения списка параметров техпроцессов
     */
    public void techProcessUpdateAfterEvent() {
        if (adaptRightPartSelectOneMenu == null) {
            adaptRightPartSelectOneMenu = rightPartSelectOneMenuParam.get(0);
            paramsOfTechProcessList = allDao.getParamsOfTechProc(adaptRightPartSelectOneMenu.getId());
            adaptParamsOfTechProc = paramsOfTechProcessList.get(0);
        } else {
            paramsOfTechProcessList = allDao.getParamsOfTechProc(adaptRightPartSelectOneMenu.getId());
        }
    }

    /**
     * Обработчик события выделения строки
     *
     * @param event событие
     */
    public void onRowSelect(SelectEvent<MPTable> event) {
        LOGGER.info("select link: " + event.getObject());
        disableRemoveBtn = false;
    }

    /**
     * Обработчик удаления параметра техпроцесса из таблицы, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveTableParam() {
        LOGGER.info("remove param: " + selectedPartInTable);

        try {
            allDao.removeParamFromTable(selectedPartInTable.getObjid(), selectedPartInTable.getTechprid(),
                    selectedPartInTable.getPartypeid(), selectedPartInTable.getId(), utilMB.getLogin(), utilMB.getIp());

            selectedPartInTable = null;
            disableRemoveBtn = true;

            if (adaptRightPartSelectOneMenu != null) {
                tableParam = allDao.getTableParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
            } else {
                tableParam = allDao.getTableParam(leftOneLine.getId(), 0);
            }

        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик появления уведомления об успешном удалении, возникает при нажатии на кнопку удалить (-) после завершения ее работы
     */
    public void addDelMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

    /**
     * Обработчик вида и содержимого появленяющегося уведомления об успешном удалении,
     * возникает при нажатии на кнопку удалить (-) после завершения ее работы
     */
    public void showError() {
        addDelMessage(FacesMessage.SEVERITY_INFO, "Удалено", "Успешное удаление");
    }

    /**
     * Обработчик добавления нового параметра в таблицу, нажатие на копку добавить параметр техпроцесса (+)
     */
    public void onAddNew() {
        tableParam.add(new MPTable());
    }

    /**
     * Обработчик сохранения изменения нового параметра в таблицу, нажатие на копку сохранить
     */

    public void onSaveChanges() {
        try {
            allDao.addParamAtTable(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId(),
                    adaptParamsOfTechProc.getPartypeid(), adaptParamsOfTechProc.getTechprid(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
        if (techProcParamString) {
            tableParam = allDao.getTableParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
        } else {
            tableParam = allDao.getTableParam(leftOneLine.getId(), 0);
        }
    }

    public List<ObjectType> getLeftPartSelectOneMenuParam() {
        return leftPartSelectOneMenuParam;
    }

    public void setLeftPartSelectOneMenuParam(List<ObjectType> leftPartSelectOneMenuParam) {
        this.leftPartSelectOneMenuParam = leftPartSelectOneMenuParam;
    }

    public List<TechProc> getRightPartSelectOneMenuParam() {
        return rightPartSelectOneMenuParam;
    }

    public void setRightPartSelectOneMenuParam(List<TechProc> rightPartSelectOneMenuParam) {
        this.rightPartSelectOneMenuParam = rightPartSelectOneMenuParam;
    }

    public List<MPTable> getTableParam() {
        return tableParam;
    }

    public void setTableParam(List<MPTable> tableParam) {
        this.tableParam = tableParam;
    }

    public ObjectType getLeftOneLine() {
        return leftOneLine;
    }

    public void setLeftOneLine(ObjectType leftOneLine) {
        this.leftOneLine = leftOneLine;
    }

    public TechProc getAdaptRightPartSelectOneMenu() {
        return adaptRightPartSelectOneMenu;
    }

    public void setAdaptRightPartSelectOneMenu(TechProc adaptRightPartSelectOneMenu) {
        this.adaptRightPartSelectOneMenu = adaptRightPartSelectOneMenu;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }

    public MPTable getSelectedPartInTable() {
        return selectedPartInTable;
    }

    public void setSelectedPartInTable(MPTable selectedPartInTable) {
        this.selectedPartInTable = selectedPartInTable;
    }

    public List<TechProcParam> getParamsOfTechProcessList() {
        return paramsOfTechProcessList;
    }

    public void setParamsOfTechProcessList(List<TechProcParam> paramsOfTechProcessList) {
        this.paramsOfTechProcessList = paramsOfTechProcessList;
    }

    public TechProcParam getAdaptParamsOfTechProc() {
        return adaptParamsOfTechProc;
    }

    public void setAdaptParamsOfTechProc(TechProcParam adaptParamsOfTechProc) {
        this.adaptParamsOfTechProc = adaptParamsOfTechProc;
    }

    public void setDisableRemoveBtn(boolean disableRemoveBtn) {
        this.disableRemoveBtn = disableRemoveBtn;
    }

    public boolean isTechProcParamString() {
        return techProcParamString;
    }

    public void setTechProcParamString(boolean techProcParamString) {
        this.techProcParamString = techProcParamString;
    }

    public TechProc getAll() {
        return all;
    }
}
