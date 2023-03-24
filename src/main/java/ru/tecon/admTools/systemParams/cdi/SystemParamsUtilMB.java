package ru.tecon.admTools.systemParams.cdi;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Maksim Shchelkonogov
 * 23.03.2023
 */
@Named("systemParamsUtil")
@ViewScoped
public class SystemParamsUtilMB implements Serializable {

    private String login;
    private String ip;
    private boolean write = false;

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
}
