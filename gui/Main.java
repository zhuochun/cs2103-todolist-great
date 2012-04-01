package gui;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import storage.logging.Log;
import storage.user.User;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(TaskMeter.class.getName());

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
        try {
            Log.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        User.initial();
        
        final Display display = Display.getDefault();
        try {
            TaskMeter application = new TaskMeter(display);
            application.open();
            application.layout();
            while (!application.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "main", e);
        }
    }
}
