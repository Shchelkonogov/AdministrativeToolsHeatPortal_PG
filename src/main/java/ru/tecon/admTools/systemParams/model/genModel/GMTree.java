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
    private long my_id;
    private String my_type;
    private String categ;
    private short measure_id;
    private String measure_name;
    private boolean vis_stat;
    private String icon;

    public GMTree() {
    }

    public GMTree(String id, String name, String parent, long my_id, String my_type, String categ, short measure_id, String measure_name, boolean vis_stat) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.my_id = my_id;
        this.my_type = my_type;
        this.categ = categ;
        this.measure_id = measure_id;
        this.measure_name = measure_name;
        this.vis_stat = vis_stat;
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

    public long getMy_id() {
        return my_id;
    }

    public String getMy_type() {
        return my_type;
    }

    public String getCateg() {
        return categ;
    }

    public short getMeasure_id() {
        return measure_id;
    }

    public String getMeasure_name() {
        return measure_name;
    }

    public void setMeasure_name(String measure_name) {
        this.measure_name = measure_name;
    }

    public boolean isVis_stat() {
        return vis_stat;
    }

    public void setVis_stat(boolean vis_stat) {
        this.vis_stat = vis_stat;
    }

    public Short getVis_statShort(){
        if (vis_stat){
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
                .add("my_id=" + my_id)
                .add("my_type='" + my_type + "'")
                .add("categ='" + categ + "'")
                .add("measure_id=" + measure_id)
                .add("measure_name='" + measure_name + "'")
                .add("vis_stat=" + vis_stat)
                .add("icon='" + icon + "'")
                .toString();
    }
}
