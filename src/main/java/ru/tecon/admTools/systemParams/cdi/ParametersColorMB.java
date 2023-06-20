package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.ParametersColorSB;
import ru.tecon.admTools.systemParams.model.ParametersColor;

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
 * Контоллер для расцветки состояний параметров части системных параметров
 * @author Maksim Shchelkonogov
 */
@Named("paramColor")
@ViewScoped
public class ParametersColorMB implements Serializable, AutoUpdate {

    private List<ParametersColor> parametersColorList;

    @Inject
    private transient Logger logger;

    @Inject
    private SystemParamsUtilMB utilMB;

    @EJB
    private ParametersColorSB parametersColorSB;

    /**
     * Метод обновляет расцветку ячеек цвет таблицы
     */
    @Override
    public void update() {
        parametersColorList = parametersColorSB.getParametersColor();

        for (int i = 0; i < parametersColorList.size(); i++) {
            if (utilMB.isWrite()) {
                PrimeFaces.current().executeScript("initColorPicker(" + i + ");");
            } else {
                PrimeFaces.current().executeScript("initColorPickerRead(" + i + ", '#" + parametersColorList.get(i).getColor() + "');");
            }
        }
    }

    /**
     * Метод обрабатывает нажатие на кнопку сохранить.
     * Сохраняет изменения цветов в базу
     */
    public void onSaveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        parametersColorList.stream().filter(ParametersColor::isChanged).forEach(parametersColor -> {
            logger.info("update for login " + utilMB.getLogin() + " and ip " + utilMB.getIp() + " parameter color " + parametersColor);

            try {
                parametersColorSB.updateParameterColor(parametersColor.getId(), parametersColor.getColor(), utilMB.getLogin(), utilMB.getIp());
                parametersColor.updateColor();
            } catch (SystemParamException e) {
                parametersColor.revert();
                errorMessages.add(parametersColor.getName());
                logger.warning(e.getMessage());
            }
        });

        update();

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
    }

    public List<ParametersColor> getParametersColorList() {
        return parametersColorList;
    }
}
