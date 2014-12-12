package jus.poc.rw;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.v1.Version;

import java.util.concurrent.locks.*;

import com.sun.xml.internal.bind.v2.runtime.Name;

public class Reader extends Actor{
	
	Version disk=null;
	ReentrantReadWriteLock readerLock = null;
	double acquireTime = 0; //  le temps de debut d'accéder à la ressouce
	double occupyTime = 0; // le temps total d'usage
	

	public Reader(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw,
			IResource[] selection, IObservator observator, ReentrantReadWriteLock lock, Resource res) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator);
		// TODO Auto-generated constructor stub
		this.readerLock =  lock;
		this.disk = (Version) res;
	}



	@Override
	protected void acquire(IResource resource) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		//disk = (Version) resource;
		
//		try {
			//while (true) {
				readerLock.readLock().lock(); //vérrouiller
				Version.setCurrentUser(this.getName()); //une fois obtenir la droit de y'acceder, on déclare l'occupation actuelle.
				acquireTime = System.currentTimeMillis();
				System.out.println("Je suis "+ getName()+ " dont ID est "
				+ getId() + " en train de lire: "+ disk.getMsg()+ "à "+ acquireTime);
//				
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		this.wait(timeout);
		this.acquire(disk);
	}



	@Override
	protected void release(IResource resource) throws InterruptedException {
		// TODO Auto-generated method stub
		occupyTime = System.currentTimeMillis() - acquireTime;
		System.out.println("Je l'ai occupé pendant: " + occupyTime); // Calculer le temps total d'utiliser le ressource
		readerLock.readLock().unlock();// dévérouiller
	}

}
