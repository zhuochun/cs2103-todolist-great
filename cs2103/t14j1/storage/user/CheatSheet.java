package cs2103.t14j1.storage.user;

public class CheatSheet {
    
    private static final String[] categories = {
        "Global", "User", "Edit", "Windows", "Setting", "Tips" // TODO: fill this up
    };
    
    private static final String[][] categoryItems  = {
    	{ // "Global"
            // TODO: change and fill this up
    		"Hide/Show TaskMeter - F6",
    		"Focus at SmartBar - Ctrl + K",
    		"Focus at Task Table - Ctrl + T",
    		"Focus at List Table - Ctrl + I"
        },
    	{ // "User"
            "Add New List - Ctrl + L",
            "Add New Task - Ctrl + N",
            "Save - Ctrl + S",
            "Search - Ctrl + F"
        },
        { // "Edit"
            "Undo - Ctrl + Z",
            "Redo - Ctrl + Y",
            "Delete Task/List - Del",
            "Edit Task/List - Ctrl + E",
            "Toggle Status - Ctrl + D",
            "Activate Reminder - Ctrl + R",
            "Change Priority - Ctrl + PRIORITY (1 or 2 or 3)"
        },
        { // "Windows"
            "Toggle between Quick Add View/Full View - Ctrl + M"
        },
        { // "Setting"
            "Toggle Auto Completion - Ctrl + 0"
        },
        { // "Tips"
            "Get Tips - Ctrl + L"
        }
    };
    
    public static String[] getCategories() {
        return categories;
    }
    
    public static String[] getItemInCategory(String name) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(name)) {
                return categoryItems[i];
            }
        }
        return null;
    }

}
