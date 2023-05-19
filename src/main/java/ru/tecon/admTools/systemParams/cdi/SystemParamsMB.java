package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import ru.tecon.admTools.specificModel.ejb.CheckUserSB;
import ru.tecon.admTools.systemParams.model.SystemParamsCategories;
import ru.tecon.admTools.utils.AdmTools;

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
 *
 * @author Maksim Shchelkonogov
 */
@Named("systemParams")
@ViewScoped
public class SystemParamsMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SystemParamsMB.class.getName());

    private static final Map<String, SystemParamsCategories> SYSTEM_PARAMS_MAP = Stream.of(new Object[][]{
            {"Подразделения", new SystemParamsCategories("/view/sysParams/struct/structDivisions.xhtml", "")},
            {"Объекты", new SystemParamsCategories("/view/sysParams/struct/structObjects.xhtml", "")},
            {"Связи", new SystemParamsCategories("/view/sysParams/objectLinks.xhtml", "")},
            {"Агрегаты", new SystemParamsCategories("/view/sysParams/struct/structAggregates.xhtml", "")},
            {"Техпроцессы", new SystemParamsCategories("/view/sysParams/struct/structProcesses.xhtml", "")},
            {"Устройства", new SystemParamsCategories("/view/sysParams/struct/structDevices.xhtml", "")},
            {"Единицы измерения", new SystemParamsCategories("/view/sysParams/measure.xhtml", "")},
            {"Справочники", new SystemParamsCategories("/view/sysParams/catalog.xhtml", "")},
            {"Тип объекта по умолчанию", new SystemParamsCategories("/view/sysParams/defaultValues.xhtml", "")},
            {"Температурные графики", new SystemParamsCategories("/view/sysParams/temperature/tempGraphs.xhtml", "")},
            {"Суточные снижения", new SystemParamsCategories("/view/sysParams/temperature/dailyReduction.xhtml", "")},
            {"Коэффициенты для режимной карты", new SystemParamsCategories("/view/sysParams/coefficientsForRegimeCard.xhtml", "")},
            {"Расцветка параметров", new SystemParamsCategories("/view/sysParams/paramColor.xhtml", "")},
            {"Приоритет проблем", new SystemParamsCategories("/view/sysParams/problemPriority.xhtml", "")},
            {"Температура грунта", new SystemParamsCategories("/view/sysParams/groundTemp.xhtml", "")},
            {"Нормативные показатели", new SystemParamsCategories("/view/sysParams/normIndicators.xhtml", "")},
            {"Тнв по многолетним наблюдениям", new SystemParamsCategories("/view/sysParams/multiYearTemp.xhtml", "")},
            {"Основные параметры", new SystemParamsCategories("/view/sysParams/mainParam.xhtml", "")},
            {"Статистические агрегаты", new SystemParamsCategories("/view/sysParams/statAggr.xhtml", "")},
            {"Переключение сезона", new SystemParamsCategories("/view/sysParams/seasonChange.xhtml", "")},
            {"Тнв по многолетним наблюдениям", new SystemParamsCategories("/view/sysParams/multiYearTemp.xhtml", "")},
            {"Настройка типа параметра", new SystemParamsCategories("/view/sysParams/paramTypeSetting.xhtml", "paramTypeSetting")}
    }).collect(Collectors.toMap(k -> (String) k[0], v -> (SystemParamsCategories) v[1], (oldValue, newValue) -> oldValue, LinkedHashMap::new));

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
        content = SYSTEM_PARAMS_MAP.get(parameter).getPath();
        LOGGER.info("update content: " + content);
    }

    /**
     * Проверка выбран ли элемент меню
     *
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
     * Обработка нажатия кнопки
     *
     * @param item  имя меню
     * @param index индекс меню
     */
    public void selectButton(String item, int index) {
        String beanName = SYSTEM_PARAMS_MAP.get(item).getBeanName();
        if (!beanName.isEmpty()) {
            Object bean = AdmTools.findBean(SYSTEM_PARAMS_MAP.get(item).getBeanName());
            if (bean instanceof AutoUpdate) {
                ((AutoUpdate) bean).update();
            }
        }

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
