package jus.poc.rw;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.v1.Version;

public class Writer extends Actor{

	
	private ReentrantReadWriteLock writerLock = null;
//	double acquireTime = 0; //  le temps de debut d'accéder à la ressouce
//	double occupyTime = 0; // le temps total d'usage
	private Version resLocal;
	
	public Writer(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw,
			IResource[] selection, IObservator observator, ReentrantReadWriteLock readWriteLock) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator, readWriteLock);
		// TODO Auto-generated constructor stub
		writerLock = (ReentrantReadWriteLock) readWriteLock;
	
	}

	@Override
	protected void acquire(IResource resource) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		writerLock.writeLock().lock();
		resLocal = (Version) resource;
		Version.setCurrentUser(this.getName()); //une fois obtenir la droit de y'acceder, on déclare l'occupation actuelle.
		resLocal.setAcquireTime(new Date());
		System.out.println("Je suis "+ getName()+ " dont ID est "
				+ getId() + "en train de lire: "+ resLocal.getMsg()+ " à "+ resLocal.getAcquireTime());
	}

	@Override
	protected void release(IResource resource) throws InterruptedException {
		// TODO Auto-generated method stub
		resLocal.setReleaseTime(new Date());;
		System.out.println("Je l'ai occupé pendant: " + resLocal.getReleaseTime()); // Calculer le temps total d'utiliser le ressource
		writerLock.writeLock().unlock();
		
	}

}
