import mtp.controller.MTPController;
import mtp.view.MTPView;
import mtp.model.MTPModel;

public class Runner {
    public static void main(String[] args) {
        MTPView view = new MTPView();
        MTPModel model = new MTPModel();
        MTPController controller = new MTPController(view, model);
        controller.init();
    }
}