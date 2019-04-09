public class Runner {
    public static void main(String[] args) {
        MTPGUI view = new MTPGUI();
        MTPModel model = new MTPModel();
        MTPController controller = new MTPController(view, model);
        controller.init();
    }
}