import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileSelector implements ActionListener {
    private MainPanel parent;

    private JFileChooser fileChooser;

    public FileSelector(MainPanel parent) {
        this.parent = parent;
        fileChooser = new JFileChooser();
    }

    public boolean isFileSelected() {
        return fileChooser.getSelectedFile() != null;
    }

    public File getFile() {
        if (!isFileSelected()) throw new IllegalStateException();
        return fileChooser.getSelectedFile();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        fileChooser.showOpenDialog(parent);
        if (parent.isReady()) parent.setStatus(MainPanel.READY);
        else parent.setStatus(MainPanel.NOT_READY);
        parent.refresh();
    }
}
