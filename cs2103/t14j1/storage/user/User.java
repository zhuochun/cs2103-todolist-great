package cs2103.t14j1.storage.user;

public class User {
    
    private static String name = "default";
    private static boolean isFirstTime = false;
    
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
    
    public static String getUserFolder() {
        return "user/" + name + "/";
    }
    
    public static boolean isFirstTimeUser() {
        return isFirstTime;
    }
    
    public static void setFirstTimeUser() {
        isFirstTime= true;
    }
    
}