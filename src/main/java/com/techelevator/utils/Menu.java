package com.techelevator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class Menu<T> {

    private static final TextEffect MENU_COLORS = new TextEffect(TextEffect.Code.BLUE);

    private final List<MenuItem> items = new ArrayList<>();
    private final BasicConsole console;
    private final String title;
    private final T target;

    public Menu(String title, T target, BasicConsole console) {
        this.title = title;
        this.target = target;
        this.console = console;
    }

    public void run() throws Exception {
        Boolean run = true;
        while (run) {
            display();
            Integer selection = console.promptForInteger("Please select: ");
            if (selection > 0 && selection <= items.size()) {
                MenuItem item = items.get(selection - 1);
                run = item.call();
            }
        }
    }

    public <X> Menu<T> addSubMenu(String text, Menu<X> subMenu) {
        items.add(new BaseMenuItem<>(subMenu, text) {
            @Override
            public Boolean call() throws Exception {
                subMenu.run();
                return true;
            }
        });
        return this;
    }

    public Menu<T> addItem(String text, Consumer<T> method) {
        return addItem(text, method, true);
    }

    public Menu<T> addItem(String text, Consumer<T> method, boolean returnValue) {
        items.add(new BaseMenuItem<>(target, text) {
            @Override
            public Boolean call() {
                method.accept(target);
                return returnValue;
            }
        });
        return this;
    }

    public Menu<T> addItem(String text, boolean returnValue) {
        items.add(new BaseMenuItem<>(target, text) {
            @Override
            public Boolean call() {
                return returnValue;
            }
        });
        return this;
    }

    private void display() {
        console.printBlankLine();
        console.printBanner(MENU_COLORS.apply(title));
        for (int i = 0; i < items.size(); ++i) {
            MenuItem item = items.get(i);
            console.printMessage(String.format("%d) %s", i + 1, item.geText()));
        }
    }

    private interface MenuItem extends Callable<Boolean> {
        String geText();
    }

    private static abstract class BaseMenuItem<X> implements MenuItem {
        private final String text;

        private BaseMenuItem(X target, String text) {
            this.text = text;
        }

        @Override
        public String geText() {
            return text;
        }
    }
}
