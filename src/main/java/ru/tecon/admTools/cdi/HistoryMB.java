package ru.tecon.admTools.cdi;

import ru.tecon.admTools.ejb.SpecificModelLocal;
import ru.tecon.admTools.model.ParamHistory;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
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
