package com.techelevator.utils;

import io.github.tbeerbower.TextEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic menu system that allows for the creation of menus and items that can be managed as a system.
 * Items can be added to menu instances as a name, title and method to be invoked on a target object.
 * Menus can be added to the system and managed on a stack where the current menu is the one on top of the stack.
 * Navigating to a submenu should push the submenu onto the stack.  Exiting a menu pops the current menu from the stack.
 * When the menu system is run the current menu is displayed and the selection of an item invokes that item's method on
 * the system's target object.
 * When the last menu is popped from the stack, the menu system run exits.
 * @param <T> the type of the target object for the menu system
 */
public class MenuSystem<T> {
    private static final TextEffect MENU_COLORS = new TextEffect(TextEffect.Code.BACKGROUND_BLUE);
    private final BasicConsole console;
    private final T target;
    private final Map<String,Menu<T>> menus;
    private final Stack<Menu<T>> menuStack = new Stack<>();

    // ***** Constructors *****************************************************

    /**
     * @param target  the target object for the system; the menu item methods will be invoked on this target object
     * @param console  the console for displaying output
     * @param menus  the list of menus for this system
     * @param startMenuName  the unique name of the starting menu; must match the name of one of the given menus
     */
    public MenuSystem(T target, BasicConsole console, List<Menu<T>> menus, String startMenuName) {
        this.target = target;
        this.console = console;
        this.menus = menus.stream().collect(Collectors.toMap(Menu::getName, Function.identity()));
        Menu<T> startingMenu = this.menus.get(startMenuName);
        menuStack.push(startingMenu);
    }

    /**
     * Run the menu system starting with the current menu.
     */
    public void run() {
        Menu<T> menu = getCurrentMenu();
        while (menu != null) {
            display();
            Integer selection = console.promptForInteger("Please select: ");
            if (selection > 0 && selection <= menu.items.length) {
                Item<T> item = menu.items[selection - 1];
                item.invoke(target);
            }
            menu = getCurrentMenu();
        }
    }

    /**
     * Exit the current menu to return to the previous menu
     */
    public void exitCurrentMenu() {
        menuStack.pop();
    }

    /**
     * Make the menu with the given unique name the current menu
     * @param menuName the unique name of the menu to make current
     */
    public void makeMenuCurrent(String menuName) {
        menuStack.push(menus.get(menuName));
    }


    // ***** Helper methods ***************************************************

    private void display() {
        Menu<T> menu = getCurrentMenu();

        console.printBlankLine();
        console.printBanner(MENU_COLORS, menu.title);
        for (int i = 0; i < menu.items.length; ++i) {
            Item<T> item = menu.items[i];
            console.printMessage(String.format("%d) %s", i + 1, item.geText()));
        }
    }

    private Menu<T> getCurrentMenu() {
        return menuStack.size() == 0 ? null : menuStack.peek();
    }


    // ***** inner class: Builder *********************************************

    public static class Builder<T> {
        private final List<Item<T>> items = new ArrayList<>();

        public Builder<T> addItem(String text, Consumer<T> method) {
            items.add(new Item<>(text, method));
            return this;
        }

        public Menu<T> getMenu(String name, String title) {
            return new Menu<T>(name, title, items);
        }
    }


    // ***** inner class: Menu ************************************************

    public static class Menu<T> {
        private final String name;
        private final String title;
        private final Item<T>[] items;

        private Menu(String name, String title, List<Item<T>> items) {
            this.name = name;
            this.title = title;
            this.items = items.toArray(new Item[0]);
        }

        public String getName() {
            return name;
        }
    }


    // ***** inner class: Item ************************************************

    private static class Item<T> {
        private final String text;
        private final Consumer<T> method;

        private Item(String text, Consumer<T> method) {
            this.text = text;
            this.method = method;
        }

        public String geText() {
            return text;
        }

        public void invoke(T target) {
            method.accept(target);
        }
    }
}
