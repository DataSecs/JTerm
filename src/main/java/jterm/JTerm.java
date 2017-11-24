/*
* JTerm - a cross-platform terminal
* Copyright (code) 2017 Sergix, NCSGeek
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

// package = folder :P
package jterm;

import jterm.command.Command;
import jterm.command.CommandException;
import jterm.command.CommandExecutor;
import jterm.gui.Terminal;
import jterm.io.InputHandler;
import jterm.io.Keys;
import jterm.util.PrintStreamInterceptor;
import jterm.util.PromptInterceptor;
import jterm.util.PromptPrinter;
import jterm.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class JTerm {
    private static final Map<String, CommandExecutor> COMMANDS = new HashMap<>();
    public static PromptPrinter out;
    public static final String VERSION = "0.7.0";
    public static String PROMPT = ">> ";
    public static String dirChar;
    public static final String LICENSE = "JTerm Copyright (C) 2017 Sergix, NCSGeek, chromechris\n"
            + "This program comes with ABSOLUTELY NO WARRANTY.\n"
            + "This is free software, and you are welcome to redistribute it\n"
            + "under certain conditions.\n";

    // Default value of getProperty("user.dir") is equal to the default directory set when the program starts
    // Global directory variable (use "cd" command to change)
    public static String currentDirectory = System.getProperty("user.dir");
    public static final String USER_HOME_DIR = System.getProperty("user.home");

    public static boolean IS_WIN;
    public static boolean IS_UNIX;

    public static final BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
    private static Terminal terminal;
    private static boolean headless = false;

    public static void main(String[] args) {
        PROMPT = currentDirectory + PROMPT;
        setOS();
        initCommands();
        if (args.length > 0 && args[0].equals("headless")) {
            out = new PromptInterceptor();
            headless = true;
            out.println(LICENSE);
            out.print(PROMPT);
            try {
                while (true) {
                    InputHandler.read();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            terminal = new Terminal();
            terminal.setTitle("JTerm");
            terminal.setSize(720, 480);
            terminal.setVisible(true);
            out = new PrintStreamInterceptor(terminal);
            Keys.initGUI();
        }
    }

    public static void executeCommand(String options) {
        List<String> optionsArray = Util.getAsArray(options);

        if (optionsArray.size() == 0) {
            return;
        }

        String command = optionsArray.remove(0);
        if (!COMMANDS.containsKey(command)) {
            out.printf("Command \"%s\" is not available%n", command);
            return;
        }

        try {
            if (JTerm.isHeadless()) out.println();
            COMMANDS.get(command).execute(optionsArray);
        } catch (CommandException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void initCommands() {
        // Reflections reflections = new Reflections("jterm.command", new MethodAnnotationsScanner());
        // Set<Method> methods = reflections.getMethodsAnnotatedWith(Command.class);
        ArrayList<Method> methods = new ArrayList<>();
        String packageName = "jterm.command";

        URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
        Arrays.stream(new File(root.getFile()).listFiles()).forEach(file -> {
            try {
                Class clazz = Class.forName(String.format("%s.%s", packageName, file.getName().replaceAll(".class$", "")));

                if (!clazz.getSimpleName().startsWith("Command")) {
                    Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
                        if (method.isAnnotationPresent(Command.class)) {
                            methods.add(method);
                        }
                    });
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        methods.forEach(method -> {
            method.setAccessible(true);
            Command command = method.getDeclaredAnnotation(Command.class);
            Arrays.stream(command.name()).forEach(commandName -> {
                CommandExecutor executor = new CommandExecutor()
                        .setCommandName(commandName)
                        .setSyntax(command.syntax())
                        .setMinOptions(command.minOptions())
                        .setCommand((List<String> options) -> {
                            try {
                                method.invoke(null, options);
                            } catch (Exception e) {
                                System.err.println("Weird stuff...");
                            }
                        });

                COMMANDS.put(commandName, executor);
            });
        });
    }

    private static void setOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            JTerm.IS_WIN = true;
            dirChar = "\\";
            Keys.initWindows();
        } else if ("linux".equals(os) || os.contains("mac") || "sunos".equals(os) || "freebsd".equals(os)) {
            JTerm.IS_UNIX = true;
            dirChar = "/";
            Keys.initUnix();
        }
    }

    public static boolean isHeadless() {
        return headless;
    }

    public static Terminal getTerminal() {
        return terminal;
    }

    public static Set<String> getCommands() {
        return JTerm.COMMANDS.keySet();
    }

    /** For Unit Tests **/
    public static void setheadless(boolean b){
        headless = b;
    }

    public static void setTerminal(Terminal terminal) {
        JTerm.terminal = terminal;
    }

    public static void setPrompt(String prompt) {
        PROMPT = prompt;
    }
}
