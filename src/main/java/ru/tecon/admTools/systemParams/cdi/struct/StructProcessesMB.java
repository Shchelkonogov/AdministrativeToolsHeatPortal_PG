package ru.tecon.admTools.systemParams.cdi.struct;

import ru.tecon.admTools.systemParams.ejb.struct.StructCurrentLocal;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
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
