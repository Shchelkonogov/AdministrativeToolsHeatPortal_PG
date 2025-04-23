package ru.tecon.admTools.mobile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import ru.tecon.admTools.mobile.model.UserObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maksim Shchelkonogov
 * 22.04.2025
 */
@Named
@ApplicationScoped
public class TechParamService {

    private final Map<Integer, UserObject> userObjectMap = new HashMap<>();

    public void addUserObjects(List<UserObject> userObjectList) {
        for (UserObject userObject: userObjectList) {
            userObjectMap.put(userObject.getId(), userObject);
        }
    }

    public Map<Integer, UserObject> getUserObjectMap() {
        return userObjectMap;
    }
}
