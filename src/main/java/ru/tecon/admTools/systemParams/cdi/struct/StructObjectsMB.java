package ru.tecon.admTools.systemParams.cdi.struct;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.scope.application.ObjectTypeController;
import ru.tecon.admTools.systemParams.ejb.struct.StructCurrentLocal;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;
import ru.tecon.admTools.systemParams.model.struct.StructType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для формы объекты группы структур
 *
 * @author Maksim Shchelkonogov
 */
@Named("structObjects")
@ViewScoped
public final class StructObjectsMB extends StructMB implements Serializable {

    private static final String HEADER = "Типы объектов";
    private static final String DIALOG_HEADER = "Создать новый тип объекта";
    private static final String PROP_HEADER = "Свойства объекта";

    @EJB(beanName = "structObjectsSB")
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

    @Inject
    private ObjectTypeController objectTypeController;

    @Override
    List<StructType> getStructTypes() {
        List<StructType> result = new ArrayList<>();
        objectTypeController.getObjectTypes().forEach(objectType -> result.add(new StructType(objectType.getId(), objectType.getName(), objectType.getCode())));
        return result;
    }

    @Override
    int addStruct(StructType structType, String login, String ip) throws SystemParamException {
        int result = super.addStruct(structType, login, ip);
        objectTypeController.loadDefaultTypes();
        return result;
    }

    @Override
    void removeStruct(StructType structType, String login, String ip) throws SystemParamException {
        super.removeStruct(structType, login, ip);
        objectTypeController.loadDefaultTypes();
    }
}
