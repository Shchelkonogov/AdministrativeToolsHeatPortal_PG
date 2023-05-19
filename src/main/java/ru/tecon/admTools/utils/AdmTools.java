package ru.tecon.admTools.utils;

import javax.faces.context.FacesContext;

/**
 * @author Maksim Shchelkonogov
 * 19.05.2023
 */
public class AdmTools {

    @SuppressWarnings("unchecked")
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }
}
