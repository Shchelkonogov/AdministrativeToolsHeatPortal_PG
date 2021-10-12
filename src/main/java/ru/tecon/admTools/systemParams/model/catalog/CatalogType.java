package ru.tecon.admTools.systemParams.model.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Класс описывающй справочник
 * @author Maksim Shchelkonogov
 */
public class CatalogType {

    private Integer id;
    private String typeName;
    private List<CatalogProp> catalogProps = new ArrayList<>();

    public CatalogType() {
    }

    public CatalogType(Integer id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<CatalogProp> getCatalogProps() {
        return catalogProps;
    }

    public void setCatalogProps(List<CatalogProp> catalogProps) {
        this.catalogProps = catalogProps;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CatalogType.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("typeName='" + typeName + "'")
                .toString();
    }
}
