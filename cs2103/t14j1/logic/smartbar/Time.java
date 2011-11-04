package cs2103.t14j1.logic.smartbar;


/**
 * @author Song Yangyu
 * A time wrapper for the time
 * Creating this wrapper for internal use now; it can be move to the task
 *  package if others found it useful
 */
class Time implements Comparable<Time>{
	private Long time = null;
	Time(Long time){
		this.time = time;
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public void setTime(Integer time){
		this.time = (long)time;
	}
	
	public Long getTime(){
		return this.time;
	}
	
	public int compareTo(Time b){
		return (int)(this.getTime() - b.getTime());
	}
}