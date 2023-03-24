package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.MainParamSB;
import ru.tecon.admTools.systemParams.model.mainParam.MPTable;
import ru.tecon.admTools.systemParams.model.mainParam.ObjType;
import ru.tecon.admTools.systemParams.model.mainParam.TechProc;
import ru.tecon.admTools.systemParams.model.mainParam.TechProcParam;

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
 * Контроллер для формы основные параметры
 * @author Aleksey Sergeev
 */
@Named("mainParamMB")
@ViewScoped
public class MainParamMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MainParamMB.class.getName());

    private ObjType leftOneLine;
    private List<ObjType> leftpartSelectOneMenuParam = new ArrayList<>();

    private TechProc adaptRightPartSelectOneMenu;
    private List<TechProc> rightPartSelectOneMenuParam = new ArrayList<>();

    private List<MPTable> tableParam = new ArrayList<>();
    private MPTable selectedPartInTable;

    private List<TechProcParam> parametrsOfTechProcsList = new ArrayList<>();
    private TechProcParam adaptParametrsOfTechProc;

    private final TechProc noSelectItem = new TechProc("Отсутствуют связанные техпроцессы");

    private boolean disableAddBtn=true;
    private boolean disableRemoveBtn = true;

    @EJB
    MainParamSB allDao;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        leftpartSelectOneMenuParam = allDao.getLeftpartSelectOneMenuParam();

        if (!leftpartSelectOneMenuParam.isEmpty()) {
            rightPartSelectOneMenuParam = allDao.getRightpartSelectOneMenuParam(leftpartSelectOneMenuParam.get(0).getId());
            System.out.println(leftpartSelectOneMenuParam.get(0));
            if (!rightPartSelectOneMenuParam.isEmpty()) {
                tableParam = allDao.getTabeParam(leftpartSelectOneMenuParam.get(0).getId(), rightPartSelectOneMenuParam.get(0).getId());
                if (!rightPartSelectOneMenuParam.isEmpty()) {
                    parametrsOfTechProcsList = allDao.getParametrsOfTechProc(rightPartSelectOneMenuParam.get(0).getId());
                }
            }
        }
    }

    /**
     * Обработчик изменения списка техпроцессов
     */
    public void rightPartListUpdateAfterEvent(){
        rightPartSelectOneMenuParam.clear();
        tableParam.clear();
        disableAddBtn=true;
        disableRemoveBtn=true;
        rightPartSelectOneMenuParam=allDao.getRightpartSelectOneMenuParam(leftOneLine.getId());
        if (!rightPartSelectOneMenuParam.isEmpty()) {

            tableParam=allDao.getTabeParam(leftOneLine.getId(), rightPartSelectOneMenuParam.get(0).getId());
            parametrsOfTechProcsList=allDao.getParametrsOfTechProc(rightPartSelectOneMenuParam.get(0).getId());
            disableAddBtn=false;
        }

    }

    /**
     * Обработчик изменения списка параметров техпроцессов
     */
    public void techProcsUpdateAfterEvent(){
        parametrsOfTechProcsList = allDao.getParametrsOfTechProc(adaptRightPartSelectOneMenu.getId());
    }

    /**
     * Обработчик изменения списка параметров техпроцессов отображаемых в таблице
     */
    public void tableUpdateAfterEvent(){
        if (leftOneLine != null) {
            tableParam=allDao.getTabeParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
        }
    }

    /**
     * Обработчик события выделения строки
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

            tableParam=allDao.getTabeParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик появления уведомления об успешном удалении, возникает при нажатии на кнопку удалить (-) после завершения ее работы
     */
    public void addDelMesssage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

    /**
     * Обработчик вида и содержимого появленяющегося уведомления об успешном удалении, возникает при нажатии на кнопку удалить (-) после завершения ее работы
     */
    public void showError() {
        addDelMesssage(FacesMessage.SEVERITY_INFO, "Удалено", "Успешное удаление");
    }

    /**
     * Обработчик добавления нового параметра в таблицу, нажатие на копку добавить параметр техпроцесса (+)
     */
    public void onAddNew(){
        tableParam.add(new MPTable());
    }

    /**
     * Обработчик сохранения изменения нового параметра в таблицу, нажатие на копку сохранить
     */

    public void onSaveChanges() {

        try {
            allDao.addParamAtTable(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId(),
                    adaptParametrsOfTechProc.getPartypeid(), adaptParametrsOfTechProc.getTechprid(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
        tableParam=allDao.getTabeParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
    }

    public List<ObjType> getLeftpartSelectOneMenuParam() {
        return leftpartSelectOneMenuParam;
    }

    public void setLeftpartSelectOneMenuParam(List<ObjType> leftpartSelectOneMenuParam) {
        this.leftpartSelectOneMenuParam = leftpartSelectOneMenuParam;
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

    public ObjType getLeftOneLine() {
        return leftOneLine;
    }

    public void setLeftOneLine(ObjType leftOneLine) {
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

    public List<TechProcParam> getParametrsOfTechProcsList() {
        return parametrsOfTechProcsList;
    }

    public void setParametrsOfTechProcsList(List<TechProcParam> parametrsOfTechProcsList) {
        this.parametrsOfTechProcsList = parametrsOfTechProcsList;
    }

    public TechProcParam getAdaptParametrsOfTechProc() {
        return adaptParametrsOfTechProc;
    }

    public void setAdaptParametrsOfTechProc(TechProcParam adaptParametrsOfTechProc) {
        this.adaptParametrsOfTechProc = adaptParametrsOfTechProc;
    }

    public boolean isDisableAddBtn() {
        return disableAddBtn;
    }


    public TechProc getNoSelectItem() {
        return noSelectItem;
    }
}
