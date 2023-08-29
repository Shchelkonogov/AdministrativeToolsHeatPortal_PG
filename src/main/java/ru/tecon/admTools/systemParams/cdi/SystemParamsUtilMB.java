package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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

    @Inject
    private transient Logger logger;

    @EJB
    private CheckUserSB checkUserSB;

    @PostConstruct
    private void init() {
        logger.info("Init user data");

        Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String sessionID = parameterMap.get("sessionId");

        ip = parameterMap.get("ip");
        login = checkUserSB.getUser(sessionID);
        try {
            write = checkUserSB.checkSessionWrite(sessionID, Integer.parseInt(parameterMap.get("formId")));
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

    @Override
    public String toString() {
        return new StringJoiner(", ", SystemParamsUtilMB.class.getSimpleName() + "[", "]")
                .add("login='" + login + "'")
                .add("ip='" + ip + "'")
                .add("write=" + write)
                .toString();
    }
}
