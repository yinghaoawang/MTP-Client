import mtp.MTPClient;

public class AutoServer {
    public static void main(String[] args) {
        MTPClient mtpClient = new MTPClient();
        mtpClient.init();
        mtpClient.forceInitServer();
    }
}