package ru.tecon.admTools.systemParams.model.catalog;

import org.primefaces.model.SortOrder;
import ru.tecon.admTools.utils.AdmTools;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * @author Maksim Shchelkonogov
 */
public class LazySorter implements Comparator<CatalogProp> {

    private final String sortField;

    private final SortOrder sortOrder;

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(CatalogProp prop1, CatalogProp prop2) {
        try {
            Object value1 = AdmTools.getPropertyValueViaReflection(prop1, sortField);
            Object value2 = AdmTools.getPropertyValueViaReflection(prop2, sortField);

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
