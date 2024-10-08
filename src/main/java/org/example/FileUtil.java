package org.example;

import java.io.File;

public class FileUtil {
    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.length() == 0;
    }
    public static boolean isFileEmpty(File file) {
        return file.exists() && file.length() == 0;
    }
}

