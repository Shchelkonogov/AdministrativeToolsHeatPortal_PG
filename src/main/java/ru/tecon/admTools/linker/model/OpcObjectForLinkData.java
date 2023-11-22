package ru.tecon.admTools.linker.model;

import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Objects;
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
    private String serverName;

    public OpcObjectForLinkData() {
        uuid = UUID.randomUUID();
    }

    public OpcObjectForLinkData(SystemObject opcObject, int objIntKey) {
        this();
        this.opcObject = opcObject;
        this.objIntKey = objIntKey;
    }

    public OpcObjectForLinkData(SystemObject opcObject, int paramCount, int objIntKey, String serverName) {
        this();
        this.opcObject = opcObject;
        this.paramCount = paramCount;
        this.objIntKey = objIntKey;
        this.serverName = serverName;
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

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public int getObjIntKey() {
        return objIntKey;
    }

    public PGobject getPgObject(String type) {
        String value = new StringJoiner(",", "(", ")")
                .add(String.valueOf(opcObject.getId()))
                .add("\"" + opcObject.getName() + "\"")
                .add(String.valueOf(objIntKey))
                .add("")
                .toString();

        try {
            PGobject pgObject = new PGobject();
            pgObject.setType(type);
            pgObject.setValue(value);
            return pgObject;
        } catch (SQLException e) {
            return null;
        }
    }

    public void setParamCount(int paramCount) {
        this.paramCount = paramCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpcObjectForLinkData that = (OpcObjectForLinkData) o;
        return paramCount == that.paramCount && objIntKey == that.objIntKey && Objects.equals(uuid, that.uuid) && Objects.equals(opcObject, that.opcObject) && Objects.equals(serverName, that.serverName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, opcObject, paramCount, objIntKey, serverName);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OpcObjectForLinkData.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("opcObject=" + opcObject)
                .add("paramCount=" + paramCount)
                .add("objIntKey=" + objIntKey)
                .add("serverName='" + serverName + "'")
                .toString();
    }
}
