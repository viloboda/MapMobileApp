package com.example.mapapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {
    public static void copyFile(File src, File dst) throws IOException {
        copyFile(new FileInputStream(src), dst);
    }

    public static void copyFile(InputStream src, File dst) throws IOException {
        try (InputStream in = src) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

}
