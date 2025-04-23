package ru.tecon.admTools.mobile;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Local;
import ru.tecon.admTools.mobile.model.AsyncData;
import ru.tecon.admTools.mobile.model.ObjectData;
import ru.tecon.admTools.mobile.model.UserObject;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Maksim Shchelkonogov
 * 22.04.2025
 */
@Local
public interface TechParamLocal {

    String getRedirectUrl(String name);

    List<UserObject> getUserObjects(String user);

    List<ObjectData> getObjectData(int objectId, boolean allData);

    @Asynchronous
    Future<Void> getParamData(ObjectData objectData, int objectId, int parId, int statAggr);

    @Asynchronous
    Future<Void> getTnvData(ObjectData objectData, int objectId);

    String getAsyncRequest(int objectId, String userName);

    List<AsyncData> getAsyncData(String AsyncRequest);
}
