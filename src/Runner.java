import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Runner implements Runnable {
    private MainPanel mainPanel;
    private File userCodeFile, judgeInputFile, judgeOutputFile;
    public Runner(MainPanel mainPanel, File userCodeFile, File judgeInputFile, File judgeOutputFile) {
        this.mainPanel = mainPanel;
        this.userCodeFile = userCodeFile;
        this.judgeInputFile = judgeInputFile;
        this.judgeOutputFile = judgeOutputFile;
    }

    @Override
    public void run() {
        Path workingDirectory;
        try {
            workingDirectory = Files.createTempDirectory("temp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Copy files
        mainPanel.setStatus(MainPanel.COPYING);
        copyFile(userCodeFile, workingDirectory);
        copyFile(judgeInputFile, workingDirectory);

        // Compile file
        mainPanel.setStatus(MainPanel.COMPILING);
        boolean compiled = compile(userCodeFile, workingDirectory);
        if (!compiled) {
            mainPanel.setStatus(MainPanel.COMPILE_TIME_ERROR);
            JOptionPane.showMessageDialog(mainPanel, "No - Compile Time Error", "Judgement", JOptionPane.ERROR_MESSAGE);
            mainPanel.finishRun();
            return;
        }

        // Run code
        mainPanel.setStatus(MainPanel.RUNNING);
        ArrayList<String> userOutput = new ArrayList<>();
        boolean ran = run(userCodeFile, workingDirectory, userOutput);
        if (!ran) {
            mainPanel.setStatus(MainPanel.RUN_TIME_ERROR);
            JOptionPane.showMessageDialog(mainPanel, "No - Run Time Error", "Judgement", JOptionPane.ERROR_MESSAGE);
            mainPanel.finishRun();
            return;
        }

        mainPanel.setStatus(MainPanel.GRADING);

        // Take in judge output
        ArrayList<String> judgeOutput = new ArrayList<>();
        readFile(judgeOutputFile, judgeOutput);

        // Prepare strings
        Judge.prepareStrings(judgeOutput);
        Judge.prepareStrings(userOutput);

        // Grade the code
        boolean passed = Judge.judge(judgeOutput, userOutput);
        if (passed) {
            mainPanel.setStatus(MainPanel.ACCEPTED);
            JOptionPane.showMessageDialog(mainPanel, "Accepted", "Judgement", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mainPanel.setStatus(MainPanel.WRONG_ANSWER);
            JOptionPane.showMessageDialog(mainPanel, "No - Wrong Answer", "Judgement", JOptionPane.INFORMATION_MESSAGE);
        }

        mainPanel.finishRun();
    }

    private boolean compile(File userCodeFile, Path workingDirectory) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(workingDirectory.toFile());
            processBuilder.command("javac", userCodeFile.getName());
            try {
                Process process = processBuilder.start();
                BufferedReader errorMessages = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String temp;
                while ((temp = errorMessages.readLine()) != null)
                    if (temp.endsWith("error") || temp.endsWith("errors"))
                        return false;
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    private boolean run(File userCodeFile, Path workingDirectory, ArrayList<String> userOutput) {
        String className = userCodeFile.getName().substring(0, userCodeFile.getName().lastIndexOf("."));
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.command("java", "-Djava.security.manager",className);

        try {
            Process process = processBuilder.start();
            BufferedReader outputMessages = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorMessages = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String temp;
            while ((temp = errorMessages.readLine()) != null) {
                if (!temp.contains("WARNING")) return false;
            }

            while ((temp = outputMessages.readLine()) != null)
                userOutput.add(temp);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFile(File file, ArrayList<String> output) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = bufferedReader.readLine()) != null) output.add(temp);
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFile(File file, Path destination) {
        try {
            Files.copy(file.toPath(), new File(destination + "/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
