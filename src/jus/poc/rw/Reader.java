package jus.poc.rw;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.v1.Version;

import java.util.Date;
import java.util.concurrent.locks.*;

import com.sun.xml.internal.bind.v2.runtime.Name;

public class Reader extends Actor{
	
//	Version disk=null;
	private ReentrantReadWriteLock readerLock = null;
//	private Version resLocal;
	
	

	public Reader(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw,
			IResource[] selection, IObservator observator, ReentrantReadWriteLock readWriteLock) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator, readWriteLock);
		// TODO Auto-generated constructor stub
		this.readerLock =  (ReentrantReadWriteLock) readWriteLock;
		
	}



	@Override
	protected void acquire(IResource resource) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		//disk = (Version) resource;
		
//		try {
			//while (true) {
//		resLocal = (Version) resource;
				readerLock.readLock().lock(); //vérrouiller
				observator.acquireResource(this, resPre);
				Version.setCurrentUser(this.getName()); //une fois obtenir la droit de y'acceder, on déclare l'occupation actuelle.
				resPre.setAcquireTime(new Date());
				AcquireTime = resPre.getAcquireTime();
//				System.out.println(getName()+ " dont ID est "
//				+ getId() + " en train de lire: "+resLocal.getMsg()  + " à "+ resLocal.getAcquireTime());
//				
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		super.run();
//		this.wait(timeout);
//		this.acquire(disk);
//	}
//


	@Override
	protected void release(IResource resource) throws InterruptedException {
		// TODO Auto-generated method stub
		resPre.setReleaseTime(new Date());
		releaseTime = resPre.getReleaseTime();
		PrintMessage("read");
		observator.releaseResource(this, resPre);
		readerLock.readLock().unlock();// dévérouiller
	}

}
