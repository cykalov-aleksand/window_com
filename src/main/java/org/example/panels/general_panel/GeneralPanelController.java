package org.example.panels.general_panel;

import com.fazecast.jSerialComm.SerialPort;
import org.example.PortService;
import org.example.utils.CheckBoxUtils;
import org.example.utils.ProtocolConstants;
import org.example.panels.observer.ServiceStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class GeneralPanelController {
    private static final Logger logger= LoggerFactory.getLogger(GeneralPanelController.class);
    private final GeneralPanel generalPanel;
    private final ServiceStation serviceStation;
    private final PortService portService;

    public GeneralPanelController(GeneralPanel generalPanel, ServiceStation serviceStation, PortService portService) {
        this.generalPanel = generalPanel;
        this.serviceStation=serviceStation;
        this.portService=portService;
        setupListener();
    }
    private void setupListener(){
        generalPanel.actionListenerRecord(e->pressButtonRecord());
        generalPanel.actionListenerButtonExit(e -> handleExit());
          }
    private void handleExit() {
        Window window = SwingUtilities.getWindowAncestor(generalPanel);
        if (window != null) {
           portService.closePort();
            window.dispose();
        }
    }
    private void pressButtonRecord(){
        List<JCheckBox> checkBoxes = generalPanel.getCheckBoxes();
        byte[]bytes=CheckBoxUtils.buildCheckBoxStateBytes(checkBoxes, ProtocolConstants.CMD_GENERAL_RECORD);
        System.out.println(Arrays.toString(bytes));
        SerialPort port = (SerialPort) generalPanel.getComboBox().getSelectedItem();
        if (port == null) {
            generalPanel.setStatusText("Не выбран COM‑порт");
            return;
        }
        generalPanel.getButtonRecord().setEnabled(false);
        serviceStation.replaceGroup("GeneralPanel",CheckBoxUtils.listActiveCheckBox(checkBoxes));
       serviceStation.setSerialPort(port);
        new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                portService.setAndOpenPort(port);
                portService.writeBytes(bytes);

                logger.info("TX | port={} | marker={} | len={} | data={}",
                        port.getSystemPortName(), bytes[0], bytes.length, Arrays.toString(bytes));

                byte[] rxBytes = portService.readExactly(2000,6); // увеличенный таймаут

                logger.info("RX | port={} | len={} | data={}",
                        port.getSystemPortName(), rxBytes.length, Arrays.toString(rxBytes));

                if (rxBytes.length < 6) {
                    throw new Exception("Получено меньше 6 байт: " + rxBytes.length);
                }

                boolean success = true;
                for (int i = 0; i < 6; i++) {
                    if (bytes[i] != rxBytes[i]) {
                        success = false;
                        break;
                    }
                }
                return success;
            }
            @Override
            protected void done() {
                generalPanel.getButtonRecord().setEnabled(true); // разблокируем кнопку
                try {
                    boolean success = get();
                    if (success) {
                        logger.info("Данные переданы успешно: первые 6 байт совпадают");
                        generalPanel.setStatusText("Данные переданы успешно: первые 6 байт совпадают");
                    } else {
                        generalPanel.setStatusText("Ошибка: первые 6 байт не совпадают");
                        // Для отладки можно вывести в консоль или отдельный лог‑компонент
                    }
                } catch (Exception ex) {
                    String msg = "Ошибка обмена: " + ex.getMessage();
                    logger.error(msg);
                    generalPanel.setStatusText(msg);
                }
            }
        }.execute();
    }
   }
