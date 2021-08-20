package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.ParametersColorSB;
import ru.tecon.admTools.systemParams.model.ParametersColor;

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
 * Контоллер для расцветки состояний параметров части системных параметров
 * @author Maksim Shchelkonogov
 */
@Named("paramColor")
@ViewScoped
public class ParametersColorMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ParametersColorMB.class.getName());

    private List<ParametersColor> parametersColorList;

    private String login;
    private String ip;
    private boolean write = false;

    @EJB
    private ParametersColorSB parametersColorSB;

    @PostConstruct
    private void init() {
        parametersColorList = parametersColorSB.getParametersColor();

        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");
    }

    /**
     * Метод обновляет расцветку ячеек цвет таблицы
     */
    public void update() {
        for (int i = 0; i < parametersColorList.size(); i++) {
            if (write) {
                PrimeFaces.current().executeScript("changeValue(" + i + ");");
                PrimeFaces.current().executeScript("changeColor(" + i + ");");
            } else {
                PrimeFaces.current().executeScript("changeColor2(" + i + ", '#" + parametersColorList.get(i).getColor() + "');");
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
            LOGGER.info("update for login " + login + " and ip " + ip + " parameter color " + parametersColor);

            try {
                parametersColorSB.updateParameterColor(parametersColor.getId(), parametersColor.getColor(), login, ip);
                parametersColor.updateColor();
            } catch (SystemParamException e) {
                parametersColor.revert();
                errorMessages.add(parametersColor.getName());
                LOGGER.warning(e.getMessage());
            }
        });

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
    }

    public List<ParametersColor> getParametersColorList() {
        return parametersColorList;
    }

    public boolean isWrite() {
        return write;
    }
}
