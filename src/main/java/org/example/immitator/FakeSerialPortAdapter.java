package org.example.immitator;

import java.util.Arrays;

public class FakeSerialPortAdapter implements SerialPortAdapter {

    private final String name;
    private byte[] lastSent = new byte[0];
    private final int extraBytesCount; // 1 или 2

    public FakeSerialPortAdapter(String name, int extraBytesCount) {
        this.name = name;
        this.extraBytesCount = extraBytesCount;
    }

    @Override
    public void open() {
        // ничего не делаем
    }

    @Override
    public void close() {
        // ничего не делаем
    }

    @Override
    public void writeBytes(byte[] bytes) {
        lastSent = bytes == null ? new byte[0] : bytes.clone();
        // можно логировать, если нужно
    }

    @Override
    public byte[] readExactly(int timeoutMs, int expectedLength) {
        // Формируем ответ: отправленные байты + extraBytesCount байт
        int totalLen = lastSent.length + extraBytesCount;
        byte[] response = new byte[totalLen];

        System.arraycopy(lastSent, 0, response, 0, lastSent.length);

        // Заполняем дополнительные байты чем-то понятным для демонстрации,
        // например, 0xAA, 0xBB — чтобы было видно, что это «ответ устройства»
        for (int i = 0; i < extraBytesCount; i++) {
            response[lastSent.length + i] = (byte) (0xAA + i);
        }

        // Если запрашивают меньше, чем мы хотим отдать — отдаём сколько просят.
        // Но в твоём коде ты запрашиваешь 6 байт, а у тебя в запросе обычно тоже 6,
        // так что логика будет работать как задумано.
        if (expectedLength < response.length) {
            return Arrays.copyOf(response, expectedLength);
        }
        return response;
    }

    @Override
    public String getSystemPortName() {
        return name;
    }
}
