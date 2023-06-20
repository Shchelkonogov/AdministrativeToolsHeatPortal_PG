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
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы нормативные показатели
 * @author Maksim Shchelkonogov
 */
@Named("normIndicators")
@ViewScoped
public class NormIndicatorsMB implements Serializable, AutoUpdate {

    private List<IndicatorTV> indicatorTVList = new ArrayList<>();
    private List<IndicatorCO> indicatorCOList = new ArrayList<>();
    private List<IndicatorGVS> indicatorGVSList = new ArrayList<>();
    private List<IndicatorVENT> indicatorVENTList = new ArrayList<>();
    private List<IndicatorOther> indicatorOtherList = new ArrayList<>();
    private List<IndicatorT7> indicatorT7List = new ArrayList<>();
    private List<IndicatorDT7> indicatorDT7List = new ArrayList<>();
    private List<IndicatorUnderSupply> indicatorUnderSupplyList = new ArrayList<>();

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
        indicatorTVList = normIndicatorsBean.getTV();
        indicatorCOList = normIndicatorsBean.getCO();
        indicatorGVSList = normIndicatorsBean.getGVS();
        indicatorVENTList = normIndicatorsBean.getVENT();
        indicatorOtherList = normIndicatorsBean.getOther();
        indicatorT7List = normIndicatorsBean.getT7();
        indicatorDT7List = normIndicatorsBean.getDT7();
        indicatorUnderSupplyList = normIndicatorsBean.getUnderSupply();
    }

    /**
     * Обработчик нажатия кнопки сохранить
     */
    public void onSaveChanges(String type) {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        switch (type) {
            case "TV": {
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
                break;
            }
            case "CO": {
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
                break;
            }
            case "GVS": {
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
                break;
            }
            case "VENT": {
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
            case "Other": {
                indicatorOtherList.stream().filter(IndicatorOther::isChange).forEach(indicatorOther -> {
                    logger.info("update norm indicator other " + indicatorOther);

                    try {
                        normIndicatorsBean.updateOther(indicatorOther, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        errorMessages.add("Источник - Потребитель");
                        logger.warning(e.getMessage());
                    }
                });

                indicatorOtherList = normIndicatorsBean.getOther();
                break;
            }
            case "T7": {
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
                break;
            }
            case "dT7": {
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
            case "underSupply": {
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

    public List<IndicatorOther> getIndicatorOtherList() {
        return indicatorOtherList;
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
}
