package cs2103.t14j1.logic.smartbar;

import java.util.Calendar;

/**
 * For a given time String, this would parse and return the corresponding 
 * date and time
 * @author SongYY
 *
 */
class DateTimeProcessor extends RegexMatcher{
	
	private String operatingDateTimeStr;
	private DateTime dateTime = new DateTime();
	private Calendar dateToSubstituteOnNoDateStr;
	
	private boolean dateTimeProecssed = false;
	
	public DateTimeProcessor(String dateTimeStr) {
		super(dateTimeStr);
		this.operatingDateTimeStr	= dateTimeStr;
	}
	
	public boolean setDateToSubstituteOnNoDateStr(Calendar givenDate){
		if(this.dateToSubstituteOnNoDateStr != null)	return false;
		
		this.dateToSubstituteOnNoDateStr = givenDate;
		return true;
	}
	
	public DateTime getDateTime(){
		if(dateTimeProecssed){
			return this.dateTime;
		}
	
		processTime();
		processDate();
		dateTimeProecssed = true;
		return this.dateTime;
	}
	
	private void processTime() {
		if(regexMatchesWholeWorldAndSaveMatchedStr(regTimeFormat)){
			this.dateTime.setTime(regGetTimeFromPureTimeStr(matchedStr));
			removeMatchedStringFromOperatingStr();
		}
	}
	
	/**
	 * Date format: 
	 *  1) dd/[mm|M](/yy(yy)?)?		where M means Jan~Dec, or January ~ December;
	 *  	"/" here can also be " "
	 *  2) today, tomorrow
	 *  3) Mon ~ Sun, or Monday ~ Sunday --> indicates the next day.
	 *  4) next Mon ~ Sun, or Monday ~ Sunday --> indicates the day of next week 
	 */
	private void processDate() {
		if(regexMatchesWholeWorldAndSaveMatchedStr(regDateFormat_M_dd_$yyyy$)){
			this.dateTime.setDate(dateFormat_M_dd_$yyyy$_Process(
					removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr)));
			removeMatchedStringFromOperatingStr();
		} else if(regexMatchesWholeWorldAndSaveMatchedStr(regDateFormat_dd_$mm$M$_$yy$yy$$)){
			this.dateTime.setDate(dateFormat_dd_$mm$M$_$yy$yy$$_Process(
					removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr)));
			removeMatchedStringFromOperatingStr();
		}else if(regexMatchesWholeWorldAndSaveMatchedStr(regDateFormat_today_tomorrow)){
			this.dateTime.setDate(regDateFormat_today_tomorrow_Process(
					removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr)));
			removeMatchedStringFromOperatingStr();
		} else if(regexMatchesWholeWorldAndSaveMatchedStr(regDateFormat_order_weekD)){
			this.dateTime.setDate(regDateFormat_order_weekD_Process(
					removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr)));
			removeMatchedStringFromOperatingStr();
		}
	}

	private static Calendar dateFormat_M_dd_$yyyy$_Process(String pureDateStr) {
		pureDateStr = removeLeadingFromStrOnIfExist(pureDateStr);
		String[] dateInfoParam = pureDateStr.split(regexDateSpacer);
		
		String yearStrToBeProcessed = null;
		if(dateInfoParam.length > 2) yearStrToBeProcessed = dateInfoParam[2];
		
		return generateDateFromDayMonthYearGiven(dateInfoParam[1],dateInfoParam[0],yearStrToBeProcessed); 
	}
	
	private static Calendar dateFormat_dd_$mm$M$_$yy$yy$$_Process(String pureDateStr) {
		pureDateStr = removeLeadingFromStrOnIfExist(pureDateStr);
		String[] dateInfoParam = pureDateStr.split(regexDateSpacer);
		
		String yearStrToBeProcessed = null;
		if(dateInfoParam.length > 2) yearStrToBeProcessed = dateInfoParam[2];
		
		return generateDateFromDayMonthYearGiven(dateInfoParam[0],dateInfoParam[1],yearStrToBeProcessed);
	}
	
	private static Calendar generateDateFromDayMonthYearGiven(String dayStr,
			String monthStr, String yearStr) {
		Calendar date = Calendar.getInstance();
		date.clear();
		processIntDateStr(date,dayStr);
		processMonthStr(date,monthStr);
		processIntYear(date,yearStr);
		
		return date;
	}

	private void removeMatchedStringFromOperatingStr() {
			if(matchedStr == null)	return;
			this.operatingDateTimeStr = this.operatingDateTimeStr.replace(matchedStr, " ");
		}
	
	private static void processIntYear(Calendar date, String yearStr) {
		int year = 0;
		if(yearStr != null){
			year = Integer.parseInt(yearStr);
			
			if(year < 50){	// form of dd/mm/10 -> should be 2010
				year += 2000;
			}else if(year < 100){	// form of dd/mm/65 -> should be 1965
				year += 1900;
			}
		}else{	// not specified, default to this year
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		date.set(Calendar.YEAR, year);
	}

	private static void processMonthStr(Calendar date, String inStr) {
		// parse month
		int monthNum = dateParseGetMonth(inStr);
		if(monthNum == -1)	outputErr("Error with parsing month string.");
		else	date.set(Calendar.MONTH,monthNum);
	}

	private static void processIntDateStr(Calendar dateOperatingOn,String inStr) {
		String dayStr = removeOrderedWordsFromStrIfExist(inStr);
		dateOperatingOn.set(Calendar.DATE, Integer.parseInt(dayStr));
	}

	private static String removeOrderedWordsFromStrIfExist(String datStr) {
		// eliminate the "st/nd/rd/th" which specifies the order
		if(datStr.length() > 2 && Character.isLetter(datStr.charAt(2))){
			datStr = datStr.substring(0, datStr.length()-2);
		}
		return datStr;
	}

	private static String removeLeadingFromStrOnIfExist(String inStr) {
		// remove the leading "on " if it exist
		if(inStr.length() > 3 && inStr.substring(0, 3).equalsIgnoreCase("on ")){
			inStr = inStr.substring(3);
		}
		
		return inStr;
	}

	private boolean regexMatchesWholeWorldAndSaveMatchedStr(String regex) {
		return regexMatchedStrAndSaveMatchedStr(
				regWordSpacer + regex + regWordSpacer, 
				this.operatingDateTimeStr, 
				IGNORE_CASE);
	}

	/**
	 * The time string must strictly follows
	 *  1) hh:mm:ss (am|pm)?
	 *  2) hh:mm (am|pm)?
	 *  3) hh (am|pm)
	 */
	protected static Long regGetTimeFromPureTimeStr(String pureTimeStr){
		long time = 0;	//new Long(0); 	// initialization
		pureTimeStr = pureTimeStr.toLowerCase();
		
		if(pureTimeStr.contains("at "))	pureTimeStr = pureTimeStr.replace("at ", "");
		
		// capture the [a|p]m. 
		if(pureTimeStr.contains("pm"))	time += DateTime.HOUR_PER_HALF_DAY * DateTime.SEC_PER_HOUR;
		
		// remove the am/pm tag
		String[] purifiedTime = pureTimeStr.split("[a|p]m");
		pureTimeStr = purifiedTime[0];
		
		// get the time parameter
		String[] timeParam = pureTimeStr.split(":");
		
		long amplifier = 3600;
		try{
			for(int i=0;i<timeParam.length;i++,amplifier/=60){
				time += Long.parseLong(timeParam[i].trim()) * amplifier;
			}
		} catch(NumberFormatException e){
			outputErr("Error in processing time format: Num Handling");
			e.printStackTrace();
		}
		
		return time;
	}

	static Calendar regDateFormat_order_weekD_Process(String weekDStr) {
		Calendar date = Calendar.getInstance();
		
		// spacer is here
		String[] dayInfo = weekDStr.split(regWordSpacer);
		weekDStr = dayInfo[dayInfo.length - 1];
		
		boolean nextWeek = false;
		boolean hasThis = false;
		
		if(dayInfo.length > 1 && dayInfo[dayInfo.length - 2].matches("next")){
			nextWeek = true;
		} else if(dayInfo.length > 1 && dayInfo[dayInfo.length - 2].matches("this")){
			hasThis = true;
		}
		
		int dayOfTheWeek = dateParseGetDayOfWeekFromText(weekDStr);
		
		int currentDay = date.get(Calendar.DAY_OF_WEEK);
		
		if(currentDay == -1){	// failure in day parsing
			return null;
		}
		
		/* say today is Wednesday
		 * 
		 * Next Saturday: the Saturday of next week
		 * This Saturday: the Saturday of this week
		 * This Monday: the Monday of this week
		 * Monday   => next Monday
		 * Saturday => next Saturday
		 * 
		 */
		if(nextWeek || (!hasThis && currentDay > dayOfTheWeek)){	// indicating the day of next week
			date.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
			date.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		} else{	// indicating this week. if on the same date, it's still this week
			date.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		}
		return date;
	}

	static Calendar regDateFormat_today_tomorrow_Process(String today_tomorrow) {
		Calendar date = Calendar.getInstance();
		if(today_tomorrow.compareTo("today") == 0){
			// do nothing here
		} else if(today_tomorrow.compareTo("tomorrow") == 0 || today_tomorrow.compareTo("tmr") == 0){
			date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) + 1);
		} else{
			return null;
		}
		
		return date;
	}
	
	private static int dateParseGetMonth(String month){
		int monthNum = 0;
		try{
			// if can parse successful, just return the parse value
			monthNum = Integer.parseInt(month);
			monthNum--;	// cuz the month in Calendar is starting with 0
		} catch (NumberFormatException e) {
			// if it's not integer, then it's text format --> need to translate
			return dateParseGetMonthFromText(month);
		}
		
		return (monthNum<12)?monthNum:-1;	// return -1 on false
	}
	
	
	/**
	 * For a given String specifying the month, change it to the "month" of int
	 * according to the format of Calendar
	 * @param month
	 * @return
	 */
	static int dateParseGetMonthFromText(String month){
		if(month.equalsIgnoreCase("Jan") || 
			 month.equalsIgnoreCase("January")){
			return Calendar.JANUARY;
		} else if(month.equalsIgnoreCase("Feb") || 
				 month.equalsIgnoreCase("February")){
			return Calendar.FEBRUARY;
		} else if(month.equalsIgnoreCase("Mar")|| 
				 month.equalsIgnoreCase("March")){
			return Calendar.MARCH;
		} else if(month.equalsIgnoreCase("Apr")|| 
				 month.equalsIgnoreCase("April")){
			return Calendar.APRIL;
		} else if(month.equalsIgnoreCase("May")){ 
			return Calendar.MAY;
		} else if(month.equalsIgnoreCase("Jun")|| 
				 month.equalsIgnoreCase("June")){
			return Calendar.JUNE;
		} else if(month.equalsIgnoreCase("Jul")|| 
				 month.equalsIgnoreCase("July")){
			return Calendar.JULY;
		} else if(month.equalsIgnoreCase("Aug")|| 
				 month.equalsIgnoreCase("August")){
			return Calendar.AUGUST;
		} else if(month.equalsIgnoreCase("Sept")|| 
				 month.equalsIgnoreCase("September")){
			return Calendar.SEPTEMBER;
		} else if(month.equalsIgnoreCase("Oct")|| 
				 month.equalsIgnoreCase("October")){
			return Calendar.OCTOBER;
		} else if(month.equalsIgnoreCase("Nov") || 
				 month.equalsIgnoreCase("November") ){
			return Calendar.NOVEMBER;
		} else if(month.equalsIgnoreCase("Dec") || 
				 month.equalsIgnoreCase("December")){
			return Calendar.DECEMBER;
		} else{
			return -1;
		}
	}
	
	/** For a given String specifying the day of a week, change it to the int 
	 *  with Calendar type 
	 * @param day
	 * @return
	 */
	static int dateParseGetDayOfWeekFromText(String day) {
		
		if(day.substring(0, 3).compareToIgnoreCase("Mon") == 0){
			return Calendar.MONDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Tue") == 0){
			return Calendar.TUESDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Wed") == 0){
			return Calendar.WEDNESDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Thu") == 0){
			return Calendar.THURSDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Fri") == 0){
			return Calendar.FRIDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Sat") == 0){
			return Calendar.SATURDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Sun") == 0){
			return Calendar.SUNDAY;
		} else{
			outputErr("Error in day parsing, String: " + day.substring(0,3));
			return -1;
		}
		
	}
}
