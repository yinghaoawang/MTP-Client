package mtp;

import mtp.controller.MTPController;
import mtp.model.MTPModel;
import mtp.view.MTPView;

public class MTPClient {
    MTPView view;
    MTPModel model;
    private MTPController controller;
    public MTPClient() {
        view = new MTPView();
        model = new MTPModel();
        controller = new MTPController(view, model);
    }

    public void init() {
        controller.init();
    }

    public void forceInitServer() {
        controller.initServer();
    }
}
