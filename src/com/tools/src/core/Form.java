package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Form {

    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final List<String> inputs;
    private int maxLabelSize;
    private List<String> labels;
    private List<Integer> sensitiveData;
    private String title;

    public Form(BufferedReader reader, BufferedWriter writer) {
        this.reader = reader;
        this.writer = writer;
        inputs = new ArrayList<>();
        sensitiveData = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
        maxLabelSize = labels.get(0).length();
        for (String label : labels) {
            maxLabelSize = Math.max(maxLabelSize, label.length());
        }
    }

    public void setSensitiveData(List<Integer> sensitiveData) {
        this.sensitiveData = sensitiveData;
    }

    private void horizontalRule() throws IOException {
        writer.write("-" + "-".repeat(title.length()) + "-");
        writer.newLine();
        writer.flush();
    }

    private void printTitle() throws IOException {
        writer.write(" " + title + " ");
        writer.newLine();
        writer.flush();
    }

    private void displayTitle() throws IOException {
        horizontalRule();
        printTitle();
        horizontalRule();
    }

    private void endMenu() throws IOException {
        horizontalRule();
    }

    private void promptChoice(String prompt) throws IOException {
        writer.write(prompt);
        writer.flush();
    }

    public List<String> fill() throws IOException {
        displayTitle();
        Console console = System.console();
        for (int i = 0; i < labels.size(); i += 1) {
            String label = labels.get(i);
            String prompt = String.format(" %" + maxLabelSize + "s : ", label);
            promptChoice(prompt);
            String input;
            if (sensitiveData.contains(i) && console != null) {
                input = String.valueOf(console.readPassword());
            } else {
                input = reader.readLine();
            }
            inputs.add(input);
        }
        endMenu();
        return inputs;
    }

}
