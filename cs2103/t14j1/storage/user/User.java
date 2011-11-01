package cs2103.t14j1.storage.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cs2103.t14j1.storage.TaskList;

/**
 * a default User setting
 * 
 * @author Zhuochun
 * 
 */
public class User {

    private static String folder       = "user/";
    private static String name         = "default";
    private static String setting      = "setting.txt";
    private static boolean isFirstTime = false;

    private static String[] saveStrings = { "AutoComplete", "SortingMethod", "AutoSaveTime" };

    // Settings
    public static boolean performAutoComplete = true;
    public static int[]   sortingMethod       = { TaskList.SORT_DEADLINE,
                                                  TaskList.SORT_DATE,
                                                  TaskList.SORT_PRIORITY };
    public static int     autoSaveTime        = 300000; // default 5 mins

    // Exception Strings
    private static final String EXCEPTION_EMPTY_NAME = "User name cannot be null or empty";

    public static String getName() {
        return name;
    }

    public static void setName(String newName) {
        if (name == null || name.trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_EMPTY_NAME);
        }

        name = newName;
    }

    public static String getUserPath() {
        return folder + name + "/";
    }

    public static boolean isFirstTimeUser() {
        return isFirstTime;
    }

    public static void setFirstTimeUser() {
        isFirstTime = true;
    }

    public static void initial() {
        String filename = getUserPath() + setting;

        try {
            File file = new File(filename);

            // read in file
            if (file.isFile()) {
                BufferedReader r = new BufferedReader(new FileReader(file));
                // load user setting
                String line = r.readLine(); // First Line is User Name

                String[] terms;

                // load performAutoComplete
                while ((line = r.readLine()) != null) {
                    terms = line.split(" = ");
                    
                    //System.out.println(terms[0] + " -> " + terms[1]);

                    if (terms[0].equals(saveStrings[0])) {
                        performAutoComplete = terms[1].equals("true") ? true : false;
                    } else if (terms[0].equals(saveStrings[1])) {
                        String[] items = terms[1].split(",");
                        for (int i = 0; i < 3; i++) {
                            sortingMethod[i] = Integer.parseInt(items[i]);
                        }
                    } else if (terms[0].equals(saveStrings[2])) {
                        autoSaveTime = Integer.parseInt(terms[1]);
                    }
                }
            }
            // TODO: log
        } catch (IOException e) {
            // TODO: log
        } catch (Exception e) {
            // TODO: log
        }

    }

    public static void save() {
        String filename = getUserPath() + setting;
        
        try {
            // open file
            File file = new File(filename);
            FileWriter w = new FileWriter(file);

            // write line by line
            w.write("Settings for User : " + name + "\n");
            w.write(saveStrings[0] + " = " + performAutoComplete + "\n");
            w.write(saveStrings[1] + " = ");
            for (int i = 0; i < 2; i++) {
                w.write(sortingMethod[i] + ",");
            }
            w.write(sortingMethod[2] + "\n");
            w.write(saveStrings[2] + " = " + autoSaveTime + "\n");

            w.close();
        } catch (IOException e) {
            // TODO: log
        }
    }
}
