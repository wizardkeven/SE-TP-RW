package jus.poc.rw.v1;

import java.text.SimpleDateFormat;
import java.util.Date;

import sun.text.resources.FormatData_fr_FR;
import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Version extends Resource{

	private static String msg = "Resource Version 1"; //Generate a message in the first version
	private static String currentUser;
	private String acquireTime; //  le temps de debut d'accéder à la ressouce
	private String releaseTime; // le temps total d'usage
	SimpleDateFormat formatDate;
	

	public Version(IDetector detector, IObservator observator) {
		super(detector, observator);
		// TODO Auto-generated constructor stub
		init(null);
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
	}

	@Override
	public void beginR(Actor arg0) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginW(Actor arg0) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endR(Actor arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endW(Actor arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static String getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(String currentUser) {
		Version.currentUser = currentUser;
	}

	public String getAcquireTime() {
		return acquireTime;
	}

	public void setAcquireTime(Date acquireTime) {
		this.acquireTime = formatDate.format(acquireTime);
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date endingTime) {
//		this.releaseTime = formatDate.format(endingTime.compareTo(Date.parse(acquireTime)));
//		formatDate.f
	}


}
