package org.example.immitator;

public interface SerialPortAdapter {
    void open();
    void close();
    void writeBytes(byte[] bytes);
    byte[] readExactly(int timeoutMs, int expectedLength);
    String getSystemPortName();
}
