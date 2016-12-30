import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Driver {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the count of threads");
        int n = Integer.parseInt(bufferedReader.readLine());
        System.out.println("Enter the path to file with links");
        String f = bufferedReader.readLine();
        System.out.println("Enter output directory");
        Downloader.pathToSave = bufferedReader.readLine();

        Downloader.map = Downloader.readLinksFromFile(f);

        for (int i = 0; i < n; i++) {
            Multidownloading multidownloading = new Multidownloading();
            Thread thread = new Thread(multidownloading);
            thread.start();
        }

    }
}
