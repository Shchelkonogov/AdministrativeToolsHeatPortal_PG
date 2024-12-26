package ru.tecon.admTools.utils.cdi.scope.request;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

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
