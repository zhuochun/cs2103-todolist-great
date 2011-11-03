package cs2103.t14j1.storage.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * java logger API setup
 * 
 * @author Zhuochun
 *
 */
public class Log {
    private static FileHandler txtFile;
    private static SimpleFormatter txtFormat;
    
    public static void setup() throws IOException {
        // create logger
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        
        // append to existing log
        boolean append = true;
        int sizeLimit  = 1000000; // 1MB
        
        txtFile   = new FileHandler("Logging.log", sizeLimit, 1, append);
        txtFormat = new SimpleFormatter();
        txtFile.setFormatter(txtFormat);
        
        logger.addHandler(txtFile);
    }

}