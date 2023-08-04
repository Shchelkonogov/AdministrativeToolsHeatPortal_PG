package ru.tecon.admTools.linker.model;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Данные для таблицы "Линкованные объекты / Объекты" по кнопке "Долинковать"
 *
 * @author Maksim Shchelkonogov
 * 02.08.2023
 */
public class OpcObjectForLinkData implements Serializable, LazyData {

    private final UUID uuid;
    private SystemObject opcObject;
    private int paramCount;
    private int objIntKey;

    public OpcObjectForLinkData() {
        uuid = UUID.randomUUID();
    }

    public OpcObjectForLinkData(SystemObject opcObject, int paramCount, int objIntKey) {
        this();
        this.opcObject = opcObject;
        this.paramCount = paramCount;
        this.objIntKey = objIntKey;
    }

    public void setOpcObject(SystemObject opcObject) {
        this.opcObject = opcObject;
    }

    public SystemObject getOpcObject() {
        return opcObject;
    }

    public int getParamCount() {
        return paramCount;
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public int getObjIntKey() {
        return objIntKey;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OpcObjectForLinkData.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("opcObject=" + opcObject)
                .add("paramCount=" + paramCount)
                .toString();
    }
}
