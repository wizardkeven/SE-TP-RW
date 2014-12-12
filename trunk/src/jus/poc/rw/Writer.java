package jus.poc.rw;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.v1.Version;

public class Writer extends Actor{

	
	Version disk=null;
	ReentrantReadWriteLock writerLock = null;
	double acquireTime = 0; //  le temps de debut d'accéder à la ressouce
	double occupyTime = 0; // le temps total d'usage
	
	public Writer(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw,
			IResource[] selection, IObservator observator, ReentrantReadWriteLock lock) {
		super(useLaw, vacationLaw, iterationLaw, selection, observator);
		// TODO Auto-generated constructor stub
		writerLock = lock;
	
	}

	@Override
	protected void acquire(IResource resource) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		writerLock.writeLock().lock();
		disk = (Version) resource;
		Version.setCurrentUser(this.getName()); //une fois obtenir la droit de y'acceder, on déclare l'occupation actuelle.
		acquireTime = System.currentTimeMillis();
		System.out.println("Je suis "+ getName()+ " dont ID est "
				+ getId() + "en train de lire: "+ disk.getMsg()+ "à "+ acquireTime);
	}

	@Override
	protected void release(IResource resource) throws InterruptedException {
		// TODO Auto-generated method stub
		occupyTime = System.currentTimeMillis() - acquireTime;
		System.out.println("Je l'ai occupé pendant: " + occupyTime); // Calculer le temps total d'utiliser le ressource
		writerLock.writeLock().unlock();
		
	}

}
