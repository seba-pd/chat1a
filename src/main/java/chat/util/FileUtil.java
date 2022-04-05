package chat.util;

import java.io.*;
import java.nio.file.Path;

public class FileUtil {

    public static void sendfile(Path path, DataOutputStream dataOutputStream) {
        try {
            int bytes;
            File file = new File(String.valueOf(path));
            FileInputStream fileInputStream = new FileInputStream(file);

            dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[8 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void receiveFile(String pathToFile, DataInputStream dataInputStream) throws IOException {
        int bytes;
        File file = new File(pathToFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[8 * 1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            fileOutputStream.flush();
            size -= bytes;
        }
        fileOutputStream.close();
    }
}
