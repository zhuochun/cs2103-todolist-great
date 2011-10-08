package cs2103.t14j1.logic;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateFormatTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetNow() {
        Date now = DateFormat.getNow();
        
        System.out.println(DateFormat.dateToStrLong(now));
    }

    @Test
    public void testStrToDateLong() {
        fail("Not yet implemented");
    }

    @Test
    public void testStrToDateShort() {
        fail("Not yet implemented");
    }

    @Test
    public void testStrToDateString() {
        String date = "2010-12-20";
        
        Date d = DateFormat.strToDate(date);
        
        System.out.println(DateFormat.dateToStr(d));
        System.out.println(DateFormat.dateToStrLong(d));
    }

    @Test
    public void testStrToDateStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testDateToStrLong() {
        fail("Not yet implemented");
    }

    @Test
    public void testDateToStrDate() {
        fail("Not yet implemented");
    }

    @Test
    public void testDateToStrDateString() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetMinuteAfter() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetDateAfter() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsLeapYear() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetDaysInMonth() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsSameWeekDates() {
        fail("Not yet implemented");
    }

}
