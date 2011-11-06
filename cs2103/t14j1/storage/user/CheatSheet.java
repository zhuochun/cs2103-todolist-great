package cs2103.t14j1.storage.user;

public class CheatSheet {
    
    private static final String[] categories = {
        "User", "Edit", "Setting" // TODO: fill this up
    };
    
    private static final String[][] categoryItems  = {
        { // "User"
            "Add New List - Ctrl + L"
        },
        { // "Edit"
            // TODO: change and fill this up
            "Add New List - Ctrl + L"
        },
        { // "Setting"
            // TODO: change and fill this up
            "Add New List - Ctrl + L"
        }
        // add more
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
