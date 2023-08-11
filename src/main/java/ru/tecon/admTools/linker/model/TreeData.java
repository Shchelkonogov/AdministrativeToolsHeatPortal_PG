package ru.tecon.admTools.linker.model;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
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

    public TreeData() {
        uuid = UUID.randomUUID();
    }

    public TreeData(String itemId, String name, String parent, String myId, String myType) {
        this();
        this.itemId = itemId;
        this.name = name;
        this.parent = parent;
        this.myId = myId;
        this.myType = myType;
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

    public String getName() {
        return name;
    }

    public String getMyType() {
        return myType;
    }

    public SystemParam getParam() throws ParseException {
        ArrayList<String> idList = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(itemId);
        while (matcher.find()) {
            idList.add(matcher.group());
        }

        if (idList.size() == 2) {
            return new SystemParam(Integer.parseInt(idList.get(0)), Integer.parseInt(idList.get(1)));
        } else {
            throw new ParseException("Не верное количество групп", idList.size());
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TreeData.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("itemId='" + itemId + "'")
                .add("name='" + name + "'")
                .add("parent='" + parent + "'")
                .add("myId=" + myId)
                .add("myType='" + myType + "'")
                .toString();
    }

    @Override
    public int compareTo(@NotNull TreeData o) {
        return this.getName().compareTo(o.getName());
    }
}
