package zad1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil{

    public static int counter = 0;
    public static void processDir(String dirName, String resultFileName) {
        try {
            Files.walkFileTree(Paths.get(dirName), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        boolean isOverWrite = counter != 0;
                        String fileName = String.valueOf(file);
                        byte[] redBytes = readChannel(fileName);
                        String decodedString = new String(redBytes, "Cp1250");
                        byte[] utf8 = decodedString.getBytes(StandardCharsets.UTF_8);
                        writeChannel(resultFileName, utf8, isOverWrite);
                        counter++;
                        return FileVisitResult.CONTINUE;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static byte[] readChannel(String fname) throws Exception {
        try (FileInputStream in = new FileInputStream(fname);
             FileChannel fc = in.getChannel()) {
            int size = (int) fc.size();
            ByteBuffer buf = ByteBuffer.allocate(size);
            fc.read(buf);
            buf.flip();
            byte[] wynik = new byte[buf.remaining()];
            buf.get(wynik);
            return wynik;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    static void writeChannel(String fname, byte[] data, boolean isOverWrite) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(data);
        try (
                FileOutputStream out = new FileOutputStream(fname, isOverWrite);
                FileChannel fc = out.getChannel();) {
            fc.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
