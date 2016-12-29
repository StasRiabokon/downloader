import java.io.IOException;

public class Multidownloading implements Runnable {
    @Override
    public void run() {

        while (!Downloader.map.isEmpty()) {
            try {
                Downloader.downloadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("loading ended");
        }
        System.out.println("thread completed");

    }
}
