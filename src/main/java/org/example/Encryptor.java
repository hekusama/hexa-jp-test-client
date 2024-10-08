package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Encryptor {

    private static final String key = "Tenma";

    // Method to encrypt a string using XOR
    public static String xorEncrypt(String input) {
        char[] keyChars = key.toCharArray();
        char[] inputChars = input.toCharArray();
        char[] encryptedChars = new char[input.length()];

        for (int i = 0; i < input.length(); i++) {
            encryptedChars[i] = (char) (inputChars[i] ^ keyChars[i % keyChars.length]);
        }

        return new String(encryptedChars);
    }

    // Method to decrypt a string using XOR (same as encrypt since XOR is reversible)
    public static String xorDecrypt(String input) {
        return xorEncrypt(input); // XOR encryption is symmetrical
    }

    public static String readEncryptedData(String fileName) {
        return readEncryptedData(new File(fileName));
    }

    // Method to read encrypted high scores from the file
    public static String readEncryptedData(File file) {
        StringBuilder encryptedScores = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                encryptedScores.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return xorDecrypt(encryptedScores.toString().trim()); // Decrypt after reading
    }

    // Method to write encrypted high scores to the file
    public static void writeEncryptedData(File file, String data) {
        String encryptedScores = xorEncrypt(data); // Encrypt before writing

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(encryptedScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

