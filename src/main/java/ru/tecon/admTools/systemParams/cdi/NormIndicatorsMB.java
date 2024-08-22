package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.NormIndicatorsSB;
import ru.tecon.admTools.systemParams.model.normIndicators.*;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы нормативные показатели
 * @author Maksim Shchelkonogov
 */
@Named("normIndicators")
@ViewScoped
public class NormIndicatorsMB implements Serializable, AutoUpdate {

    private static final List<String> METROLOGY_HEADER_NAME =
            Arrays.asList("Температура K<sub>t</sub> [%]",
                    "Давление K<sub>p</sub> [%]",
                    "Расход K<sub>g</sub>, K<sub>v</sub> [%]",
                    "Энергия K<sub>q</sub> [%]");

    private static final List<String> INDICATOR_TV_HEADER_NAME =
            Arrays.asList("Подпитка нормированная К<sub>п_н</sub> [%]",
                    "Удельный расход К<sub>з_тв</sub> [тонн/Гкал]",
                    "Коэффициент подпитки К<sub>п_тв</sub> [%]");

    private static final List<String> INDICATOR_CO_HEADER_NAME =
            Arrays.asList("Удельный расход К<sub>з_цо</sub> [тонн/Гкал]",
                    "Коэффициент подпитки К<sub>п_цо</sub> [%]");

    private static final List<String> INDICATOR_GVS_HEADER_NAME =
            Collections.singletonList("Коэффициент циркуляции К<sub>ц</sub> [%]");

    private static final List<String> INDICATOR_VENT_HEADER_NAME =
            Arrays.asList("Удельный расход К<sub>з_в</sub> [тонн/Гкал]",
                    "Коэффициент подпитки К<sub>п_в</sub> [%]");

    private static final List<String> BORDER_VALUES_CO_HEADER_NAME =
            Arrays.asList("Тепловая потеря в подаче К<sub>Δт_цо</sub> [%]",
                    "Тепловая потеря в обратке К<sub>Δт_о_цо</sub> [%]",
                    "Утечка К<sub>у_цо</sub> [тонн]",
                    "Расход тепла норматив К<sub>ΔQ_цо</sub> [%]",
                    "К<sub>цо</sub>",
                    "Недоотпуск К1",
                    "Перетоп К2");

    private static final List<String> BORDER_VALUES_VENT_HEADER_NAME =
            Arrays.asList("Тепловая потеря в подаче К<sub>Δт_в</sub> [%]",
                    "Тепловая потеря в обратке К<sub>Δт_о_в</sub> [%]",
                    "Утечка К<sub>у_в</sub> [тонн]",
                    "Расход тепла норматив К<sub>ΔQ_в</sub> [%]");

    private static final List<String> BORDER_VALUES_GVS_HEADER_NAME =
            Arrays.asList("Тепловая потеря в подаче К<sub>Δт_гвс</sub> [%]",
                    "Тепловая потеря в обратке К<sub>Δт_о_гвс</sub> [%]",
                    "Утечка К<sub>у_гвс</sub> [тонн]",
                    "ΔT<sub>гвс</sub> [°C]",
                    "ΔT7 [°C] (T7<sub>ТП</sub> - T7<sub>потр</sub>)",
                    "T7<sub>норм</sub> [°C]",
                    "К<sub>гвс</sub>");

    private List<IndicatorMetrology> indicatorMetrologyList = new ArrayList<>();
    private List<IndicatorTV> indicatorTVList = new ArrayList<>();
    private List<IndicatorCO> indicatorCOList = new ArrayList<>();
    private List<IndicatorGVS> indicatorGVSList = new ArrayList<>();
    private List<IndicatorVENT> indicatorVENTList = new ArrayList<>();
    private List<IndicatorT7> indicatorT7List = new ArrayList<>();
    private List<IndicatorDT7> indicatorDT7List = new ArrayList<>();
    private List<IndicatorUnderSupply> indicatorUnderSupplyList = new ArrayList<>();
    private List<IndicatorBorderCo> indicatorBorderCoList = new ArrayList<>();
    private List<IndicatorBorderVent> indicatorBorderVentList = new ArrayList<>();
    private List<IndicatorBorderGvs> indicatorBorderGvsList = new ArrayList<>();

    @Inject
    private transient Logger logger;

    @EJB
    private NormIndicatorsSB normIndicatorsBean;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        logger.info("load form data");

        loadData();
    }

    /**
     * Загрузка данных формы
     */
    private void loadData() {
        indicatorMetrologyList = normIndicatorsBean.getMetrology();
        indicatorTVList = normIndicatorsBean.getTV();
        indicatorCOList = normIndicatorsBean.getCO();
        indicatorGVSList = normIndicatorsBean.getGVS();
        indicatorVENTList = normIndicatorsBean.getVENT();
        indicatorT7List = normIndicatorsBean.getT7();
        indicatorDT7List = normIndicatorsBean.getDT7();
        indicatorUnderSupplyList = normIndicatorsBean.getUnderSupply();
        indicatorBorderCoList = normIndicatorsBean.getBorderValuesCo();
        indicatorBorderVentList = normIndicatorsBean.getBorderValuesVent();
        indicatorBorderGvsList = normIndicatorsBean.getBorderValuesGvs();
    }

    /**
     * Обработчик нажатия кнопки сохранить
     */
    public void onSaveChanges(String type) {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        switch (type) {
            case "metrology": {
                indicatorMetrologyList.stream().filter(IndicatorMetrology::isChange).forEach(indicatorMetrology -> {
                    logger.info("update metrology indicator " + indicatorMetrology);

                    try {
                        normIndicatorsBean.updateMetrology(indicatorMetrology, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Метрологические погрешности");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorMetrologyList = normIndicatorsBean.getMetrology();
                break;
            }
            case "efficiency": {
                indicatorTVList.stream().filter(IndicatorTV::isChange).forEach(indicatorTV -> {
                    logger.info("update norm indicator TV " + indicatorTV);

                    try {
                        normIndicatorsBean.updateTV(indicatorTV, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Тепловой ввод");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorTVList = normIndicatorsBean.getTV();

                indicatorCOList.stream().filter(IndicatorCO::isChange).forEach(indicatorCO -> {
                    logger.info("update norm indicator CO " + indicatorCO);

                    try {
                        normIndicatorsBean.updateCO(indicatorCO, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add(indicatorCO.getName());
                        logger.warning(e.getMessage());
                    }
                });

                indicatorCOList = normIndicatorsBean.getCO();

                indicatorGVSList.stream().filter(IndicatorGVS::isChange).forEach(indicatorGVS -> {
                    logger.info("update norm indicator GVS " + indicatorGVS);

                    try {
                        normIndicatorsBean.updateGVS(indicatorGVS, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add(indicatorGVS.getName());
                        logger.warning(e.getMessage());
                    }
                });

                indicatorGVSList = normIndicatorsBean.getGVS();

                indicatorVENTList.stream().filter(IndicatorVENT::isChange).forEach(indicatorVENT -> {
                    logger.info("update norm indicator VENT " + indicatorVENT);

                    try {
                        normIndicatorsBean.updateVENT(indicatorVENT, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add(indicatorVENT.getName());
                        logger.warning(e.getMessage());
                    }
                });

                indicatorVENTList = normIndicatorsBean.getVENT();
                break;
            }
            case "borderValues": {
                indicatorBorderCoList.stream().filter(IndicatorBorderCo::isChange).forEach(indicatorBorderCo -> {
                    logger.info("update border indicator CO " + indicatorBorderCo);

                    try {
                        normIndicatorsBean.updateBorderValuesCo(indicatorBorderCo, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Граничные значения - центральное отопление");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorBorderCoList = normIndicatorsBean.getBorderValuesCo();

                indicatorBorderVentList.stream().filter(IndicatorBorderVent::isChange).forEach(indicatorBorderVent -> {
                    logger.info("update border indicator vent " + indicatorBorderVent);

                    try {
                        normIndicatorsBean.updateBorderValuesVent(indicatorBorderVent, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Граничные значения - вентиляция");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorBorderVentList = normIndicatorsBean.getBorderValuesVent();

                indicatorBorderGvsList.stream().filter(IndicatorBorderGvs::isChange).forEach(indicatorBorderGvs -> {
                    logger.info("update border indicator gvs " + indicatorBorderGvs);

                    try {
                        normIndicatorsBean.updateBorderValuesGvs(indicatorBorderGvs, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Граничные значения - гвс");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorBorderGvsList = normIndicatorsBean.getBorderValuesGvs();
                break;
            }
            case "analysis": {
                indicatorUnderSupplyList.stream().filter(IndicatorUnderSupply::isChange).forEach(indicatorUnderSupply -> {
                    logger.info("update underSupply indicator " + indicatorUnderSupply);

                    try {
                        normIndicatorsBean.updateUnderSupply(indicatorUnderSupply, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add(e.getMessage());
                        logger.warning(e.getMessage());
                    }
                });

                indicatorUnderSupplyList = normIndicatorsBean.getUnderSupply();

                indicatorT7List.stream().filter(IndicatorT7::isChange).forEach(indicatorT7 -> {
                    logger.info("update T7 indicator " + indicatorT7);

                    try {
                        normIndicatorsBean.updateT7(indicatorT7, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Анализ потребителей T7");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorT7List = normIndicatorsBean.getT7();

                indicatorDT7List.stream().filter(IndicatorDT7::isChange).forEach(indicatorDT7 -> {
                    logger.info("update dT7 indicator " + indicatorDT7);

                    try {
                        normIndicatorsBean.updateDT7(indicatorDT7, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Анализ потребителей ΔT7");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorDT7List = normIndicatorsBean.getDT7();
                break;
            }
        }

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
    }

    /**
     * Обработчик изменения ячейки таблицы
     * @param event событие изменения
     */
    public void onCellEdit(CellEditEvent<?> event) {
        String clientID = event.getColumn().getChildren().get(0).getClientId().replaceAll(":", "\\:");
        PrimeFaces.current().executeScript("document.getElementById('" + clientID + "').parentNode.style.backgroundColor = 'lightgrey'");
    }


    public List<IndicatorTV> getIndicatorTVList() {
        return indicatorTVList;
    }

    public List<IndicatorCO> getIndicatorCOList() {
        return indicatorCOList;
    }

    public List<IndicatorGVS> getIndicatorGVSList() {
        return indicatorGVSList;
    }

    public List<IndicatorVENT> getIndicatorVENTList() {
        return indicatorVENTList;
    }

    public List<IndicatorT7> getIndicatorT7List() {
        return indicatorT7List;
    }

    public List<IndicatorDT7> getIndicatorDT7List() {
        return indicatorDT7List;
    }

    public List<IndicatorUnderSupply> getIndicatorUnderSupplyList() {
        return indicatorUnderSupplyList;
    }

    public List<IndicatorMetrology> getIndicatorMetrologyList() {
        return indicatorMetrologyList;
    }

    public List<IndicatorBorderCo> getIndicatorBorderCoList() {
        return indicatorBorderCoList;
    }

    public List<IndicatorBorderVent> getIndicatorBorderVentList() {
        return indicatorBorderVentList;
    }

    public List<IndicatorBorderGvs> getIndicatorBorderGvsList() {
        return indicatorBorderGvsList;
    }

    public String getMetrologyHeaderName(int index) {
        return METROLOGY_HEADER_NAME.get(index);
    }

    public String getBorderValuesCOHeaderName(int index) {
        return BORDER_VALUES_CO_HEADER_NAME.get(index);
    }

    public String getBorderValuesVentHeaderName(int index) {
        return BORDER_VALUES_VENT_HEADER_NAME.get(index);
    }

    public String getBorderValuesGVSHeaderName(int index) {
        return BORDER_VALUES_GVS_HEADER_NAME.get(index);
    }

    public String getIndicatorTVHeaderName(int index) {
        return INDICATOR_TV_HEADER_NAME.get(index);
    }

    public String getIndicatorCOHeaderName(int index) {
        return INDICATOR_CO_HEADER_NAME.get(index);
    }

    public String getIndicatorGVSHeaderName(int index) {
        return INDICATOR_GVS_HEADER_NAME.get(index);
    }

    public String getIndicatorVentHeaderName(int index) {
        return INDICATOR_VENT_HEADER_NAME.get(index);
    }
}
