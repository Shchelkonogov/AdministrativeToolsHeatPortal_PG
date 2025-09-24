package ru.tecon.admTools.systemParams.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import java.io.Serializable;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 23.03.2023
 */
@Named("systemParamsUtil")
@ViewScoped
public class SystemParamsUtilMB implements Serializable {

    private String login;
    private String ip;
    private boolean write;
    private String sessionId;

    @Inject
    private transient Logger logger;

    @EJB
    private CheckUserSB checkUserSB;

    @PostConstruct
    private void init() {
        logger.info("Init user data");

        Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        sessionId = parameterMap.get("sessionId");

        ip = parameterMap.get("ip");
        login = checkUserSB.getUser(sessionId);
        try {
            write = checkUserSB.checkSessionWrite(sessionId, Integer.parseInt(parameterMap.get("formId")));
        } catch (NumberFormatException ignore) {
            logger.log(Level.WARNING, "Error parse \"formId\" parameter: {0}", parameterMap.get("formId"));
            write = false;
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SystemParamsUtilMB.class.getSimpleName() + "[", "]")
                .add("login='" + login + "'")
                .add("ip='" + ip + "'")
                .add("write=" + write)
                .add("sessionId='" + sessionId + "'")
                .toString();
    }
}
