package cs2103.t14j1.taskmeter.calendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.eclipse.swt.widgets.Shell;

class T{
	String name;
	Date startTime;
	Date endTime;
	
	T(String name, Date startTime, Date endTime){
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}

class TaskItem extends Composite{

	public TaskItem(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	
}

public class CalView extends Composite{
	
	// for drawing
	private GregorianCalendar time;
	private List<String> viewTimeStr;
	
	private int dayNum = 7;
	private int timeInterval = 30;	// time interval, should be a divider of 60
	
	
	// internal data
	private List<T> tasks;
	
	// for formating
	private static final String timeFormat = "%2d:%2d";
	private static final String dateFormat = "%s,%s.%s";
	
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
	
	// for colour scheme
	private static Color calColorBackGround;
	private static Color calColorLineColor;
	private static Color calColorTask;
	
	/**
	 * This would reset the time interval, and redraw
	 * @param newVal
	 * @return true on succeed, false on not
	 */
	public boolean setTimeInterval(int newVal){
		if(60%newVal != 0){
			return false;
		}
		this.timeInterval = newVal;
		
		this.redraw();
		return true;
	}
	
	private void timeReset(boolean resetDate){
		if(resetDate){
			time = new GregorianCalendar();
		}
		
		// regenerate the set of time string
		time.set(GregorianCalendar.HOUR_OF_DAY, 0);
		time.set(GregorianCalendar.MINUTE, 0);
		time.set(GregorianCalendar.SECOND, 0);
	}
	
	private void initializeStartingDate(){
		this.time= new GregorianCalendar();
		int dayOfAWeek = time.get(GregorianCalendar.DAY_OF_WEEK);
		if(dayOfAWeek - dayNum != 0){
			time.set(GregorianCalendar.DAY_OF_WEEK, 0);
		}
		
		timeReset(false);
	}
	
	CalView(Composite parent, int style) {
		super(parent, style);
		
		// initialize the color scheme
		calColorBackGround = this.getDisplay().getSystemColor(SWT.COLOR_CYAN);
		calColorLineColor = this.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		calColorTask = this.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
		tasks = new LinkedList<T>();
		time = new GregorianCalendar();
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
		int startX = calLeftSpaceSize;
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
		initializeStartingDate(); SimpleDateFormat dateFormat = new SimpleDateFormat("MMM,dd,EEE");
		for(int i=0;i<dayNum;i++){
			int posX = (int)(dayPosX[i]);// + intervalX/2);
			int posY = calTopTextSize;	//TODO: decide this later
			gc.drawString(
//					String.format(
//					CalView.dateFormat,
//					time.get(GregorianCalendar.DAY_OF_MONTH),
//					time.get(GregorianCalendar.MONTH),
//					time.get(GregorianCalendar.DAY_OF_WEEK)),
					dateFormat.format(time.getTime()),
					posX,posY);
			time.add(GregorianCalendar.DAY_OF_YEAR, 1);
		}
		
			// horizontal line
		hourPosY = new double[verticalPartNum];
		gc.setLineWidth(calDayVSlicerLineWidth);timeReset(false);
		
		// create a list of labels for drawing
		double tempY = startY;
		for(int i=0;i<verticalPartNum+1;i++){
			if(i<verticalPartNum){
				hourPosY[i] = tempY;
			}

			gc.drawString(String.format(
					timeFormat, 
					time.get(GregorianCalendar.HOUR_OF_DAY),
					time.get(GregorianCalendar.MINUTE)),
					calTimeStartPosX,(int)((double)tempY - ((double)calTimeTextFontSize)/2));
			time.add(GregorianCalendar.MINUTE, this.timeInterval);
			
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
		initializeStartingDate();
		for(T task:tasks){
			// assume there's no multi-day task
			Date startTime = task.startTime;
			Date endTime = task.endTime;
			String title = task.name;
			
			if(timeOutOfScope(startTime,endTime)){
				continue;
			}
			
			// dayPosX
			// hourPosY
			
			// then draw the task
				// first decide on the date
			long startTimeStamp = startTime.getTime();
			long endTimeStamp = endTime.getTime();
			long calStartTimeStamp = time.getTimeInMillis();
			int dayIndex = (int)((startTimeStamp - endTimeStamp)/1000/3600/24);
				// then on the exact time
			calStartTimeStamp += 1000*3600*24*dayIndex;
			long diff = (startTimeStamp - calStartTimeStamp)/1000;
			int startIndex = (int) (diff/60/timeInterval);
			int startYShift = (int) ((double)diff/60/timeInterval * intervalY);
			diff = (endTimeStamp - calStartTimeStamp)/1000;
			int endYShift = (int) ((double)diff/60/timeInterval * intervalY);
			int startYPos = (int) (hourPosY[startIndex] + startYShift);
			int startXPos = (int) dayPosX[dayIndex];
			
			// seriously... start drawing :)
			gc.setBackground(calColorTask);
			gc.fillRectangle(startXPos, startYPos, (int)intervalX, startYShift);
			
			// then write the task title
			int textYPos = startYPos + startYShift/2;
			gc.setForeground(calColorLineColor);
			gc.setBackground(calColorBackGround);
			gc.drawText(title, startXPos + 10, textYPos);	// TODO: resolve here.. a bit of hacking
		}
		
	}
	
	private boolean timeOutOfScope(Date startTime, Date endTime) {
		GregorianCalendar endDate = (GregorianCalendar) time.clone();
		endDate.add(GregorianCalendar.DATE, dayNum);
		if(startTime.before(time.getTime()) || endTime.after(endDate.getTime())
			|| endTime.getDate() != startTime.getDate() || endTime.before(startTime)){
			return true;
		}
		return false;
	}

	void addTask(T task){
		tasks.add(task);
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		
		// generate basic T class for testing
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar later = (GregorianCalendar) now.clone();
		later.add(GregorianCalendar.HOUR, 1);
		T t1 = new T("Task 1", now.getTime(), later.getTime());
		CalView view = new CalView(shell, SWT.NONE);
		view.addTask(t1);
		view.setSize(900, 700);
		view.setLocation(5,5);
		
		shell.setSize(1024, 768);
		shell.setAlpha(250);

		
		shell.open();
				
		shell.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle rect = shell.getClientArea();
				
				int edgeX = 5, edgeY = 5;
				
				int wordsX = edgeX + 20, wordsY = edgeY + 10;
				int beforeViewY = wordsX + 30;
				
				int viewX = rect.x + wordsX;
				int viewY = rect.y + wordsY + beforeViewY;
				
				int viewW = rect.width;
				int viewH = rect.width;
				
				/*GC gc = e.gc;
				gc.drawText("Lulla", wordsX, wordsY, SWT.DRAW_DELIMITER); */
				
			}
		});
		
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
			}
		}
	}
}
