package ru.tecon.admTools.specificModel.cdi;

import ru.tecon.admTools.specificModel.ejb.SpecificModelLocal;
import ru.tecon.admTools.specificModel.model.GraphDecreaseDescription;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для отображения значений графика или суточного снижения
 */
@Named("graphDecreaseDataShow")
@RequestScoped
public class GraphDecreaseDataShowMB {

    private String name;

    private List<GraphDecreaseDescription> tableData = new ArrayList<>();

    @EJB
    private SpecificModelLocal bean;

    public void loadGraphData(int id, String name) {
        this.name = "Температурный график " + name;
        tableData = bean.getGraphDescription(id);
    }

    public void loadDecreaseData(int id, String name) {
        this.name = "Суточное снижение " + name;
        tableData = bean.getDecreaseDescription(id);
    }

    public List<GraphDecreaseDescription> getTableData() {
        return tableData;
    }

    public String getName() {
        return name;
    }
}
