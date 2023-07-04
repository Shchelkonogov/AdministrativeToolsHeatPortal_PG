package ru.tecon.admTools.systemParams.model.genModel;

import ru.tecon.admTools.systemParams.model.Measure;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Класс описывающий дерево обобщенной модели
 *
 * @author Aleksey Sergeev
 */
public class GMTree implements Serializable {

    private String id;
    private String name;
    private String parent;
    private long myId;
    private String myType;
    private String categ;
    private Measure measure;
    private boolean visStat;
    private String icon;

    public GMTree() {
    }

    public GMTree(String id, String name, String parent, long myId, String myType, String categ, boolean visStat) {
        this();
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.myId = myId;
        this.myType = myType;
        this.categ = categ;
        this.visStat = visStat;

        switch (myType) {
            case "SA":
                icon = "pi pi-check";
                break;
            case "PP":
                icon = "pi pi-cog";
                break;
            default:
                icon = "pi pi-folder";
                break;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public long getMyId() {
        return myId;
    }

    public String getMyType() {
        return myType;
    }

    public String getCateg() {
        return categ;
    }

    public boolean isVisStat() {
        return visStat;
    }

    public void setVisStat(boolean visStat) {
        this.visStat = visStat;
    }

    public int getVisStat() {
        return visStat ? 1 : 0;
    }

    public String getIcon() {
        return icon;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getMeasureShortNameWithSupTag() {
        return measure != null ? measure.getShortNameWithSupTag() : "";
    }

    public Integer getMeasureId() {
        return measure != null ? measure.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GMTree gmTree = (GMTree) o;
        return myId == gmTree.myId && visStat == gmTree.visStat && Objects.equals(id, gmTree.id) && Objects.equals(name, gmTree.name) && Objects.equals(parent, gmTree.parent) && Objects.equals(myType, gmTree.myType) && Objects.equals(categ, gmTree.categ) && Objects.equals(measure, gmTree.measure) && Objects.equals(icon, gmTree.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parent, myId, myType, categ, measure, visStat, icon);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GMTree.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("parent='" + parent + "'")
                .add("myId=" + myId)
                .add("myType='" + myType + "'")
                .add("categ='" + categ + "'")
                .add("measure=" + measure)
                .add("visStat=" + visStat)
                .add("icon='" + icon + "'")
                .toString();
    }
}
