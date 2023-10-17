package ru.tecon.admTools.linker.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * @author Maksim Shchelkonogov
 * 03.10.2023
 */
public class LinkSchemaData implements Serializable, LazyData, Comparable<LinkSchemaData> {

    private final UUID uuid;
    private Schema schema;
    private int eqLinked;
    private int neqLinked;
    private int allLinked;
    private int eqNoLinked;
    private int neqNoLinked;
    private int allNoLinked;

    public LinkSchemaData() {
        uuid = UUID.randomUUID();
    }

    public LinkSchemaData(Schema schema, int eqLinked, int neqLinked, int allLinked, int eqNoLinked, int neqNoLinked, int allNoLinked) {
        this();
        this.schema = schema;
        this.eqLinked = eqLinked;
        this.neqLinked = neqLinked;
        this.allLinked = allLinked;
        this.eqNoLinked = eqNoLinked;
        this.neqNoLinked = neqNoLinked;
        this.allNoLinked = allNoLinked;
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public int getEqLinked() {
        return eqLinked;
    }

    public int getNeqLinked() {
        return neqLinked;
    }

    public int getAllLinked() {
        return allLinked;
    }

    public int getEqNoLinked() {
        return eqNoLinked;
    }

    public int getNeqNoLinked() {
        return neqNoLinked;
    }

    public int getAllNoLinked() {
        return allNoLinked;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LinkSchemaData.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("schema=" + schema)
                .add("eqLinked=" + eqLinked)
                .add("neqLinked=" + neqLinked)
                .add("allLinked=" + allLinked)
                .add("eqNoLinked=" + eqNoLinked)
                .add("neqNoLinked=" + neqNoLinked)
                .add("allNoLinked=" + allNoLinked)
                .toString();
    }

    @Override
    public int compareTo(@NotNull LinkSchemaData o) {
        return Integer.compare(getEqLinked(), o.getEqLinked());
    }
}
