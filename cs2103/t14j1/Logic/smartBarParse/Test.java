package cs2103.t14j1.Logic.smartBarParse;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	/**
	 * Use this to test the regular expression
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "toMatch";
		String res[] = str.toLowerCase().split("m"); 
		
		PrintStream out = System.out;
		
//		for(int i=0;i<res.length;i++){
//			out.println(res[i]);
//		}
		
		
		Scanner in = new Scanner(System.in);
		
		// first need a pattern
		Pattern pattern = 
        Pattern.compile("\\d\\d\\d");
		
		// then need a matcher
		Matcher matcher = 
        pattern.matcher("");
		
		String a = " +=.";
		String res1[] = a.split("\\ ");
		
		
		for(int i=0;i<res1.length;i++){
			System.out.println(res1[i]);
		}
		
//		boolean found = false;
//    while (matcher.find()) {
//        System.out.format("I found the text \"%s\" starting at " +
//           "index %d and ending at index %d.%n",
//            matcher.group(), matcher.start(), matcher.end());
//        found = true;
//    }
//    if(!found){
//        System.out.format("No match found.%n");
//    }
	}

}
