package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CheckBoxUtils {
    private static final Logger logger= LoggerFactory.getLogger(CheckBoxUtils.class);
    public static byte[] buildCheckBoxStateBytes(List<JCheckBox> checkBoxList, byte marker) {
        int dataBytes = (ProtocolConstants.COUNT + 7) / 8;
        byte[] bytes = new byte[1 + dataBytes];
        bytes[0] = marker;

        for (JCheckBox cb : checkBoxList) {
            if (cb == null || !cb.isSelected()) {
                continue;
            }

            String name = cb.getName();
            if (name == null) {
                continue; // безопасно пропускаем
            }

            int index;
            try {
                index = Integer.parseInt(name);
            } catch (NumberFormatException e) {
                // Лог или игнорирование — зависит от требований
                logger.info("CheckBoxUtils: пропущено чекбокс с некорректным именем: {}", name);
                continue;
            }

            // Защита от выхода за границы, если имена не от 0 до COUNT-1
            if (index < 0 || index >= ProtocolConstants.COUNT) {
                logger.info("CheckBoxUtils: индекс {} вне диапазона [0, {}]",index,(ProtocolConstants.COUNT - 1) );
                continue;
            }

            int byteIndex = index / 8;
            int bitIndex = index % 8;
            bytes[byteIndex + 1] |= (1 << bitIndex);
        }
        return bytes;
    }

    public static List<String> listActiveCheckBox(List<JCheckBox> checkBoxList) {
        List<String> result = new ArrayList<>();
        for (JCheckBox cb : checkBoxList) {
            if (cb != null && cb.isSelected()) {
                String name = cb.getName();
                if (name != null) {
                    result.add(name);
                }
            }
        }
        return result;
    }
}
