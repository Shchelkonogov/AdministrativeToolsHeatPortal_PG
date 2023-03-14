package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.SeasonChangeSB;
import ru.tecon.admTools.systemParams.model.seasonChange.SeasonChangeTable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы основные параметры
 * @author Aleksey Sergeev
 */
@Named("seasonChangeMB")
@ViewScoped
public class SeasonChangeMB implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(SeasonChangeMB.class.getName());

    private List<SeasonChangeTable> seasonChangeTableList = new ArrayList<>();
    private String seasonChangeTable = "LETO";

    private String login;
    private String ip;

    private boolean disableSaveBtn = true;
    private boolean write=false;

    @EJB
    SeasonChangeSB seasonChangeSB;

    @PostConstruct
    private void init() {
        seasonChangeTableList = seasonChangeSB.getTableParams();

        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");

        if(seasonChangeTableList.get(0).getSeason().equals("Зима")){
            disableSaveBtn=false;
        }
    }

    public void onSeasonChange(){
        try {
            seasonChangeSB.changeSeason(seasonChangeTable, login, ip);
            LOGGER.info("Season changed to " + seasonChangeTable);
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
        seasonChangeTableList=seasonChangeSB.getTableParams();
    }

    public void buttonChangeAfterSelect() {
        disableSaveBtn = seasonChangeTable.equals("ZIMA") && seasonChangeTableList.get(0).getSeason().equals("Зима") ||
                seasonChangeTable.equals("LETO") && seasonChangeTableList.get(0).getSeason().equals("Лето");
    }

    public List<SeasonChangeTable> getSeasonChangeTableList() {
        return seasonChangeTableList;
    }

    public void setSeasonChangeTableList(List<SeasonChangeTable> seasonChangeTableList) {
        this.seasonChangeTableList = seasonChangeTableList;
    }

    public String getSeasonChangeTable() {
        return seasonChangeTable;
    }

    public void setSeasonChangeTable(String seasonChangeTable) {
        this.seasonChangeTable = seasonChangeTable;
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

    public boolean isDisableSaveBtn() {
        return disableSaveBtn;
    }

    public void setDisableSaveBtn(boolean disableSaveBtn) {
        this.disableSaveBtn = disableSaveBtn;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }
}
