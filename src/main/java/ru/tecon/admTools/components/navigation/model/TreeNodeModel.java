package ru.tecon.admTools.components.navigation.model;

import ru.tecon.admTools.utils.TreeNodeCompare;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

public class TreeNodeModel implements Serializable, TreeNodeCompare, TreeNodeLazy {

    private final UUID id;

    private String itemId;
    private String name;
    private String parent;
    private String myId;
    private String myType;

    public TreeNodeModel() {
        id = UUID.randomUUID();
    }

    public TreeNodeModel(String itemId, String myType) {
        this();
        this.itemId = itemId;
        this.myType = myType;
    }

    public TreeNodeModel(String itemId, String name, String parent, String myId, String myType) {
        this();
        this.itemId = itemId;
        this.name = name;
        this.parent = parent;
        this.myId = myId;
        this.myType = myType;
    }

    @Override
    public boolean isDirectory() {
        return myType.startsWith("S");
    }

    public String getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return !isDirectory();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    public int getMyId() {
        return Integer.parseInt(myId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TreeNodeModel.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("itemId='" + itemId + "'")
                .add("name='" + name + "'")
                .add("parent='" + parent + "'")
                .add("myId='" + myId + "'")
                .add("myType='" + myType + "'")
                .toString();
    }
}
