package ru.tecon.admTools.systemParams.cdi.struct;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.ejb.struct.StructCurrentLocal;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;

import java.io.Serializable;

/**
 * Контроллер для формы агрегаты группы структур
 * @author Maksim Shchelkonogov
 */
@Named("structAggregates")
@ViewScoped
public class StructAggregatesMB extends StructMB implements Serializable {

    private static final String HEADER = "Типы агрегатов";
    private static final String DIALOG_HEADER = "Создать новый тип агрегата";
    private static final String PROP_HEADER = "Свойства агрегата";

    @EJB(beanName = "structAggregatesSB")
    private StructCurrentLocal divisionsSB;

    @EJB
    private StructSB wrapperDivisions;

    @PostConstruct
    public void init() {
        super.setStructCurrentBean(divisionsSB);
        super.setStructBean(wrapperDivisions);
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
