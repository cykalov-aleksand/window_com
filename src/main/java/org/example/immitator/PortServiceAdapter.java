package org.example.immitator;

import com.fazecast.jSerialComm.SerialPort;

public class PortServiceAdapter {
    private final SerialPortAdapter adapter;

    public PortServiceAdapter(SerialPortAdapter adapter) {
        this.adapter = adapter;
    }

    public void setAndOpenPort(SerialPort port) {
        adapter.open();
    }

    public void writeBytes(byte[] bytes) {
        adapter.writeBytes(bytes);
    }

    public byte[] readExactly(int timeoutMs, int expectedLength) {
        return adapter.readExactly(timeoutMs, expectedLength);
    }

    public void closePort() {
        adapter.close();
    }

    public String getCurrentPortName() {
        return adapter.getSystemPortName();
    }
}