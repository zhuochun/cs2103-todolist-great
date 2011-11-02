package cs2103.t14j1.storage.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private static FileHandler txtFile;
    private static SimpleFormatter txtFormat;
    
    public static void setup() throws IOException {
        // Create Logger
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        
        txtFile   = new FileHandler("Logging.txt");
        txtFormat = new SimpleFormatter();
        txtFile.setFormatter(txtFormat);
        logger.addHandler(txtFile);
    }

}
