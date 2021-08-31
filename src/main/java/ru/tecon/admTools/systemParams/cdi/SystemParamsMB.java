package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Контроллер формы системные параметры левая часть меню и правая меняется
 * в зависимости от выбранного элемента
 * @author Maksim Shchelkonogov
 */
@Named("systemParams")
@ViewScoped
public class SystemParamsMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SystemParamsMB.class.getName());

    private Map<String, String> systemParamsMap = Stream.of(new String[][] {
            {"Расцветка параметров", "/view/sysParams/paramColor.xhtml"},
            {"Подразделения", "/view/sysParams/structDivisions.xhtml"},
            {"Объекты", "/view/sysParams/structObjects.xhtml"},
            {"Агрегаты", "/view/sysParams/structAggregates.xhtml"},
            {"Техпроцессы", "/view/sysParams/structProcesses.xhtml"},
            {"Приоритет проблем", "/view/sysParams/problemPriority.xhtml"},
            {"Связи", "/view/sysParams/objectLinks.xhtml"},
            {"Системные свойства", "/view/sysParams/sysProp.xhtml"}
    }).collect(Collectors.toMap(k -> k[0], v -> v[1]));

    private String ip;
    private String login;
    private boolean write = false;

    private String content = "";

    @EJB
    private CheckUserSB checkUserSB;

    @PostConstruct
    private void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String sessionID = request.getParameter("sessionID");

        ip = request.getParameter("ip");
        login = checkUserSB.getUser(sessionID);
        write = checkUserSB.checkSessionWrite(sessionID, Integer.parseInt(request.getParameter("formID")));
    }

    public void updateContent(String parameter) {
        content = systemParamsMap.get(parameter);
        LOGGER.info("update content: " + content);
    }

    public String getContent() {
        return content;
    }

    public Set<String> getParameters() {
        return systemParamsMap.keySet();
    }

    public String getLogin() {
        return login;
    }

    public boolean isWrite() {
        return write;
    }

    public String getIp() {
        return ip;
    }
}
