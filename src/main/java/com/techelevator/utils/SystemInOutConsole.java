package com.techelevator.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * SystemInOutConsole is a class that provides text-based UI functionality using System.in and System.out.
 * This could be abstracted further to use any InputStream and PrintStream, but System.in
 * and System.out have been used intentionally to facilitate understanding by beginners.
 */

public class SystemInOutConsole implements BasicConsole {

    // ***** Colr effect for error messages
    private static final TextEffect ERROR_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_RED, TextEffect.Code.BLACK);

    private final Scanner input = new Scanner(System.in);

    @Override
    public void pauseOutput() {
        System.out.print("(Press return to continue...)");
        input.nextLine();
    }

    @Override
    public void printMessage(String message) {
        printMessage(message, true);
    }

    @Override
    public void printMessage(String message, boolean withLineFeed) {
        if (withLineFeed) {
            System.out.println(message); // Print with linefeed
        }
        else {
            System.out.print(message);  // Print without linefeed
        }
    }

    @Override
    public void printErrorMessage(String message) {
        printBanner(ERROR_COLORS, message);
    }

    @Override
    public void printBlankLine() {
        System.out.println();
    }

    @Override
    public void printBlankLines(int numberOfLines) {
        for (int i = 0; i < numberOfLines; i++) {
            System.out.println();
        }
    }

    @Override
    public void printDivider() {
        System.out.println("-----------------------------");
    }

    @Override
    public void printBanner(TextEffect effect, String message) {
        TextGrid.Builder builder = new TextGrid.Builder(1, true).setHorizontalCellPadding(1);
        builder.addCell(effect, message);
        System.out.println(builder.generate().toString());
    }

    @Override
    public void printBulletedItems(String[] items) {
        for (String item : items) {
            System.out.println("* " + item);
        }
    }

    @Override
    public String promptForString(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    @Override
    public boolean promptForYesNo(String prompt) {
        while (true) {
            String reply = promptForString(prompt);
            String upperReply = reply.toUpperCase();
            if (upperReply.startsWith("Y")) {
                return true;
            } else if (upperReply.startsWith("N")) {
                return false;
            } else {
                printErrorMessage("Please enter Y or N");
            }
        }
    }

    @Override
    public Integer promptForInteger(String prompt) {
        Integer result = null;
        String entry = promptForString(prompt);
        while (!entry.isBlank() && result == null) {
            try {
                result = Integer.parseInt(entry);
            } catch (NumberFormatException e) {
                printErrorMessage("Enter a number, please");
                entry = promptForString(prompt);
            }
        }
        return result;
    }

    @Override
    public Double promptForDouble(String prompt) {
        Double result = null;
        String entry = promptForString(prompt);
        while (!entry.isBlank() && result == null) {
            try {
                result = Double.parseDouble(entry);
            } catch (NumberFormatException e) {
                printErrorMessage("Enter a number, please");
                entry = promptForString(prompt);
            }
        }
        return result;
    }

    @Override
    public BigDecimal promptForBigDecimal(String prompt) {
        BigDecimal result = null;
        String entry = promptForString(prompt);
        while (!entry.isBlank() && result == null) {
            try {
                result = new BigDecimal(entry);
            } catch (NumberFormatException e) {
                printErrorMessage("Enter a decimal number, please");
                entry = promptForString(prompt);
            }
        }
        return result;
    }

    @Override
    public LocalDate promptForLocalDate(String prompt) {
        LocalDate result = null;
        String entry = promptForString(prompt + "(YYYY-MM-DD) ");
        while (!entry.isBlank() && result == null) {
            try {
                result = LocalDate.parse(entry);
            } catch (DateTimeParseException e) {
                printErrorMessage("Enter a date in YYYY-MM-DD format, please");
                entry = promptForString(prompt);
            }
        }
        return result;
    }

}
