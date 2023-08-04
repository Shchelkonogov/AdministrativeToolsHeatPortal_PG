package ru.tecon.admTools.linker.model;

import java.io.Serializable;
import java.util.*;

/**
 * Данные для таблицы "Линкованные объекты / Объекты"
 *
 * @author Maksim Shchelkonogov
 * 10.07.2023
 */
public class LinkedData implements Serializable, LazyData {

    private final UUID uuid;
    private SystemObject dbObject;
    private Schema schema;
    private SystemObject opcObject;
    private int objIntKey;
    private String serverName;
    private boolean subscribed;

    private final Map<String, Boolean> recount = new HashMap<>();

    public LinkedData() {
        uuid = UUID.randomUUID();
    }

    public LinkedData(SystemObject dbObject, Schema schema, SystemObject opcObject, String serverName, boolean subscribed, int objIntKey) {
        this();
        this.dbObject = dbObject;
        this.schema = schema;
        this.opcObject = opcObject;
        this.serverName = serverName;
        this.subscribed = subscribed;
        this.objIntKey = objIntKey;
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public SystemObject getDbObject() {
        return dbObject;
    }

    public void setDbObject(SystemObject dbObject) {
        this.dbObject = dbObject;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public SystemObject getOpcObject() {
        return opcObject;
    }

    public void setOpcObject(SystemObject opcObject) {
        this.opcObject = opcObject;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public int getObjIntKey() {
        return objIntKey;
    }

    public Map<String, Boolean> getRecount() {
        return recount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedData that = (LinkedData) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LinkedData.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("dbObject=" + dbObject)
                .add("schema=" + schema)
                .add("opcObject=" + opcObject)
                .add("objIntKey=" + objIntKey)
                .add("serverName='" + serverName + "'")
                .add("subscribed=" + subscribed)
                .add("recount=" + recount)
                .toString();
    }
}
