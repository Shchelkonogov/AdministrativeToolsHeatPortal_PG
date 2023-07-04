package ru.tecon.admTools.utils;

import org.postgresql.util.PSQLException;

import javax.faces.context.FacesContext;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.sql.SQLException;

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

    public static Object getPropertyValueViaReflection(Object o, String field)
            throws ReflectiveOperationException, IllegalArgumentException, IntrospectionException {
        return new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o);
    }

    /**
     * Вытаскиваю из {@link SQLException} сообщение.
     * @param ex Ошибка для проверки {@link SQLException}
     */
    public static String getSQLExceptionMessage(SQLException ex) {
        if ((ex.getSQLState() != null) &&
                ex.getSQLState().equals("11111") &&
                (ex instanceof PSQLException) &&
                (((PSQLException) ex).getServerErrorMessage() != null)) {
                return ((PSQLException) ex).getServerErrorMessage().getMessage();
        } else {
            return "Внутренняя ошибка сервера";
        }
    }
}
