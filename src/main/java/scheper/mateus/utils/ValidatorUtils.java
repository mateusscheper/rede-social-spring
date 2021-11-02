package scheper.mateus.utils;

import org.apache.commons.lang3.ObjectUtils;
import scheper.mateus.exception.BusinessException;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ValidatorUtils {

    public static final String ID_DE_USUARIO_NAO_PODE_SER_NULO = "ID de usuário não pode ser nulo.";

    private ValidatorUtils() {
        throw new UnsupportedOperationException();
    }

    public static void validarNulo(String message, Object... object) {
        if (ObjectUtils.allNull(object)) {
            if (message.startsWith("{") && message.endsWith("}")) {
                String messageNoBraces = message.replace("{", "").replace("}", "");
                try {
                    message = ResourceBundle.getBundle("messages").getString(messageNoBraces);
                } catch (MissingResourceException e) {
                    // ignored
                }
            }

            throw new BusinessException(message);
        }
    }
}