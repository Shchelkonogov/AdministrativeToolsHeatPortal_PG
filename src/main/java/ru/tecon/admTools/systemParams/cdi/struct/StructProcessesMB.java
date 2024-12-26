package ru.tecon.admTools.systemParams.cdi.struct;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.ejb.struct.StructCurrentLocal;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;

import java.io.Serializable;

/**
 * Контроллер для формы техпроцессы группы структур
 * @author Maksim Shchelkonogov
 */
@Named("structProcesses")
@ViewScoped
public class StructProcessesMB extends StructMB implements Serializable {

    private static final String HEADER = "Типы технологических процессов";
    private static final String DIALOG_HEADER = "Создать новый тип технологического процесса";
    private static final String PROP_HEADER = "Свойства технологического процесса";

    @EJB(beanName = "structProcessesSB")
    private StructCurrentLocal structCurrentBean;

    @EJB
    private StructSB structBean;

    @PostConstruct
    public void init() {
        super.setStructCurrentBean(structCurrentBean);
        super.setStructBean(structBean);
    }

    public String getHeader() {
        return HEADER;
    }

    public String getDialogHeader() {
        return DIALOG_HEADER;
    }

    public String getPropHeader() {
        return PROP_HEADER;
    }
}
