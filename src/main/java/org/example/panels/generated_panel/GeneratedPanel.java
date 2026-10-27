package org.example.panels.generated_panel;

import com.fazecast.jSerialComm.SerialPort;
import org.example.panels.observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GeneratedPanel extends JPanel implements Observer {
   private JPanel topPanel=new JPanel();
   private final List<JCheckBox>listCheckBox=new ArrayList<>();
    public GeneratedPanel(){
        setLayout(new BorderLayout());

        JPanel centerPanel=new JPanel();
        JPanel bottomPanel=new JPanel();
        add(topPanel,BorderLayout.NORTH);
        add(centerPanel,BorderLayout.CENTER);
        add(bottomPanel,BorderLayout.SOUTH);
    }

    @Override
    public void updateMap(Map<String, List<String>> mapName) {
        List<String> stringList = mapName.getOrDefault("SynchronisationPanel", List.of());
        Map<String,Boolean>mapi=new HashMap<>();
        listCheckBox.forEach((a)->mapi.put(a.getText(),a.isSelected()));
        topPanel.removeAll();
        listCheckBox.clear();

        for (String name : stringList) {
            JCheckBox cb = new JCheckBox(name);
            cb.setName(name);
            listCheckBox.add(cb);
            topPanel.add(cb);
        }
        if(!mapi.isEmpty()) {
            for (JCheckBox checkBox : listCheckBox) {
                if (mapi.containsKey(checkBox.getText())) {
                    checkBox.setSelected(mapi.get(checkBox.getText()));
                }
            }
        }
    }

    @Override
    public void updateSerialPort(SerialPort serialPort) {

    }
}
