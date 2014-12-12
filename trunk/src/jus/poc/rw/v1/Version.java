package jus.poc.rw.v1;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Version extends Resource{

	private static String msg = "Resource Version 1"; //Generate a message in the first version
	private static String currentUser;
	

	public Version(IDetector detector, IObservator observator) {
		super(detector, observator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
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


}
