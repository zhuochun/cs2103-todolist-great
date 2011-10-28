package cs2103.t14j1.logic.reminder;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ReminderDialog {
    
    Display display;
    Shell shell;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            ReminderDialog window = new ReminderDialog();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ReminderDialog() {
        display = Display.getDefault();
        shell   = new Shell();
    }
    
    public void close() {
        shell.dispose();
    }

    /**
     * Open the window.
     */
    public void open() {
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                e.doit = false;
                shell.setVisible(false);
            }
        });
        shell.setSize(450, 300);
        shell.setText("SWT Application");

        shell.layout();
        shell.open();
        
        display.timerExec(5000, new Runnable() {
            public void run() {
                shell.setVisible(true);
                shell.setText("1000");
            }
        });
        display.timerExec(10000, new Runnable() {
            public void run() {
                shell.setVisible(true);
                shell.setText("2000");
            }
        });
        
        
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
          }
          display.dispose ();
        
//            try {
//                Thread.sleep(20000);
//            } catch (InterruptedException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//            close();
    }

}