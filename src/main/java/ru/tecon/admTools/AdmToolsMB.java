package ru.tecon.admTools;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import ru.tecon.admTools.model.Condition;
import ru.tecon.admTools.model.DataModel;
import ru.tecon.admTools.model.GraphDecreaseItemModel;
import ru.tecon.admTools.model.ParamConditionDataModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Named("Controller")
@ViewScoped
public class AdmToolsMB implements Serializable {

    private int objectID;
    private String objectPath;

    private Set<Integer> modelIDToSave = new HashSet<>();

    private List<DataModel> tableModel;
    private List<DataModel> filteredTableModel = new ArrayList<>();
    private Set<String> techProcFilter;

    private List<DataModel> enumerableTableModel;
    private DataModel selectedEnumerateItem;
    private Set<Integer> modelEnumIDToSave = new HashSet<>();

    private List<ParamConditionDataModel> paramConditionDataModel;
    private List<Condition> conditions;

    private List<GraphDecreaseItemModel> decreases;
    private GraphDecreaseItemModel selectDecrease;

    private List<GraphDecreaseItemModel> graphs;
    private GraphDecreaseItemModel selectGraph;
    private String optValue = "";

    private DataModel changedDecreaseGraphItem;
    private int rowIndex;

    @EJB
    private AdmToolsSB bean;

    @PostConstruct
    private void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        try {
            objectID = Integer.valueOf(request.getParameter("objectID"));
        } catch (NumberFormatException e) {
            return;
        }

        objectPath = bean.getObjectPath(objectID);

        loadData();

        conditions = bean.getConditions();
        decreases = bean.getDecreases();
        graphs = bean.getGraphs();

        techProcFilter = tableModel.stream().map(DataModel::getTechProcCode).collect(Collectors.toSet());
    }

    private void loadData() {
        tableModel = bean.getData(objectID);
//        filteredTableModel = tableModel;
        filteredTableModel.clear();
        filteredTableModel.addAll(tableModel);
        enumerableTableModel = bean.getEnumerableData(objectID);
    }

    public List<DataModel> getTableModel() {
        return tableModel;
    }

    public List<DataModel> getEnumerableTableModel() {
        return enumerableTableModel;
    }

    public void onCellEditInit(CellEditEvent event) {
        System.out.println(filteredTableModel.size());
        rowIndex = event.getRowIndex();
        changedDecreaseGraphItem = filteredTableModel.get(rowIndex);
        if (event.getColumn().getClientId().endsWith("graphColumn")) {
            optValue = "";
            PrimeFaces.current().ajax().update("graphSelectForm:gTab");
            PrimeFaces.current().executeScript("PF('graphDialog').show();");
        } else {
            if (event.getColumn().getClientId().endsWith("decreaseColumn")) {
                PrimeFaces.current().executeScript("PF('decreaseDialog').show();");
            }
        }
    }

    public void onCellEdit(CellEditEvent event) {
        modelIDToSave.add(filteredTableModel.get(event.getRowIndex()).getId());
    }

    public String getObjectPath() {
        return objectPath;
    }

    public void saveData() {
        System.out.println(modelIDToSave);

        System.out.println("update analog objects:");
        tableModel.stream().filter(dataModel -> modelIDToSave.contains(dataModel.getId())).forEach(System.out::println);
        System.out.println("update enumerate objects:");
        enumerableTableModel.stream().filter(dataModel -> modelEnumIDToSave.contains(dataModel.getId())).forEach(System.out::println);

        FacesContext context = FacesContext.getCurrentInstance();

        tableModel.stream().filter(dataModel -> modelIDToSave.contains(dataModel.getId())).forEach(dataModel -> {
            System.out.println("save: " + dataModel);
            String message = bean.saveGraph(objectID, dataModel);
            if (message != null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи",  message));
            }
            message = bean.saveRanges(objectID, dataModel);
            if (message != null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи",  message));
            }
        });

        enumerableTableModel.stream().filter(dataModel -> modelEnumIDToSave.contains(dataModel.getId())).forEach(dataModel -> {
            System.out.println("save: " + dataModel);
            bean.saveEnumParam(objectID, dataModel);
        });

        loadData();

        modelIDToSave.clear();
        modelEnumIDToSave.clear();
//
        tableModel.forEach(System.out::println);
//        System.out.println(1);
//        filteredTableModel.forEach(System.out::println);
    }

    public void onBooleanValueChange(int rowIndex) {
        modelIDToSave.add(filteredTableModel.get(rowIndex).getId());
    }

    public DataModel getSelectedEnumerateItem() {
        return selectedEnumerateItem;
    }

    public void setSelectedEnumerateItem(DataModel selectedEnumerateItem) {
        this.selectedEnumerateItem = selectedEnumerateItem;
    }

    public void onRowSelect() {
        paramConditionDataModel = bean.getParamCondition(objectID, selectedEnumerateItem.getParID(), selectedEnumerateItem.getStatAgr());
        selectedEnumerateItem.setParamConditions(paramConditionDataModel);
    }

    public List<ParamConditionDataModel> getParamConditionDataModel() {
        return paramConditionDataModel;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void onCellEnumEdit(CellEditEvent event) {
        selectedEnumerateItem.getParamConditions().get(event.getRowIndex()).setEdited(true);
        modelEnumIDToSave.add(selectedEnumerateItem.getId());
    }

    public List<GraphDecreaseItemModel> getDecreases() {
        return decreases;
    }

    public void saveDecrease() {
        changedDecreaseGraphItem.setDecreaseName(selectDecrease.getName());
        changedDecreaseGraphItem.setDecreaseID(selectDecrease.getId());
        modelIDToSave.add(changedDecreaseGraphItem.getId());
        PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":decreaseValue");
    }

    public GraphDecreaseItemModel getSelectDecrease() {
        return selectDecrease;
    }

    public void setSelectDecrease(GraphDecreaseItemModel selectDecrease) {
        this.selectDecrease = selectDecrease;
    }

    public List<DataModel> getFilteredTableModel() {
        return filteredTableModel;
    }

    public void setFilteredTableModel(List<DataModel> filteredTableModel) {
        this.filteredTableModel = filteredTableModel;
    }

    public List<GraphDecreaseItemModel> getGraphs() {
        return graphs;
    }

    public GraphDecreaseItemModel getSelectGraph() {
        return selectGraph;
    }

    public void setSelectGraph(GraphDecreaseItemModel selectGraph) {
        this.selectGraph = selectGraph;
    }

    public void saveGraph() {
        changedDecreaseGraphItem.setGraphName(selectGraph.getName());
        changedDecreaseGraphItem.setGraphID(selectGraph.getId());
        modelIDToSave.add(changedDecreaseGraphItem.getId());
        PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
    }

    public String gettMax() {
        if (changedDecreaseGraphItem == null) {
            return "-";
        }

        if (changedDecreaseGraphItem.istPercent()) {
            try {
                return new BigDecimal(changedDecreaseGraphItem.gettMax())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN)
                        .add(new BigDecimal(optValue))
                        .setScale(2, RoundingMode.HALF_EVEN).toString();
            } catch (NumberFormatException e) {
                return changedDecreaseGraphItem.gettMax() + "%";
            }
        } else {
            return changedDecreaseGraphItem.gettMax().toString();
        }
    }

    public String gettMin() {
        if (changedDecreaseGraphItem == null) {
            return "-";
        }

        if (changedDecreaseGraphItem.istPercent()) {
            try {
                return new BigDecimal(changedDecreaseGraphItem.gettMin())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN)
                        .add(new BigDecimal(optValue))
                        .setScale(2, RoundingMode.HALF_EVEN).toString();
            } catch (NumberFormatException e) {
                return changedDecreaseGraphItem.gettMin() + "%";
            }
        } else {
            return changedDecreaseGraphItem.gettMin().toString();
        }
    }

    public String getaMax() {
        if (changedDecreaseGraphItem == null) {
            return "-";
        }

        if (changedDecreaseGraphItem.isaPercent()) {
            try {
                return new BigDecimal(changedDecreaseGraphItem.getaMax())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN)
                        .add(new BigDecimal(optValue))
                        .setScale(2, RoundingMode.HALF_EVEN).toString();
            } catch (NumberFormatException e) {
                return changedDecreaseGraphItem.getaMax() + "%";
            }
        } else {
            return changedDecreaseGraphItem.getaMax().toString();
        }
    }

    public String getaMin() {
        if (changedDecreaseGraphItem == null) {
            return "-";
        }

        if (changedDecreaseGraphItem.isaPercent()) {
            try {
                return new BigDecimal(changedDecreaseGraphItem.getaMin())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN)
                        .add(new BigDecimal(optValue))
                        .setScale(2, RoundingMode.HALF_EVEN).toString();
            } catch (NumberFormatException e) {
                return changedDecreaseGraphItem.getaMin() + "%";
            }
        } else {
            return changedDecreaseGraphItem.getaMin().toString();
        }
    }

    public Set<String> getTechProcFilter() {
        return techProcFilter;
    }

    public void setOptValue(String optValue) {
        this.optValue = optValue;
    }

    public String getOptValue() {
        if ((changedDecreaseGraphItem != null) && (changedDecreaseGraphItem.getGraphID() == null)) {
            return changedDecreaseGraphItem.getGraphName();
        }
        return "";
    }

    public void saveOptValue() {
        changedDecreaseGraphItem.setGraphName(optValue);
        changedDecreaseGraphItem.setGraphID(null);
        modelIDToSave.add(changedDecreaseGraphItem.getId());
        PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
    }
}
