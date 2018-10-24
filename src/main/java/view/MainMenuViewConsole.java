package view;

import Command.ConsoleCommand;
import Objects.ActionMenu;
import Objects.Exceptions.ExitException1;

import java.util.HashMap;
import java.util.Map;

public class MainMenuViewConsole implements MainMenuView {
    HashMap<String, ActionMenu> menuPuncts = null;

    HashMap<Integer, String> tempMap;

    public MainMenuViewConsole(HashMap<String, ActionMenu> menuPuncts) {
        this.menuPuncts = menuPuncts;

    }

    @Override
    public void show() {
        try {

            while (true) {
                int i = 0;
                tempMap = new HashMap<>();
                for (Map.Entry<String, ActionMenu> entry : menuPuncts.entrySet()) {
                    System.out.println(i + ". " + entry.getKey());
                    tempMap.put(i, entry.getKey());
                    i++;
                }
                System.out.println("Input menu number");
                int inutN = ConsoleCommand.inputNumber();
                if (tempMap.containsKey(inutN)) {
                    menuPuncts.get(tempMap.get(inutN)).action();
                }

            }

        } catch (ExitException1 exitException1) {
            System.out.println("Exiting");
        }

    }

}
