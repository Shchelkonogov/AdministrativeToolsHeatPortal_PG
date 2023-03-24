package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.SeasonChangeSB;
import ru.tecon.admTools.systemParams.model.seasonChange.SeasonChangeTable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы переключение сезона
 * @author Aleksey Sergeev
 */
@Named("seasonChangeMB")
@ViewScoped
public class SeasonChangeMB implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(SeasonChangeMB.class.getName());

    private List<SeasonChangeTable> seasonChangeTableList = new ArrayList<>();
    private String seasonChangeTable = "LETO";

    private boolean disableSaveBtn = true;

    @EJB
    SeasonChangeSB seasonChangeSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        seasonChangeTableList = seasonChangeSB.getTableParams();

        if(seasonChangeTableList.get(0).getSeason().equals("Зима")){
            disableSaveBtn=false;
        }
    }

    /**
     * Обработчик переключения сезона, нажатие на копку изменить
     */
    public void onSeasonChange(){
        try {
            seasonChangeSB.changeSeason(seasonChangeTable, utilMB.getLogin(), utilMB.getIp());
            LOGGER.info("Season changed to " + seasonChangeTable);
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
        seasonChangeTableList=seasonChangeSB.getTableParams();
    }

    /**
     * Обработчик активности кнопки изменить в зависимости от выбранного в списке сезона
     */
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

    public boolean isDisableSaveBtn() {
        return disableSaveBtn;
    }

    public void setDisableSaveBtn(boolean disableSaveBtn) {
        this.disableSaveBtn = disableSaveBtn;
    }
}
