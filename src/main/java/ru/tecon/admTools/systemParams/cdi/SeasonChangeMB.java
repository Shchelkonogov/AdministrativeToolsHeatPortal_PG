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
 *
 * @author Aleksey Sergeev
 */
@Named("seasonChangeMB")
@ViewScoped
public class SeasonChangeMB implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(SeasonChangeMB.class.getName());

    private List<SeasonChangeTable> seasonChangeTableList = new ArrayList<>();

    private boolean disableSaveBtn = false;

    @EJB
    SeasonChangeSB seasonChangeSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        seasonChangeTableList = seasonChangeSB.getTableParams();
    }

    /**
     * Обработчик переключения сезона, нажатие на копку изменить
     */
    public void onSeasonChange() {
        String seasonChangeTable = seasonChangeTableList.get(0).getSeason();
        if (seasonChangeTable.equals("Лето")) {
            seasonChangeTable = "ZIMA";
        } else seasonChangeTable = "LETO";

        try {
            seasonChangeSB.changeSeason(seasonChangeTable, utilMB.getLogin(), utilMB.getIp());
            LOGGER.info("Season changed to " + seasonChangeTable);
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
        seasonChangeTableList = seasonChangeSB.getTableParams();
    }

    public List<SeasonChangeTable> getSeasonChangeTableList() {
        return seasonChangeTableList;
    }

    public void setSeasonChangeTableList(List<SeasonChangeTable> seasonChangeTableList) {
        this.seasonChangeTableList = seasonChangeTableList;
    }

    public boolean isDisableSaveBtn() {
        return disableSaveBtn;
    }

    public void setDisableSaveBtn(boolean disableSaveBtn) {
        this.disableSaveBtn = disableSaveBtn;
    }
}
