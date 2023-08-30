package ru.tecon.admTools.linker.model;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Данные описывающие дерево в закладке "Линкованные объекты / Вычислимые параметры"
 *
 * @author Maksim Shchelkonogov
 * 08.08.2023
 */
public class TreeData implements Comparable<TreeData> {

    private final UUID uuid;
    private String itemId;
    private String name;
    private String parent;
    private String myId;
    private String myType;
    private TreeType treeType;

    public TreeData() {
        uuid = UUID.randomUUID();
    }

    public TreeData(String itemId, String name, String parent, String myId, String myType, TreeType treeType) {
        this();
        this.itemId = itemId;
        this.name = name;
        this.parent = parent;
        this.myId = myId;
        this.myType = myType;
        this.treeType = treeType;
    }

    public String getId() {
        return uuid.toString();
    }

    public String getParent() {
        return parent;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getName() {
        return name;
    }

    public String getMyType() {
        return myType;
    }

    public void setMyType(String myType) {
        this.myType = myType;
    }

    public TreeType getTreeType() {
        return treeType;
    }

    /**
     * Получение данных из объекта по паттерну ^PA(?<PA>\d+)SA(?<SA>-?\d+)$
     * @return данные id и statAgr
     * @throws ParseException в случае ошибки поиска по паттерну
     */
    public SystemParam getSystemParam() throws ParseException {
        Matcher matcher = Pattern.compile("^PA(?<PA>\\d+)SA(?<SA>-?\\d+)$").matcher(itemId);
        if (matcher.find()) {
            return new SystemParam(Integer.parseInt(matcher.group("PA")), Integer.parseInt(matcher.group("SA")));
        }

        throw new ParseException("Не соответствует паттерну ^PA(?<PA>\\d+)SA(?<SA>-?\\d+)$", 0);
    }

    /**
     * Получение данных из объекта по паттерну .*OP(?<id>\d+)$
     * @return данные id
     * @throws ParseException в случае ошибки поиска по паттерну
     */
    public String getParamId() throws ParseException {
        Matcher matcher = Pattern.compile(".*OP(?<id>\\d+)$").matcher(itemId);
        if (matcher.find()) {
            return matcher.group("id");
        }

        throw new ParseException("Не соответствует паттерну .*OP(?<id>\\d+)$", 0);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TreeData.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("itemId='" + itemId + "'")
                .add("name='" + name + "'")
                .add("parent='" + parent + "'")
                .add("myId='" + myId + "'")
                .add("myType='" + myType + "'")
                .add("treeType=" + treeType)
                .toString();
    }

    @Override
    public int compareTo(@NotNull TreeData o) {
        return this.getName().compareTo(o.getName());
    }
}
