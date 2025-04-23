package ru.tecon.admTools.mobile;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.linker.ejb.LinkerStateless;
import ru.tecon.admTools.mobile.model.AsyncData;
import ru.tecon.admTools.mobile.model.ObjectData;
import ru.tecon.admTools.mobile.model.UserObject;
import ru.tecon.admTools.systemParams.cdi.SystemParamsUtilMB;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Maksim Shchelkonogov
 * 21.04.2025
 */
@Named("techParam")
@ViewScoped
public class TechParamMB implements Serializable {

    private boolean mobile;
    private List<UserObject> userObjects;
    private UserObject userObject;
    private String fullObjectName;
    private List<ObjectData> objectData;
    private List<AsyncData> asyncData;
    private boolean archive = true;

    private boolean allParam;

    @EJB(name = "techParamBean")
    private TechParamLocal bean;

    @EJB
    private LinkerStateless linkerStateless;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Inject
    private TechParamService techParamService;

    @Inject
    private transient Logger logger;

    @PostConstruct
    private void init() {
        userObjects = bean.getUserObjects(utilMB.getLogin());
        techParamService.addUserObjects(userObjects);

        logger.log(Level.INFO, "sss {0}", userObjects.size());
    }

    public List<UserObject> completeText(String query) {
        String queryLoweCase = query.toLowerCase();
        return userObjects.stream().filter(f -> f.getName().toLowerCase().contains(queryLoweCase)).collect(Collectors.toList());
    }

    public void loadObjectName() {
        if (userObject != null) {
            fullObjectName = linkerStateless.getFullObjectName(userObject.getId());
        }
    }

    public void loadData() {
        logger.log(Level.INFO, "userObject {0} allParam {1}", new Object[]{userObject, allParam});
        if (userObject != null) {
            if (archive) {
                objectData = bean.getObjectData(userObject.getId(), allParam);
            } else {
                String asyncRequest = bean.getAsyncRequest(userObject.getId(), utilMB.getLogin());
                if ((asyncRequest != null) && !asyncRequest.isEmpty()) {
                    asyncData = bean.getAsyncData(asyncRequest);
                }
            }
        }
    }

    public String getRedirect() {
        return bean.getRedirectUrl("vtp_ta_empty");
    }

    public String getRootRedirect() {
        return bean.getRedirectUrl("root_ta");
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject userObject) {
        this.userObject = userObject;
    }

    public String getName() {
        if ((fullObjectName == null) || fullObjectName.isEmpty()) {
            return "";
        } else {
            return fullObjectName + " (" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")";
        }
    }

    public List<String> getDataTableColumns() {
        List<String> result = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now().withHour(0);
        for (int i = 0; i < 24; i++) {
            localDateTime = localDateTime.plusHours(1);
            result.add(localDateTime.format(DateTimeFormatter.ofPattern("HH")) + "ч");
        }
        return result;
    }

    public List<ObjectData> getObjectData() {
        return objectData;
    }

    public boolean isAllParam() {
        return allParam;
    }

    public void setAllParam(boolean allParam) {
        this.allParam = allParam;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isArchive() {
        return archive;
    }

    public String archiveDisplayCss() {
        return archive ? "" : "display: none;";
    }

    public String asyncDisplayCss() {
        return archive ? "display: none;" : "";
    }

    public List<AsyncData> getAsyncData() {
        return asyncData;
    }
}
