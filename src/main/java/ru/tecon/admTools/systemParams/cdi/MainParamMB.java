package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.DefaultValuesSB;
import ru.tecon.admTools.systemParams.ejb.MainParamSB;
import ru.tecon.admTools.systemParams.model.ObjectType;
import ru.tecon.admTools.systemParams.model.mainParam.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Контроллер для формы основные параметры
 * @author Aleksey Sergeev
 */
@Named("mainParamMB")
@ViewScoped
public class MainParamMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MainParamMB.class.getName());

    private ObjectType leftOneLine;
    private List<ObjectType> leftpartSelectOneMenuParam = new LinkedList<>();

    private TechProc adaptRightPartSelectOneMenu;
    private TechProc all = new TechProc(0,0,"Все","Все");
    private TechProc adaptDialogRightPartSelectOneMenu;
    private List<TechProc> rightPartSelectOneMenuParam = new LinkedList<>();
    private List<TechProc> techProcInDialogList=new LinkedList<>();

    private List<MPTable> tableParam = new ArrayList<>();
    private MPTable selectedPartInTable;

    private List<TechProcParam> parametrsOfTechProcsList = new ArrayList<>();
    private TechProcParam adaptParametrsOfTechProc;


    private String login;
    private String ip;


    private boolean disableRemoveBtn = true;
    private boolean write=false;
    private boolean techProcParamSOM=true;
    private boolean techProcParamString=false;

    @EJB
    MainParamSB allDao;
    @EJB
    DefaultValuesSB defaultValuesBean;



    @PostConstruct
    private void init() {

        leftpartSelectOneMenuParam = defaultValuesBean.getObjectTypes();

        try {
            int defaultObjectTypeID = defaultValuesBean.getDefaultObjectTypeID();

            ObjectType defaultObjectType = leftpartSelectOneMenuParam.stream()
                    .filter(objectType -> objectType.getId() == defaultObjectTypeID)
                    .findFirst()
                    .orElseThrow(SystemParamException::new);

            leftpartSelectOneMenuParam.remove(defaultObjectType);
            leftpartSelectOneMenuParam.add(0, defaultObjectType);
        } catch (SystemParamException e) {
            LOGGER.log(Level.WARNING, "error load default object type", e);

        }

        leftOneLine=leftpartSelectOneMenuParam.get(0);
        rightPartSelectOneMenuParam=allDao.getRightpartSelectOneMenuParam(leftOneLine.getId());
        rightPartSelectOneMenuParam.add(0, all);
        techProcInDialogList=allDao.getRightpartSelectOneMenuParam(leftOneLine.getId());
        adaptDialogRightPartSelectOneMenu=techProcInDialogList.get(0);

        adaptRightPartSelectOneMenu=rightPartSelectOneMenuParam.get(0);
        tableParam = allDao.getTabeParam(leftpartSelectOneMenuParam.get(0).getId(),0);
        parametrsOfTechProcsList = allDao.getParametrsOfTechProc(adaptDialogRightPartSelectOneMenu.getId());


        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");
    }

    /**
     * Обработчик изменения списка техпроцессов
     */
    public void rightPartListUpdateAfterEvent(){

        disableRemoveBtn=true;
        rightPartSelectOneMenuParam=allDao.getRightpartSelectOneMenuParam(leftOneLine.getId());
        rightPartSelectOneMenuParam.add(0,all);
        techProcInDialogList=allDao.getRightpartSelectOneMenuParam(leftOneLine.getId());
        adaptRightPartSelectOneMenu=rightPartSelectOneMenuParam.get(0);
        tableParam=allDao.getTabeParam(leftOneLine.getId(), rightPartSelectOneMenuParam.get(0).getId());
        parametrsOfTechProcsList=allDao.getParametrsOfTechProc(techProcInDialogList.get(0).getId());

        if(adaptRightPartSelectOneMenu.getId() == 0){
            techProcParamSOM=true;
            techProcParamString=false;
        } if (!(adaptRightPartSelectOneMenu.getId() == 0)) {
            techProcParamSOM=false;
            techProcParamString=true;
        }
    }

    /**
     * Обработчик изменения списка параметров техпроцессов
     */
    public void techProcsUpdateAfterEvent(){
        if (adaptRightPartSelectOneMenu.getId()!=0) {
            parametrsOfTechProcsList = allDao.getParametrsOfTechProc(adaptRightPartSelectOneMenu.getId());
        }else {
            parametrsOfTechProcsList = allDao.getParametrsOfTechProc(adaptDialogRightPartSelectOneMenu.getId());
        }
        write = true;

        if(adaptRightPartSelectOneMenu.getId() == 0){
            techProcParamSOM=true;
            techProcParamString=false;
        } if (!(adaptRightPartSelectOneMenu.getId() == 0)) {
            techProcParamSOM=false;
            techProcParamString=true;
        }
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
            allDao.removeParamFromTable(selectedPartInTable.getObjid(), selectedPartInTable.getTechprid(), selectedPartInTable.getPartypeid(), selectedPartInTable.getId(), login, ip);

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

        if (techProcParamString) {
            try {
                allDao.addParamAtTable(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId(), adaptParametrsOfTechProc.getPartypeid(), adaptParametrsOfTechProc.getTechprid(), login, ip);
            } catch (SystemParamException e) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            }
            tableParam = allDao.getTabeParam(leftOneLine.getId(), adaptRightPartSelectOneMenu.getId());
        }else {
            try {
                allDao.addParamAtTable(leftOneLine.getId(), adaptDialogRightPartSelectOneMenu.getId(), adaptParametrsOfTechProc.getPartypeid(), adaptParametrsOfTechProc.getTechprid(), login, ip);
            } catch (SystemParamException e) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            }
            tableParam=allDao.getTabeParam(leftOneLine.getId(), adaptDialogRightPartSelectOneMenu.getId());
        }
    }

    public List<ObjectType> getLeftpartSelectOneMenuParam() {
        return leftpartSelectOneMenuParam;
    }

    public void setLeftpartSelectOneMenuParam(List<ObjectType> leftpartSelectOneMenuParam) {
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

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setDisableRemoveBtn(boolean disableRemoveBtn) {
        this.disableRemoveBtn = disableRemoveBtn;
    }

    public boolean isTechProcParamSOM() {
        return techProcParamSOM;
    }

    public void setTechProcParamSOM(boolean techProcParamSOM) {
        this.techProcParamSOM = techProcParamSOM;
    }

    public boolean isTechProcParamString() {
        return techProcParamString;
    }

    public void setTechProcParamString(boolean techProcParamString) {
        this.techProcParamString = techProcParamString;
    }

    public List<TechProc> getTechProcInDialogList() {
        return techProcInDialogList;
    }

    public void setTechProcInDialogList(LinkedList<TechProc> techProcInDialogList) {
        this.techProcInDialogList = techProcInDialogList;
    }

    public TechProc getAdaptDialogRightPartSelectOneMenu() {
        return adaptDialogRightPartSelectOneMenu;
    }

    public void setAdaptDialogRightPartSelectOneMenu(TechProc adaptDialogRightPartSelectOneMenu) {
        this.adaptDialogRightPartSelectOneMenu = adaptDialogRightPartSelectOneMenu;
    }
}
