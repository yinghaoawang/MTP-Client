import mtp.MTPClient;

public class Runner {
    public static void main(String[] args) {
        MTPClient mtpClient = new MTPClient();
        mtpClient.init();
        new TrayMaker(mtpClient.getFrame());

    }
}