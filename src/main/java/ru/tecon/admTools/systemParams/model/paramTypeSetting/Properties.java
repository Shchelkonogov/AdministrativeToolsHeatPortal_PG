package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 21.02.2023
 */
public class Properties implements Comparable<Properties> {

    private static final List<Integer> COMPARING_LIST = Arrays.asList(102, 307, 308, 309, 310, 103);

    private final int id;
    private final String name;

    public Properties(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Properties.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }

    @Override
    public int compareTo(@NotNull Properties o) {
        return Integer.compare(COMPARING_LIST.indexOf(id), COMPARING_LIST.indexOf(o.id));
    }
}
