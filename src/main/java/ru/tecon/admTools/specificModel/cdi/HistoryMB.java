package ru.tecon.admTools.specificModel.cdi;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import ru.tecon.admTools.specificModel.ejb.SpecificModelLocal;
import ru.tecon.admTools.specificModel.model.ParamHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для отображения истории изменения параметра
 */
@Named("history")
@RequestScoped
public class HistoryMB {

    private String name;
    private List<ParamHistory> tableData = new ArrayList<>();

    @EJB
    private SpecificModelLocal bean;

    public void loadData(String parName, int objectID, int parID, int statAgrID) {
        this.name = parName;
        tableData = bean.getParamHistory(objectID, parID, statAgrID);
    }

    public String getName() {
        return name;
    }

    public List<ParamHistory> getTableData() {
        return tableData;
    }
}
