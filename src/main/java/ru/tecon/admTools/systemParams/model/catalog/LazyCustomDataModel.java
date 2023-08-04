package ru.tecon.admTools.systemParams.model.catalog;

import org.apache.commons.collections4.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;
import ru.tecon.admTools.utils.AdmTools;
import ru.tecon.admTools.utils.LazySorter;

import javax.faces.context.FacesContext;
import java.beans.IntrospectionException;
import java.util.*;
import java.util.stream.Collectors;

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
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();
    }

    @Override
    public List<CatalogProp> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // apply offset & filters
        List<CatalogProp> catalogProps = dataSource.stream()
                .skip(offset)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<CatalogProp>> comparators = sortBy.values().stream()
                    .map(o -> new LazySorter<CatalogProp>(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<CatalogProp> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            catalogProps.sort(cp);
        }

        return catalogProps;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter: filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();

            try {
                Object columnValue = String.valueOf(AdmTools.getPropertyValueViaReflection(o, filter.getField()));
                matching = constraint.isMatching(context, columnValue, filterValue, LocaleUtils.getCurrentLocale());
            } catch (ReflectiveOperationException | IntrospectionException e) {
                matching = false;
            }

            if (!matching) {
                break;
            }
        }

        return matching;
    }
}
