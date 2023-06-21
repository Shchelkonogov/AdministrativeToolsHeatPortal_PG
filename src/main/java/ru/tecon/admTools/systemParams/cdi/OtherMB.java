package ru.tecon.admTools.systemParams.cdi;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Контроллер для формы "Прочее" настроечных параметров
 *
 * @author Maksim Shchelkonogov
 * 21.06.2023
 */
@Named("other")
@ViewScoped
public class OtherMB implements Serializable {

    private static final List<String> FORM_LINKS = Arrays.asList("defaultValues.xhtml", "plannedOutages.xhtml");

    public List<String> getFormLinks() {
        return FORM_LINKS;
    }
}
