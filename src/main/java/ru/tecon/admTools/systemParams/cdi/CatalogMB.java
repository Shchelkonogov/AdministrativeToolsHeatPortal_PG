package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.CatalogSB;
import ru.tecon.admTools.systemParams.model.catalog.CatalogProp;
import ru.tecon.admTools.systemParams.model.catalog.CatalogType;
import ru.tecon.admTools.systemParams.model.catalog.LazyCustomDataModel;

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
 * Контроллер для формы Справочники
 * @author Maksim Shchelkonogov
 */
@Named("catalog")
@ViewScoped
public class CatalogMB implements Serializable, AutoUpdate {

    private static final Logger LOGGER = Logger.getLogger(CatalogMB.class.getName());

    private List<CatalogType> catalogTypes = new ArrayList<>();
    private LazyDataModel<CatalogProp> catalogProps = new LazyCustomDataModel();
    private CatalogType selectedCatalogType;
    private CatalogProp selectedCatalogProp;

    private CatalogType newCatalogType = new CatalogType();
    private String newCatalogPropName;

    private boolean disableRemoveCatalogBtn = true;
    private boolean disableRemovePropBtn = true;

    @EJB
    private CatalogSB catalogBean;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        loadData();

        PrimeFaces.current().executeScript("PF('catalogTypeWidget').clearFilters();");
        PrimeFaces.current().executeScript("PF('catalogTypeWidget').unselectAllRows();");

        PrimeFaces.current().executeScript("PF('catalogPropWidget').clearFilters();");
        PrimeFaces.current().executeScript("PF('catalogPropWidget').unselectAllRows();");
    }

    /**
     * Метод загружает данные для формы
     */
    private void loadData() {
        catalogTypes = catalogBean.getCatalogTypes();

        catalogProps = new LazyCustomDataModel();
        selectedCatalogType = null;
        selectedCatalogProp = null;
        disableRemoveCatalogBtn = true;
        disableRemovePropBtn = true;
    }

    /**
     * Обработчик нажатия кнопки удалить справочник
     */
    public void onRemoveCatalog() {
        LOGGER.info("remove catalog: " + selectedCatalogType);

        try {
            catalogBean.removeCatalog(selectedCatalogType, utilMB.getLogin(), utilMB.getIp());

            loadData();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * обработчик выбора справочника
     * @param event событие выбора
     */
    public void onCatalogTypeSelect(SelectEvent<CatalogType> event) {
        LOGGER.info("select catalog type: " + event.getObject() + " " + selectedCatalogType);

        catalogBean.loadCatalogProps(selectedCatalogType);

        LOGGER.info("load " + selectedCatalogType.getCatalogProps().size() + " prop for catalog: " + selectedCatalogType);

        catalogProps = new LazyCustomDataModel(selectedCatalogType.getCatalogProps());
        disableRemoveCatalogBtn = false;

        selectedCatalogProp = null;
        disableRemovePropBtn = true;

        // Заглушка с расчетом размера scroll, когда используется virtualScroll он как-то странно рассчитывается
        if (selectedCatalogType.getCatalogProps().size() > 10) {
            PrimeFaces.current().executeScript("$('#catalogPanel\\\\:catalogPropPanel_content .ui-datatable-virtualscroll-wrapper').css('height', " + selectedCatalogType.getCatalogProps().size() * 31.7 + ");");
        }
    }

    /**
     * обработчик удаления значения справочника
     */
    public void onRemoveProp() {
        LOGGER.info("remove catalog property: " + selectedCatalogProp);

        try {
            catalogBean.removeCatalogProp(selectedCatalogType.getId(), selectedCatalogProp, utilMB.getLogin(), utilMB.getIp());

            selectedCatalogProp = null;
            disableRemovePropBtn = true;

            catalogBean.loadCatalogProps(selectedCatalogType);
            catalogProps = new LazyCustomDataModel(selectedCatalogType.getCatalogProps());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик выбора значения справочника
     * @param event событие выбора
     */
    public void onCatalogPropSelect(SelectEvent<CatalogProp> event) {
        LOGGER.info("select catalog property: " + event.getObject());

        disableRemovePropBtn = false;
    }

    /**
     * Обработчик заурытия всплывающего окна добавить значение справочника
     */
    public void onAddPropDialogClose() {
        newCatalogPropName = null;
    }

    public void savePropWrapper() {
        PrimeFaces.current().executeScript("savePropWrapper();");
    }

    public void saveStructWrapper() {
        PrimeFaces.current().executeScript("saveTypeWrapper();");
    }

    /**
     * обработчик создания нового значения справочника
     */
    public void onCreateCatalogProp() {
        LOGGER.info("create new prop property: " + newCatalogPropName + " for struct: " + selectedCatalogType);

        try {
            catalogBean.addCatalogProp(selectedCatalogType.getId(), newCatalogPropName, utilMB.getLogin(), utilMB.getIp());

            catalogBean.loadCatalogProps(selectedCatalogType);

            catalogProps = new LazyCustomDataModel(selectedCatalogType.getCatalogProps());
            selectedCatalogProp = null;
            disableRemovePropBtn = true;

            PrimeFaces.current().executeScript("PF('addPropDialog').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * Обработчик создания нового справочника
     */
    public void onCreateCatalogType() {
        LOGGER.info("create new catalog: " + newCatalogType + " and catalog property: " + newCatalogType.getCatalogProps());

        FacesContext context = FacesContext.getCurrentInstance();

        try {
            int structID = catalogBean.createCatalogType(newCatalogType.getTypeName(), utilMB.getLogin(), utilMB.getIp());

            if (!newCatalogType.getCatalogProps().isEmpty()) {
                newCatalogType.getCatalogProps().removeIf(CatalogProp::check);

                for (CatalogProp prop: newCatalogType.getCatalogProps()) {
                    try {
                        catalogBean.addCatalogProp(structID, prop.getPropName(), utilMB.getLogin(), utilMB.getIp());
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

    public void onRowEditCancel(RowEditEvent<CatalogProp> event) {
        newCatalogType.getCatalogProps().remove(event.getObject());
    }

    public void onAddTypeDialogClose() {
        newCatalogType = new CatalogType();
    }

    public void onAddNew() {
        newCatalogType.getCatalogProps().add(new CatalogProp());
    }

    public List<CatalogType> getCatalogTypes() {
        return catalogTypes;
    }

    public CatalogType getSelectedCatalogType() {
        return selectedCatalogType;
    }

    public void setSelectedCatalogType(CatalogType selectedCatalogType) {
        this.selectedCatalogType = selectedCatalogType;
    }

    public CatalogProp getSelectedCatalogProp() {
        return selectedCatalogProp;
    }

    public void setSelectedCatalogProp(CatalogProp selectedCatalogProp) {
        this.selectedCatalogProp = selectedCatalogProp;
    }

    public boolean isDisableRemovePropBtn() {
        return disableRemovePropBtn;
    }

    public LazyDataModel<CatalogProp> getCatalogProps() {
        return catalogProps;
    }

    public void setCatalogProps(LazyDataModel<CatalogProp> catalogProps) {
        this.catalogProps = catalogProps;
    }

    public boolean isDisableRemoveCatalogBtn() {
        return disableRemoveCatalogBtn;
    }

    public String getNewCatalogPropName() {
        return newCatalogPropName;
    }

    public void setNewCatalogPropName(String newCatalogPropName) {
        this.newCatalogPropName = newCatalogPropName;
    }

    public CatalogType getNewCatalogType() {
        return newCatalogType;
    }

    public void setNewCatalogType(CatalogType newCatalogType) {
        this.newCatalogType = newCatalogType;
    }
}
