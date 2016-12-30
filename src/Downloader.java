import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
public class Downloader {
    public static LinkedHashMap<String, String> map;
    private static final Object lock = new Object();
    public static String pathToSave;



    public static LinkedHashMap<String, String> readLinksFromFile(String path) throws IOException {
        LinkedHashMap<String, String> stringLinkedHashMap = new LinkedHashMap<>();
        String[] arr;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                arr = line.split(" ");
                stringLinkedHashMap.put(arr[1], arr[0]);
                line = bufferedReader.readLine();
            }
        }
        return stringLinkedHashMap;
    }

    private static synchronized String[] getStringUrlAndFileName() {

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        Map.Entry<String, String> entry = iterator.next();
        String url = entry.getValue();
        String fileName = entry.getKey();
        iterator.remove();
        return new String[]{fileName, url};

    }

    public static void downloadFile() throws IOException {
        String[] arr;
        synchronized (lock) {
            if (map.isEmpty()) return;
            arr = getStringUrlAndFileName();
        }

        String stringUrl = arr[1];
        String filename = arr[0];
        URL url = new URL(stringUrl);
        try (ReadableByteChannel byteChannel = Channels.newChannel(url.openStream())) {
            try (FileOutputStream outputStream = new FileOutputStream(new File(pathToSave + filename))) {
                outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);

            }
        }


    }
}
