package org.example.panels.observer;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceStation {
   private final List<Observer>observers=new ArrayList<>();
private SerialPort serialPort;
private final Map<String,List<String>>mapCheckBoxName=new HashMap<>();

public void addObserver(Observer observer){
    observers.add(observer);
}
public void removeObserver(Observer observer){
    observers.remove(observer);
}

    /**
     * Полная замена списка для ключа (рекомендуется для чекбоксов).
     */
    public void replaceGroup(String key, List<String> items) {
        if (key == null) {
            return;
        }
        mapCheckBoxName.put(key, items != null ? new ArrayList<>(items) : new ArrayList<>());
        notifyObserversMap();
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
        notifyObserversSerialPorts();
    }
private void notifyObserversSerialPorts(){
    SerialPort portSnapshot = this.serialPort; // на случай гонки
    for (Observer observer : observers) {
        SwingUtilities.invokeLater(() -> observer.updateSerialPort(portSnapshot));
    }
}

    private void notifyObserversMap() {
             Map<String, List<String>> snapshot = new HashMap<>();
            for (Map.Entry<String, List<String>> e : mapCheckBoxName.entrySet()) {
                snapshot.put(e.getKey(), new ArrayList<>(e.getValue()));
            }

            List<Observer> safeCopy = new ArrayList<>(observers);
            for (Observer observer : safeCopy) {
                SwingUtilities.invokeLater(() -> observer.updateMap(snapshot));
            }
        }
    }

