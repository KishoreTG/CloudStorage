package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class Menu {

    private final BufferedReader reader;
    private final BufferedWriter writer;

    private List<String> choices;
    private String title;
    private boolean exitBtn;

    public Menu(BufferedReader reader, BufferedWriter writer) {
        this.reader = reader;
        this.writer = writer;
        this.exitBtn = true;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setExitBtn(boolean state) {
        this.exitBtn = state;
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

    private void printChoices() throws IOException {
        for (int i = 0; i < choices.size(); i += 1) {
            writer.write(String.format("%3d. %s", i + 1, choices.get(i)));
            writer.newLine();
            writer.flush();
        }
    }

    private void printExitBtn() throws IOException {
        writer.write(String.format("%3d. %s", -1, "Exit"));
        writer.newLine();
        writer.flush();
    }

    private void listChoices() throws IOException {
        printChoices();
        if (exitBtn) {
            printExitBtn();
        }
        horizontalRule();
    }

    private void endMenu() throws IOException {
        horizontalRule();
    }

    private void promptChoice() throws IOException {
        writer.write("Enter the choice : ");
        writer.flush();
    }

    private void displayInvalidChoice() throws IOException {
        writer.write("Invalid Choice!");
        writer.newLine();
        writer.flush();
    }

    private boolean isValidChoice(int ch) {
        return (ch >= 1 && ch <= choices.size()) || (exitBtn && ch == -1);
    }

    private int getChoice() throws IOException {
        int ch;
        do {
            promptChoice();
            String input = reader.readLine();
            try {
                ch = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                ch = Integer.MAX_VALUE;
            }
            if (isValidChoice(ch)) {
                break;
            }
            displayInvalidChoice();
        } while (true);
        return ch;
    }

    public int show() throws IOException {
        displayTitle();
        listChoices();
        int ch = getChoice();
        endMenu();
        return ch;
    }

}
