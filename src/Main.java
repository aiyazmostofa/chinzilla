import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chinzilla Competitive Code Checker");
        JPanel panel = new MainPanel();
        JPanel buffer = new JPanel() {{
            setPreferredSize(new Dimension(500,190));
            add(panel);
        }};
        frame.add(buffer);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
