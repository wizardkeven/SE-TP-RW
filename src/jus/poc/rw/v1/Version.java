package jus.poc.rw.v1;

import java.text.SimpleDateFormat;
import java.util.Date;

import sun.text.resources.FormatData_fr_FR;
import jus.poc.rw.Actor;
import jus.poc.rw.Reader;
import jus.poc.rw.Resource;
import jus.poc.rw.Writer;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Version extends Resource{
	

	public Version(IDetector detector, IObservator observator) {
		super(detector, observator);
		init(null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginR(Actor reader) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		if (reader instanceof Reader) {
			reader.lock.readLock().lock();
			
		}else {
			return;
		}
		reader.setAcquireTime(new Date());
		System.out.println(observator.toString()+" constate que "+reader.getName()+
				" commence à lire "+toString()+ " à "+reader.getAcquireTime());
		
		
	}

	@Override
	public void beginW(Actor writer) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		if (writer instanceof Writer) {
			writer.lock.writeLock().lock();
		}else {
			return;
		}
		writer.setAcquireTime(new Date());
		System.out.println(observator.toString()+" constate que "+writer.getName()+
				" commence à écrire "+toString()+ " à "+ writer.getAcquireTime());

	}

	@Override
	public void endR(Actor reader) throws InterruptedException {
		// TODO Auto-generated method stub
		reader.setReleaseTime(new Date());
		reader.PrintMessage("lire");
		reader.lock.readLock().unlock();
	}

	@Override
	public void endW(Actor writer) throws InterruptedException {
		// TODO Auto-generated method stub
		writer.setReleaseTime(new Date());
		writer.PrintMessage("écrire");
		writer.lock.writeLock().unlock();
	}
	
	}
