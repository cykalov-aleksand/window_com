package org.example.panels.general_panel;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class GeneralPanel extends JPanel {
    private final JComboBox<SerialPort> comboBox;
    private final JLabel statusLabel;
    private JButton buttonRecord;
    private JButton buttonExit;
    private final List<JCheckBox> checkBoxes = new ArrayList<>(40);

    public GeneralPanel() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        comboBox = new JComboBox<>();
        comboBox.setPreferredSize(new Dimension(300, 25));
        statusLabel = new JLabel();
        if (serialPorts.length == 0) {
            statusLabel.setText("Нет доступных COM портов");
            statusLabel.setForeground(Color.RED);
        } else {
            for (SerialPort port : serialPorts) {
                comboBox.addItem(port);
            }
            comboBox.setSelectedIndex(0);
            statusLabel.setText("");
        }
        setLayout(new BorderLayout());
        add(topPanel(), BorderLayout.NORTH);
        add(centerPanel(), BorderLayout.CENTER);
        add(bottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel topPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Список Com портов:");
        panel.add(label);
        panel.add(comboBox);
        panel.add(statusLabel);
        return panel;
    }

    private JPanel centerPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 8));
        for (int i = 0; i < 40; i++) {
            JCheckBox checkBox = new JCheckBox(Integer.toString(i));
            checkBox.setName(Integer.toString(i));
            panel.add(checkBox);
            checkBoxes.add(checkBox);
        }
        return panel;
    }

    private JPanel bottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        buttonRecord = new JButton("Запись");
        buttonExit = new JButton("Выход");
        panel.add(buttonRecord);
        panel.add(buttonExit);
        return panel;
    }

    public void actionListenerRecord(ActionListener actionListener) {
        buttonRecord.addActionListener(actionListener);
    }

    public void actionListenerButtonExit(ActionListener actionListener) {
        buttonExit.addActionListener(actionListener);
    }

    public JComboBox<SerialPort> getComboBox() {
        return comboBox;
    }

    public JButton getButtonRecord() {
        return buttonRecord;
    }

    public void setStatusText(String text) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(text);
            statusLabel.setForeground(text.isEmpty() ? Color.BLACK : Color.RED);
        });
    }

    public List<JCheckBox> getCheckBoxes() {
        return checkBoxes;
    }
}
