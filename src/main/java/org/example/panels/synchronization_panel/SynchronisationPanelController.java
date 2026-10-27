package org.example.panels.synchronization_panel;

import com.fazecast.jSerialComm.SerialPort;
import org.example.PortService;
import org.example.panels.observer.ServiceStation;
import org.example.utils.CheckBoxUtils;
import org.example.utils.ProtocolConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class SynchronisationPanelController {
    private static final Logger logger= LoggerFactory.getLogger(SynchronisationPanelController.class);
    private final SynchronisationPanel synchronisationPanel;
    private ServiceStation serviceStation;
    private final PortService portService;

    public SynchronisationPanelController(SynchronisationPanel synchronisationPanel, PortService portService,
                                          ServiceStation serviceStation) {
        this.synchronisationPanel = synchronisationPanel;
        this.portService=portService;
        this.serviceStation=serviceStation;
        synchronisationPanel.actionListenerRecord(e -> pressButton());
    }
    private byte[]convertNumberToByte(int number){
        byte[] bytes=new byte[2];
        bytes[0]= (byte) (number/256);
        bytes[1]=(byte) (number%256);
        return bytes;

    }
    private void pressButton(){
        List<JCheckBox> checkBoxes = synchronisationPanel.getListCheckBox();
        List<String>stringList=checkBoxes.stream().filter(e->!e.isSelected()).map(AbstractButton::getText).toList();
        serviceStation.replaceGroup("SynchronisationPanel",stringList);
       // System.err.println(generatePanel.getMap().get(generatePanel.getInterval().getSelectedItem()));
byte[]bytesNumber=convertNumberToByte(synchronisationPanel.getMap().get(synchronisationPanel.getInterval().getSelectedItem()));
        byte[]bytes1= CheckBoxUtils.buildCheckBoxStateBytes(checkBoxes,ProtocolConstants.CMD_GENERATE_RECORD);
       byte[]bytes=new byte[bytes1.length+2];
       System.arraycopy(bytes1,0,bytes,0,bytes1.length);
       bytes[bytes1.length]=bytesNumber[0];
       bytes[bytes1.length+1]=bytesNumber[1];
        logger.info(Arrays.toString(bytes));
        SerialPort serialPort= synchronisationPanel.getSerialPort();



        synchronisationPanel.getButtonRecord().setEnabled(false);

        new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                portService.setAndOpenPort(serialPort);
                portService.writeBytes(bytes);

                logger.info("TX | port={} | marker={} | len={} | data={}",
                        serialPort.getSystemPortName(), bytes[0], bytes.length, Arrays.toString(bytes));

                byte[] rxBytes = portService.readExactly(2000,8); // увеличенный таймаут

                logger.info("RX | port={} | len={} | data={}",
                        serialPort.getSystemPortName(), rxBytes.length, Arrays.toString(rxBytes));

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
                synchronisationPanel.getButtonRecord().setEnabled(true); // разблокируем кнопку
                try {
                    boolean success = get();
                    if (success) {
                       logger.info("Данные переданы успешно: первые 6 байт совпадают");
                        synchronisationPanel.setStatusText("Данные переданы успешно: первые 6 байт совпадают");
                    } else {
                        synchronisationPanel.setStatusText("Ошибка: первые 6 байт не совпадают");
                        // Для отладки можно вывести в консоль или отдельный лог‑компонент
                    }
                } catch (Exception ex) {
                    String msg = "Ошибка обмена: " + ex.getMessage();
                    logger.error(msg);
                    synchronisationPanel.setStatusText(msg);
                }
            }
        }.execute();
    }
          }


