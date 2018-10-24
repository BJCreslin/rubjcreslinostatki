package Objects;

import Objects.Exceptions.ExitException1;

public class ExitMenu implements ActionMenu {
    @Override
    public void action() throws ExitException1 {
        throw new ExitException1();

    }
}
