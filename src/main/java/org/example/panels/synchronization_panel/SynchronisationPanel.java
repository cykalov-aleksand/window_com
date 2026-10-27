package org.example.panels.synchronization_panel;

import com.fazecast.jSerialComm.SerialPort;
import org.example.panels.observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class SynchronisationPanel extends JPanel implements Observer {
    private final JPanel centerPanel = new JPanel(new FlowLayout());
    private final JButton buttonRecord = new JButton("Запись");
    private final List<JCheckBox>listCheckBox=new ArrayList<>();
    private  JLabel statusLabel=new JLabel();
    private SerialPort serialPort;
    private final JComboBox<String>interval;
    private Map<String, Integer> map=mapString();

    public SynchronisationPanel() {
        setLayout(new BorderLayout());
        JPanel topPanel=new JPanel(new GridLayout(2,1));
        JLabel textLabel=new JLabel("tи положительного = 0,250 (мкс); tи отрицательного задай в combobox");
        List<String>stringList=new ArrayList<>(map.keySet());
        interval= new JComboBox<>(stringList.toArray(new String[0]));
        topPanel.add(textLabel);
        topPanel.add(interval);
        add(topPanel,BorderLayout.NORTH);
                      add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(buttonRecord);
        statusLabel.setText("");
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    private Map<String,Integer>mapString(){
        DecimalFormat df = new DecimalFormat("0.####");
        Map<String,Integer> map=new LinkedHashMap<>();
        float time=1.625f;
        for(int i=13;i<65536;i+=2){
            if(time<1000){
                map.put(time+" мкс",i);
            }else{
                map.put(df.format(time/1000)+" мс",i );
            }
            time+=0.25f;
        }
        return map;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public JComboBox<String> getInterval() {
        return interval;
    }

    public void actionListenerRecord(ActionListener actionListener){
        buttonRecord.addActionListener(actionListener);
    }

    public List<JCheckBox> getListCheckBox() {
        return listCheckBox;
    }

    @Override
    public void updateMap(Map<String, List<String>> mapName) {
        List<String> stringList = mapName.getOrDefault("GeneralPanel", List.of());
        Map<String,Boolean>mapi=new HashMap<>();
        listCheckBox.forEach((a)->mapi.put(a.getText(),a.isSelected()));
        centerPanel.removeAll();
        listCheckBox.clear();

        for (String name : stringList) {
            JCheckBox cb = new JCheckBox(name);
            cb.setName(name);
            listCheckBox.add(cb);
            centerPanel.add(cb);
        }
        if(!mapi.isEmpty()) {
            for (JCheckBox checkBox : listCheckBox) {
                if (mapi.containsKey(checkBox.getText())) {
                    checkBox.setSelected(mapi.get(checkBox.getText()));
                }
            }
        }

    }

    public JButton getButtonRecord() {
        return buttonRecord;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setStatusText(String text) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(text);
            statusLabel.setForeground(text.isEmpty() ? Color.BLACK : Color.RED);
        });
    }


    @Override
    public void updateSerialPort(SerialPort serialPort) {
        this.serialPort=serialPort;
    }
}
