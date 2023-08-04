package ru.tecon.admTools.linker.model;

import org.apache.commons.collections4.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;
import ru.tecon.admTools.utils.AdmTools;
import ru.tecon.admTools.utils.LazySorter;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.beans.IntrospectionException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Ленивая загрузка таблиц для формы "Линковщик"
 *
 * @author Maksim Shchelkonogov
 * 10.07.2023
 */
public class LazyLinkedDataModel<T extends LazyData> extends LazyDataModel<T> {

    private List<T> data = new ArrayList<>();
    private Map<String, SortOrder> customSorting = new HashMap<>();

    public LazyLinkedDataModel(Converter converter, List<T> data) {
        super(converter);
        this.setData(data);
    }

    public LazyLinkedDataModel(Converter converter, List<T> data, Map<String, SortOrder> customSorting) {
        this(converter, data);
        this.customSorting = customSorting;
    }

    @Override
    public String getRowKey(T object) {
        return object.getId();
    }

    @Override
    public T getRowData(String rowKey) {
        return data.stream().filter(t -> t.getId().equals(rowKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) data.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();
    }

    @Override
    public List<T> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // apply offset & filters
        List<T> result = data.stream()
                .skip(offset)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<T>> comparators = sortBy.values().stream()
                    .map(o -> new LazySorter<T>(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());

            Comparator<T> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            result.sort(cp);
        }

        return result;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, java.lang.Object o) {
        boolean matching = true;

        for (FilterMeta filter: filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            java.lang.Object filterValue = filter.getFilterValue();

            try {
                java.lang.Object columnValue = String.valueOf(AdmTools.getPropertyValueViaReflection(o, filter.getField()));
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
        // custom sort
        if (!customSorting.isEmpty()) {
            List<Comparator<T>> comparators = customSorting.entrySet().stream()
                    .map(entry -> new LazySorter<T>(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            Comparator<T> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            this.data.sort(cp);
        }
    }
}
