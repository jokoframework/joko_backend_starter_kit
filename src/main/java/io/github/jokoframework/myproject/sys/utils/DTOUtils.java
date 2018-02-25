package io.github.jokoframework.myproject.sys.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danicricco on 2/25/18.
 */
public class DTOUtils {

    /**
     * Recorre una lista de elemenos de tipo DTOConvertable, los conviente a DTO
     * y devuelve una lista de DTOs
     *
     * @param entities entidades
     * @param clazz clase
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        List<DTOConvertable> l = (List<DTOConvertable>) entities;
        for (DTOConvertable o : l) {
            list.add((T) o.toDTO());
        }
        return list;
    }

    /**
     * Copia todos los atributos que sean iguales desde el objeto origen al
     * destino
     * @param origin
     * @param destination
     * @param <T>
     * @return
     */
    public static <T extends BaseDTO> T fromEntityToDTO(DTOConvertable origin, T destination) {
        BeanUtils.copyProperties(origin, destination);
        return destination;
    }

    /**
     * Created by danicricco on 2/25/18.
     */
    public static interface BaseDTO {

    }

    /**
     * Created by danicricco on 2/25/18.
     */

    public static interface DTOConvertable {
        BaseDTO toDTO();
    }
}
