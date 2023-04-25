package ru.tecon.admTools.systemParams.model.struct;

import ru.tecon.admTools.systemParams.model.Measure;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий свойство структуры
 * @author Maksim Shchelkonogov
 */
public class StructTypeProp implements Serializable {

    private int id;
    private String name;
    private PropValType type;
    private PropCat cat;
    private String def;
    private Measure measure;
    private SpHeader spHeader;
    private int count;

    public StructTypeProp() {
    }

    public StructTypeProp(String name) {
        this();
        this.name = name;
    }

    public StructTypeProp(int id, String name, PropValType type, PropCat cat, String def, Measure measure, SpHeader spHeader) {
        this(name);
        this.id = id;
        this.type = type;
        this.cat = cat;
        this.def = def;
        this.measure = measure;
        this.spHeader = spHeader;
    }

    public StructTypeProp(int id, String name, PropValType type, PropCat cat, String def, Measure measure, SpHeader spHeader, int count) {
        this(id, name, type, cat, def, measure, spHeader);
        this.count = count;
    }

    public boolean check() {
        return !((type != null) && (cat != null) && (measure != null) && (spHeader != null));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PropValType getType() {
        return type;
    }

    public PropCat getCat() {
        return cat;
    }

    public String getDef() {
        return def;
    }

    public Measure getMeasure() {
        return measure;
    }

    public SpHeader getSpHeader() {
        return spHeader;
    }

    public int getCount() {
        return count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(PropValType type) {
        this.type = type;
    }

    public void setCat(PropCat cat) {
        this.cat = cat;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public void setSpHeader(SpHeader spHeader) {
        this.spHeader = spHeader;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StructTypeProp.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("type=" + type)
                .add("cat=" + cat)
                .add("def='" + def + "'")
                .add("measure=" + measure)
                .add("spHeader=" + spHeader)
                .add("count=" + count)
                .toString();
    }
}
