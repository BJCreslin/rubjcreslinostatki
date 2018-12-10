package Objects;

import Objects.Exceptions.ExitException1;
import view.jview.MainForm;

public class MainGraphMenu implements ActionMenu {
    @Override
    public void action() throws ExitException1 {
        MainForm app = new MainForm(); //Создаем экземпляр нашего приложения
        app.action();
        app.setVisible(true);
    }
}
