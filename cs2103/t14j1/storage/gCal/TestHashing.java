package cs2103.t14j1.storage.gCal;

import java.util.Date;
import java.util.TreeMap;


import cs2103.t14j1.storage.When;

public class TestHashing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreeMap<When,Boolean> treeMap = new TreeMap<When,Boolean>();
		When a = new When();
		When b = new When();
		When c = new When();
		b.setStartDateTime(new Date(12300000));
		b.setEndDateTime(new Date());
		c.setStartDateTime(new Date(12300000));
		c.setEndDateTime(new Date());
//		c.setEndDateTime(new Date(123));
		/*c.setStartDateTime(new Date(12345));
		b.setStartDateTime(new Date(42123));
		treeMap.put(a, true);*/
		treeMap.put(b, false);
		Boolean res = treeMap.get(c);
		
		
		
		System.out.println(treeMap.get(c));
	}

}
