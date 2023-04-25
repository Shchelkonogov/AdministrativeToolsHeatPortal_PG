package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий дерево обобщенной модели
 * @author Aleksey Sergeev
 */
public class GMTree implements Serializable {
    private String id;
    private String name;
    private String parent;
    private long myId;
    private String myType;
    private String categ;
    private short measureId;
    private String measureName;
    private boolean visStat;
    private String icon;

    public GMTree() {
    }

    public GMTree(String id, String name, String parent, long myId, String myType, String categ, short measureId, String measureName, boolean visStat) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.myId = myId;
        this.myType = myType;
        this.categ = categ;
        this.measureId = measureId;
        this.measureName = measureName;
        this.visStat = visStat;
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

    public short getMeasureId() {
        return measureId;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public boolean isVisStat() {
        return visStat;
    }

    public void setVisStat(boolean visStat) {
        this.visStat = visStat;
    }

    public Short getVisStatShort(){
        if (visStat){
            return 1;
        } else {
            return 0;
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
                .add("measureId=" + measureId)
                .add("measureName='" + measureName + "'")
                .add("visStat=" + visStat)
                .add("icon='" + icon + "'")
                .toString();
    }
}
