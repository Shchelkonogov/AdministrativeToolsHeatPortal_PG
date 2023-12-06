package ru.tecon.admTools.utils.cdi.scope.request;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * @author Maksim Shchelkonogov
 * 28.11.2023
 */
@Named("messageController")
@RequestScoped
public class MessageController {

    public void showErrorMessage() {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Ошибка модуля"));
    }
}
