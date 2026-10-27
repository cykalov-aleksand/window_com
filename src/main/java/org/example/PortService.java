package org.example;

import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.List;

public class PortService {
    private SerialPort currentPort; // достаточно обычной переменной под synchronized

    public synchronized void setAndOpenPort(SerialPort newPort) throws Exception {
        SerialPort existing = currentPort;
        if (existing != null && !existing.equals(newPort)) {
            if (existing.isOpen()) {
                existing.closePort();
            }
            currentPort = null;
        }

        if (newPort == null) {
            currentPort = null;
            return;
        }

        if (!newPort.isOpen()) {
            newPort.setBaudRate(9600);
            newPort.setNumDataBits(8);
            newPort.setParity(SerialPort.NO_PARITY);
            newPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            newPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);

            if (!newPort.openPort()) {
                throw new Exception("Не удалось открыть порт: " + newPort.getSystemPortName());
            }
        }

        currentPort = newPort;
    }
    public synchronized byte[] readExactly(long timeoutMs, int size) throws Exception {
        SerialPort port = currentPort;
        if (port == null || !port.isOpen()) {
            throw new Exception("Порт не открыт");
        }

        byte[] result = new byte[size];
        int totalRead = 0;
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (totalRead < size) {
            if (System.currentTimeMillis() >= deadline) {
                throw new Exception("Таймаут: прочитано " + totalRead + " из " + size + " байт");
            }

            int remaining = size - totalRead;
            byte[] chunk = new byte[remaining];
            int bytesRead = port.readBytes(chunk, remaining);

            if (bytesRead > 0) {
                System.arraycopy(chunk, 0, result, totalRead, bytesRead);
                totalRead += bytesRead;
            } else {
                Thread.sleep(5);
            }
        }

        return result;
    }
    public synchronized void writeBytes(byte[] data) throws Exception {
        SerialPort port = currentPort;
        if (port == null || !port.isOpen()) {
            throw new Exception("Порт не открыт");
        }
        port.writeBytes(data, data.length);
    }

    public synchronized void closePort() {
        SerialPort port = currentPort;
        currentPort = null;
        if (port != null && port.isOpen()) {
            try {
                port.closePort();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}