package ru.tecon.admTools.systemParams.model.catalog;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Maksim Shchelkonogov
 */
public class LazyCustomDataModel extends LazyDataModel<CatalogProp> {

    private List<CatalogProp> dataSource = new ArrayList<>();

    public LazyCustomDataModel() {
    }

    public LazyCustomDataModel(List<CatalogProp> dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public CatalogProp getRowData(String rowKey) {
        for (CatalogProp prop: dataSource) {
            if (prop.getId() == Long.parseLong(rowKey)) {
                return prop;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(CatalogProp object) {
        return String.valueOf(object.getId());
    }

    @Override
    public List<CatalogProp> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<CatalogProp> data = new ArrayList<>();

        //filter
        for (CatalogProp prop: dataSource) {
            boolean match = true;

            if (filterBy != null) {
                for (FilterMeta meta: filterBy.values()) {
                    try {
                        String filterField = meta.getFilterField();
                        Object filterValue = meta.getFilterValue();

                        Field field = CatalogProp.class.getDeclaredField(filterField);
                        field.setAccessible(true);
                        String fieldValue = String.valueOf(field.get(prop));

                        if ((filterValue == null) || fieldValue.contains(filterValue.toString())) {
                            match = true;
                        } else {
                            match = false;
                            break;
                        }
                    } catch (Exception e) {
                        match = false;
                    }
                }
            }

            if (match) {
                data.add(prop);
            }
        }

        //sort
        if ((sortBy != null) && !sortBy.isEmpty()) {
            for (SortMeta meta: sortBy.values()) {
                data.sort(new LazySorter(meta.getSortField(), meta.getSortOrder()));
            }
        }

        //rowCount
        int dataSize = data.size();
        this.setRowCount(dataSize);

        //paginate
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            }
            catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        }
        else {
            return data;
        }
    }

}
