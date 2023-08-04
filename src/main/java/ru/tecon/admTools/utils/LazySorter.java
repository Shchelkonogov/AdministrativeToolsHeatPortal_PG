package ru.tecon.admTools.utils;

import org.primefaces.model.SortOrder;

import java.util.Comparator;

/**
 * @author Maksim Shchelkonogov
 */
public class LazySorter<T> implements Comparator<T> {

    private final String sortField;

    private final SortOrder sortOrder;

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Object prop1, Object prop2) {
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
