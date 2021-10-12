package ru.tecon.admTools.systemParams.model.catalog;

import java.util.StringJoiner;

/**
 * Класс описывающий значение справочника
 * @author Maksim Shchelkonogov
 */
public class CatalogProp {

    private static final String DEFAULT_NAME = "Новое значение справочника";

    private long id;
    private String propName = DEFAULT_NAME;

    public CatalogProp() {
    }

    public CatalogProp(long id, String propName) {
        this.id = id;
        this.propName = propName;
    }

    public boolean check() {
        return propName.equals(DEFAULT_NAME);
    }

    public long getId() {
        return id;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CatalogProp.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("propName='" + propName + "'")
                .toString();
    }
}
