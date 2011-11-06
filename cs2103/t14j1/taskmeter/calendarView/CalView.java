package cs2103.t14j1.taskmeter.calendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;


/*class Task{
	String name;
	Date startTime;
	Date endTime;
	
	Task(String name, Date startTime, Date endTime){
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}*/

class TaskItem extends Composite{

	public TaskItem(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	
}

/**
 * The Calendar View -- to view the tasks graphically
 * 
 * To use it:
 * 1. Create a view by calling 
 *  <code> CalView view = new CalView(shell, SWT.NONE); </code>
 * 2. Set appropriate size and location
 * 	<code>	view.setSize(900, 700); view.setLocation(5,5); </code>
 * 3. Add the task list you want to display
 *  <code> view.addList(taskList); </code>
 *  
 *  TODO:
 *  	1. Add the all-day event to the top of the day
 *  	2. Display different list with different colour
 *  	3. Allow user to set the day_num, time interval
 *  
 * @author SongYY
 *
 */
public class CalView extends Composite{
	
	// for drawing
	private Calendar currentTime;
	private Calendar calStartTime;
	
	private int dayNum = 7;
	private int timeInterval = 30;	// time interval, should be a divider of 60
	
	
	// internal data
	private List<TaskList> tasklist;
	
	// for formating
	private static final String timeFormat = "%2d:%2d";
	private static final String dateFormat = "%s,%s.%s";
	
	private static final int distToUpper	= 10;
	private static final int distToLeft		= 10;
	private static final int distToRight	= 10;
	private static final int distToLower	= 10;
	
	private static final int calTopTextSize = 10;
	private static final int calTopTaskSize = 30;
	private static final int calTopSpaceSize = calTopTaskSize + calTopTextSize;
	
	private static final int calLeftTextSize = 40;
	private static final int calLeftSpaceSize = calLeftTextSize;
	
	private static final int calDayVSlicerLineWidth = 2;
	private static final int calDayHSlicerLineWidth = 1;
	private static final int calOuterLineWidth = 2; 
	private static final int calInnerLindDashFormat = SWT.LINE_DASH;
	private static final int calInnerLindSolidFormat = SWT.LINE_SOLID;
	
	private static final int calTimeTextFontFormat = SWT.BOLD;
	private static final int calTimeTextFontSize = 14;
	private static final String calTimeTextFontName = "Arial";
	private static final int calStartDay = Calendar.SUNDAY;
	
	// for colour scheme
	private static Color calColorBackGround;
	private static Color calColorLineColor;
	private static Color calColorTask;
	
	/**
	 * This would reset the time interval, and redraw
	 * Still imperfect, so make it private first
	 * @param newVal
	 * @return true on succeed, false on not
	 */
	private boolean setTimeInterval(int newVal){
		if(60%newVal != 0){
			return false;
		}
		this.timeInterval = newVal;
		
		this.redraw();
		return true;
	}
	
	private static void timeReset(Calendar time){
		if(time == null){
			time = Calendar.getInstance();
		}
		
		// regenerate the set of time string
		time.set(Calendar.HOUR_OF_DAY, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
	}
	
	
	private Calendar getCalStartTime(){
		if(this.calStartTime != null){
			return (Calendar) this.calStartTime.clone();
		} else{	// initialize the cal
			this.calStartTime= Calendar.getInstance();
			int dayOfWeek = this.calStartTime.get(Calendar.DAY_OF_WEEK);
			if(dayOfWeek - dayNum != 0){
				calStartTime.set(Calendar.DAY_OF_WEEK,calStartDay);
			}
			
			timeReset(this.calStartTime);
			return (Calendar) this.calStartTime.clone();
		}
	}
	
	public CalView(Composite parent, int style) {
		super(parent, style);
		
		// initialize the color scheme
		calColorBackGround = this.getDisplay().getSystemColor(SWT.COLOR_CYAN);
		calColorLineColor = this.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		calColorTask = this.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
		
		tasklist = new LinkedList<TaskList>();
		currentTime = Calendar.getInstance();
		
		Rectangle bounds = parent.getBounds();
		System.out.println(bounds);
		this.setSize(bounds.height - distToUpper - distToLower,bounds.width - distToLeft - distToRight);
		this.setLocation(bounds.x,bounds.y);
		
//		ScrollBar sb = 
		
		// end of initialization
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				CalView.this.paintControl(e);
			}
		});
	}
	
	private void paintControl(PaintEvent e){
		GC gc = e.gc;
		
		
		Point size = this.getSize();
		
		
		// draw the days
		int spaceW = size.x - calLeftSpaceSize;
		int spaceH = size.y - calTopSpaceSize;
		int calTimeStartPosX = calLeftSpaceSize/3;
		int calDateStartPosY = calTopSpaceSize/3;
		
		// the seven days
		int verticalPartNum = 24*60/timeInterval;
		double intervalX = ((double)spaceW)/dayNum;
		double intervalY = ((double)spaceH)/verticalPartNum;
		int smallIntervalY = 60/timeInterval;
		int startX = calLeftSpaceSize;	// the relative coordinate
		int startY = calTopSpaceSize;
		int endX = startX + spaceW;
		int endY = startY + spaceH;
		
		double dayPosX[];	// starting X position of each vertical line
		double hourPosY[];	// starting X position of each vertical line
		
		gc.setBackground(calColorBackGround);gc.setForeground(calColorLineColor);
		gc.setLineWidth(calOuterLineWidth);
		gc.fillRectangle(startX, startY, spaceW, spaceH);
		gc.drawRectangle(startX, startY, spaceW, spaceH);
		
		// then start to draw lines
		gc.setForeground(calColorLineColor);	// set the color of the line
			// vertical line
		gc.setLineWidth(calDayVSlicerLineWidth);
			// this dayPosX would possibly be used later
		dayPosX = new double[dayNum];dayPosX[0] = startX;
		for(int i=1;i<dayNum;i++){
			dayPosX[i] = dayPosX[i-1] + intervalX;
			gc.drawLine((int)dayPosX[i], startY, (int)dayPosX[i], endY);
		}
			// the horizontal date information
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM,dd,EEE");
		Calendar startCalTime = this.getCalStartTime();
		for(int i=0;i<dayNum;i++){
			int posX = (int)(dayPosX[i]);// + intervalX/2);
			int posY = calTopTextSize;	//TODO: decide this later
			gc.drawString(
//					String.format(
//					CalView.dateFormat,
//					time.get(GregorianCalendar.DAY_OF_MONTH),
//					time.get(GregorianCalendar.MONTH),
//					time.get(GregorianCalendar.DAY_OF_WEEK)),
					dateFormat.format(startCalTime.getTime()),
					posX,posY);
			startCalTime.add(Calendar.DAY_OF_YEAR, 1);
		}
		
			// horizontal line
		hourPosY = new double[verticalPartNum];
		gc.setLineWidth(calDayVSlicerLineWidth);
		startCalTime = this.getCalStartTime();
		startCalTime.set(Calendar.DATE, currentTime.get(Calendar.DATE));
		
		// create a list of labels for drawing
		double tempY = startY;
		for(int i=0;i<verticalPartNum+1;i++){
			if(i<verticalPartNum){
				hourPosY[i] = tempY;
			}

			gc.drawString(String.format(
					timeFormat, 
					startCalTime.get(Calendar.HOUR_OF_DAY),
					startCalTime.get(Calendar.MINUTE)),
					calTimeStartPosX,(int)((double)tempY - ((double)calTimeTextFontSize)/2));
			startCalTime.add(Calendar.MINUTE, this.timeInterval);
			
			tempY += intervalY;
		}
		tempY = startY;
		for(int i=0;i<verticalPartNum;i++){
			if(smallIntervalY == 0 || i%smallIntervalY == 0){	// the hour difference
				gc.setLineStyle(calInnerLindSolidFormat);
			} else{ // difference within hour -- dash line
				gc.setLineStyle(calInnerLindDashFormat);
			}
			gc.drawLine(startX, (int)tempY, endX, (int)tempY);
			tempY += intervalY;
		}
		
		// then put in the task
		for(TaskList list:tasklist){
			for(Task task:list.getTasks()){
			// assume there's no multi-day task
			Date startTime = task.getStartDateTime();
			Date endTime = task.getEndDateTime();
			String title = task.getName();
			
			if(timeOutOfScope(startTime,endTime)){
				// TODO: deleted later
				System.err.println("Out of range\n\t" + title + ": " + startTime + ", " + endTime);
				continue;
			}
			
			
			// dayPosX
			// hourPosY
			
			// then draw the task
				// first decide on the date
			long startTimeStamp = startTime.getTime();
			long endTimeStamp = endTime.getTime();
			long calStartTimeStampOfAll = getCalStartTime().getTimeInMillis();
			int dayIndex = (int)((startTimeStamp - calStartTimeStampOfAll)/1000/3600/24);
			
				// then on the exact time
			long calStartTimeStampOfThatDay = calStartTimeStampOfAll + 1000*3600*24*dayIndex;
			long diff = (startTimeStamp - calStartTimeStampOfThatDay)/1000;
			
			int startXPos = (int) dayPosX[dayIndex];
			int startYPos = startY + (int) ((double)diff/60/timeInterval * intervalY);
			
			diff = (endTimeStamp - startTimeStamp)/1000;
			int taskDurationPix = (int) (diff/60/timeInterval * intervalY);
			
			gc.setBackground(calColorTask);
			gc.fillRectangle(startXPos, startYPos, (int)intervalX, taskDurationPix);
			
			// then write the task title
			int textYPos = startYPos;
			gc.setForeground(calColorLineColor);
			gc.setBackground(calColorBackGround);
			gc.drawText(title, startXPos + 10, textYPos);	// TODO: resolve here.. a bit of hacking
			}
		}
		
	}
	
	private boolean timeOutOfScope(Date startTime, Date endTime) {
		Calendar startDate = getCalStartTime();
		Date dStartDate = startDate.getTime();
		
		// try to get the end date here
		startDate.add(Calendar.DATE, dayNum +1);
		startDate.add(Calendar.SECOND, -1);
		Date dEndDate = startDate.getTime();
		
		if(startTime.before(dStartDate) || endTime.after(dEndDate)
			|| endTime.getDate() != startTime.getDate() || endTime.before(startTime)){
			return true;
		}
		return false;
	}

	public void addList(TaskList list){
		tasklist.add(list);
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		
		// generate basic T class for testing
		Calendar now = Calendar.getInstance();
		Calendar later = (Calendar) now.clone();
		later.add(Calendar.HOUR, 1);
		Task t1 = new Task();
		TaskList l = new TaskList("test List");
		t1.setName("Task 1");
		t1.setStartDateTime(now.getTime());
		t1.setEndDateTime(later.getTime());
		l.addTask(t1);
		
		// create the calendar view
		CalView view = new CalView(shell, SWT.NONE);
		view.addList(l);
		view.setLocation(5,5);
		//view.setTimeInterval(360);
		
		shell.setSize(1024, 768);
		shell.setAlpha(250);

		
		shell.open();
		
		
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
			}
		}
	}
}
