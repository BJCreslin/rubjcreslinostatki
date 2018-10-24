package Objects;

import view.MainMenuView;
import view.MainMenuViewConsole;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMenu {
    HashMap<String, ActionMenu> menuPuncts;
    MainMenuView mainMenuView;

    public MainMenu() {
        menuPuncts = new HashMap<>();
        menuPuncts.put("Start", new StartMenu());
        menuPuncts.put("Exit", new ExitMenu());
        mainMenuView = new MainMenuViewConsole(menuPuncts);

    }

    public void start() {
        mainMenuView.show();

    }
}
