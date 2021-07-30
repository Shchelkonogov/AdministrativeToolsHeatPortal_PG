package ru.tecon.admTools.systemParams.cdi.struct;

import ru.tecon.admTools.systemParams.ejb.struct.StructCurrentRemote;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Контроллер для формы подразделения группы структур
 * @author Maksim Shchelkonogov
 */
@Named("structDivisions")
@ViewScoped
public class StructDivisionsMB extends StructMB implements Serializable {

    private static final String HEADER = "Типы подразделений";
    private static final String DIALOG_HEADER = "Создать новое подразделение";
    private static final String PROP_HEADER = "Свойства подразделения";

    @EJB(name = "structDivisionSB", mappedName = "ejb/structDivisionsSB")
    private StructCurrentRemote divisionsSB;

    @EJB
    private StructSB wrapperDivisions;

    @Override
    @PostConstruct
    public void init() {
        super.setStructCurrentBean(divisionsSB);
        super.setStructBean(wrapperDivisions);
        super.init();
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
