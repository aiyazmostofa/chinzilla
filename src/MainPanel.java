import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
    public static final int NOT_READY = 0, READY = 1, COPYING = 2, COMPILING = 3, COMPILE_TIME_ERROR = 4, RUNNING = 5, RUN_TIME_ERROR = 6, GRADING = 7, WRONG_ANSWER = 8, ACCEPTED = 9;

    private FileSelector userCodeSelector, judgeInputSelector, judgeOutputSelector;
    private JButton userCodeButton, judgeInputButton, judgeOutputButton, runButton;
    private JLabel userCodeLabel, judgeInputLabel, judgeOutputLabel, runLabel;

    private int status;
    private Thread background;

    public MainPanel() {
        status = NOT_READY;

        setPreferredSize(new Dimension(480, 180));
        setLayout(new GridLayout(4, 2, 10, 10));

        userCodeSelector = new FileSelector(this);
        judgeInputSelector = new FileSelector(this);
        judgeOutputSelector = new FileSelector(this);

        userCodeButton = new JButton("Select User's Code") {{addActionListener(userCodeSelector);}};
        judgeInputButton = new JButton("Select Judge's Input"){{addActionListener(judgeInputSelector);}};
        judgeOutputButton = new JButton("Select Judge's Output"){{addActionListener(judgeOutputSelector);}};
        runButton = new JButton("Run"){{addActionListener(start);}};

        userCodeLabel = new JLabel("File: ");
        judgeInputLabel = new JLabel("File: ");
        judgeOutputLabel = new JLabel("File: ");
        runLabel = new JLabel("Status: Not Ready");

        add(userCodeButton);
        add(userCodeLabel);

        add(judgeInputButton);
        add(judgeInputLabel);

        add(judgeOutputButton);
        add(judgeOutputLabel);

        add(runButton);
        add(runLabel);
    }

    public void refresh() {
        if (userCodeSelector.isFileSelected()) userCodeLabel.setText("File: " + userCodeSelector.getFile().getName()); else userCodeLabel.setText("File: ");
        if (judgeInputSelector.isFileSelected()) judgeInputLabel.setText("File: " + judgeInputSelector.getFile().getName()); else judgeInputLabel.setText("File: ");
        if (judgeOutputSelector.isFileSelected()) judgeOutputLabel.setText("File: " + judgeOutputSelector.getFile().getName()); else judgeOutputLabel.setText("File: ");

        runLabel.setText("Status: " + switch (status) {
            case READY -> "Ready";
            case COPYING -> "Copying";
            case COMPILING -> "Compiling";
            case COMPILE_TIME_ERROR -> "Compile Time Error";
            case RUNNING -> "Running";
            case RUN_TIME_ERROR -> "Run Time Error";
            case GRADING -> "Grading";
            case WRONG_ANSWER -> "Wrong Answer";
            case ACCEPTED -> "Accepted";
            default -> "Not Ready";
        });
    }

    public void setStatus(int status) {
        this.status = status;
        refresh();
    }

    public boolean isReady() {
        return userCodeSelector.isFileSelected() && judgeInputSelector.isFileSelected() && judgeOutputSelector.isFileSelected();
    }

    public void switchRunButton() {
        if (runButton.getText().equals("Run")) {
            runButton.removeActionListener(start);
            runButton.addActionListener(stop);
            runButton.setText("Stop");
        } else {
            runButton.removeActionListener(stop);
            runButton.addActionListener(start);
            runButton.setText("Run");
        }
    }

    private ActionListener start = e -> {
        if (isReady()) {
            background = new Thread(new Runner(this, userCodeSelector.getFile(), judgeInputSelector.getFile(), judgeOutputSelector.getFile()));
            background.start();
            switchRunButton();
        } else {
            JOptionPane.showMessageDialog(this, "Please make sure you have selected files for everything.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    };

    public void finishRun() {
        switchRunButton();
        setStatus(READY);
    }

    private ActionListener stop = e -> {
        background.interrupt();
        status = READY;
        switchRunButton();
    };
}