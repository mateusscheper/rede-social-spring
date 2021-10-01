package scheper.mateus.utils;

import org.apache.commons.lang3.ObjectUtils;
import scheper.mateus.exception.UsuarioBusinessException;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ValidatorUtils {

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

            throw new UsuarioBusinessException(message);
        }
    }
}
