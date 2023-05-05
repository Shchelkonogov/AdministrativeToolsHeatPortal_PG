package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedHashMap;
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

    private static final Map<String, String> SYSTEM_PARAMS_MAP = Stream.of(new String[][] {
            {"Подразделения", "/view/sysParams/struct/structDivisions.xhtml"},
            {"Объекты", "/view/sysParams/struct/structObjects.xhtml"},
            {"Связи", "/view/sysParams/objectLinks.xhtml"},
            {"Агрегаты", "/view/sysParams/struct/structAggregates.xhtml"},
            {"Техпроцессы", "/view/sysParams/struct/structProcesses.xhtml"},
            {"Устройства", "/view/sysParams/struct/structDevices.xhtml"},
            {"Единицы измерения", "/view/sysParams/measure.xhtml"},
            {"Справочники", "/view/sysParams/catalog.xhtml"},
            {"Тип объекта по умолчанию", "/view/sysParams/defaultValues.xhtml"},
            {"Температурные графики", "/view/sysParams/temperature/tempGraphs.xhtml"},
            {"Суточные снижения", "/view/sysParams/temperature/dailyReduction.xhtml"},
            {"Коэффициенты для режимной карты", "/view/sysParams/coefficientsForRegimeCard.xhtml"},
            {"Расцветка параметров", "/view/sysParams/paramColor.xhtml"},
            {"Приоритет проблем", "/view/sysParams/problemPriority.xhtml"},
            {"Температура грунта", "/view/sysParams/groundTemp.xhtml"},
            {"Нормативные показатели", "/view/sysParams/normIndicators.xhtml"},
            {"Тнв по многолетним наблюдениям", "/view/sysParams/multiYearTemp.xhtml"},
            {"Основные параметры", "/view/sysParams/mainParam.xhtml"},
            {"Статистические агрегаты", "/view/sysParams/statAggr.xhtml"},
            {"Переключение сезона", "/view/sysParams/seasonChange.xhtml"},
            {"Тнв по многолетним наблюдениям", "/view/sysParams/multiYearTemp.xhtml"},
            {"Настройка типа параметра", "/view/sysParams/paramTypeSetting.xhtml"},
            {"Обобщенная модель", "/view/sysParams/genModel.xhtml"}
    }).collect(Collectors.toMap(k -> k[0], v -> v[1], (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    private String content = "";

    private String selectedButton;
    private Integer oldSelectedButtonIndex;

    @EJB
    private CheckUserSB checkUserSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String sessionID = request.get("sessionID");

        utilMB.setIp(request.get("ip"));
        utilMB.setLogin(checkUserSB.getUser(sessionID));
        utilMB.setWrite(checkUserSB.checkSessionWrite(sessionID, Integer.parseInt(request.get("formID"))));
    }

    private void updateContent(String parameter) {
        content = SYSTEM_PARAMS_MAP.get(parameter);
        LOGGER.info("update content: " + content);
    }

    /**
     * Проверка выбран ли элемент меню
     * @param item элемент меню
     * @return стиль для кнопки
     */
    public String checkSelected(String item) {
        if (selectedButton != null) {
            return selectedButton.equals(item) ? "ui-button-selected" : "ui-button-flat";
        } else {
            return "ui-button-flat";
        }
    }

    /**
     * Обработка нажития кнопки
     * @param item имя меню
     * @param index индекс меню
     */
    public void selectButton(String item, int index) {
        updateContent(item);
        selectedButton = item;
        if (oldSelectedButtonIndex != null) {
            PrimeFaces.current().ajax().update("menuForm:button_" + oldSelectedButtonIndex);
        }
        oldSelectedButtonIndex = index;
        PrimeFaces.current().ajax().update("menuForm:button_" + index, "paramPanel");
    }

    public String getContent() {
        return content;
    }

    public Set<String> getParameters() {
        return SYSTEM_PARAMS_MAP.keySet();
    }
}
