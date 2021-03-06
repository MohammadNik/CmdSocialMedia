package main.java.pages;

import main.java.lifecycle.PageManager;
import main.java.ui.Input;
import main.java.ui.Printer;

public abstract class Page {
    private Input input ;

    public void onCreate() {
        input = Input.getInstance();
    }

    public abstract void onStart();

    public void onDestroy() {
        Printer.clearScreen();
        input = null;
    }

    public Input getInput() {
        return input;
    }

    public PageManager getPageManager() {
        return PageManager.getInstance();
    }
}
