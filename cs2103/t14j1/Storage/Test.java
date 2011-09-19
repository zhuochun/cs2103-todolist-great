package cs2103.t14j1.Storage;

import java.io.IOException;

public class Test {
	public static void main(String args[])throws IOException {
		Task t = new Task(null, 1, null, null, null, null, null, null);
		System.out.println(t.getValue("priority"));
		Task u = new Task(null, 2, null, null, null, null, null, null);
		TaskList q = new TaskList();
		q.add(t);
		q.add(u);
		System.out.println(q.searchTask("priority", "2"));
	}
}
