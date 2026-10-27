package org.example.panels.observer;

import com.fazecast.jSerialComm.SerialPort;

import java.util.List;
import java.util.Map;

public interface Observer {
    void updateMap(Map<String,List<String>> mapName);
    void updateSerialPort(SerialPort serialPort);
}
