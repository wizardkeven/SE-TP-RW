package jus.poc.rw.v2;

import java.util.Date;

import jus.poc.rw.Actor;
import jus.poc.rw.Reader;
import jus.poc.rw.Resource;
import jus.poc.rw.Writer;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;
import jus.poc.rw.v1.Version1;

public class Version2 extends Version1{

	protected volatile int currentReadNb = 0; //counter of readers accumulated by now
	protected volatile int write = 0; // counter of writers being executing in the system
	protected volatile int read = 0; // counter of readers being executing in the system 
	private volatile boolean writersFinished = false;
	private volatile int finishedWritingNb=0;
	private volatile int finishedReadingNb=0;
	private boolean readersFinished = false;
	
	public Version2(IDetector detector, IObservator observator) {
		super(detector, observator);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public synchronized void beginR(Actor reader) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
//		System.out.println("version2 reads");
		if (reader instanceof Reader) { //For assuring the right call
			
			while(write>0){
				wait(); //If there is a writer executing, readers should wait in line
			}
			
		}else {
			System.out.println("Abnormal read request!");
			return;
		}
		read++;//add 1 reader in current system
		reader.setAcquireTime(new Date());//mark the current time stamp
//		System.out.println(observator.toString()+" constate que "+reader.getName()+
//				" commence à lire "+toString()+ " à "+reader.getAcquireTime());		
	}

	@Override
	public synchronized void beginW(Actor writer) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		
		if (readersFinished&&!writersFinished) { // if 
			for
		}
		if (writer instanceof Writer) {
		
			while (currentReadNb < minNbReader || write > 0 || read > 0){
				
				wait(); //if there are at least one reader or writer or not enough readers taking actions wait
			}
		}else {
			return;
		}
		write++;
		writer.setAcquireTime(new Date()); //mark the current time stamp
//		System.out.println(observator.toString()+" constate que "+writer.getName()+
//				" commence à écrire "+toString()+ " à "+ writer.getAcquireTime());
	}

	@Override
	public synchronized void endR(Actor reader) throws InterruptedException {
		// TODO Auto-generated method stub
		reader.setReleaseTime(new Date());		
		read--; // current read finished its action and quit
		increaseCurrentReadNb(reader); //add one reading entry
		
		if (reader.getNbIteration()-reader.getAccessRank()==0) { //if a reader finishes its task and quit, this counter increases by 1
			finishedReadingNb++;
			if (finishedReadingNb == minNbReader) {
				readersFinished  = true;
			}
		}
		if (currentReadNb >= minNbReader && read == 0) {
			notifyAll();
		}
		reader.PrintMessage("lire"+ "read: "+read+" write: "+write+" currentReadernb "+ currentReadNb);
	}

	@Override
	public synchronized void endW(Actor writer) throws InterruptedException {
		// TODO Auto-generated method stub
		writer.setReleaseTime(new Date());
		write--;
		increaseCurrentReadNb(writer);
		if (writer.getNbIteration()-writer.getAccessRank()==0) { //if a reader finishes its task and quit, this counter increases by 1
			finishedWritingNb++;
			if (finishedWritingNb == minNbReader) {
				writersFinished  = true;
			}
		}
		notifyAll();
		writer.PrintMessage("écrire "+ "read: "+read+" write: "+write+" currentReadernb "+ currentReadNb);
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	public int getCurrentReadNb() {
		return currentReadNb;
	}

	public void setCurrentReadNb(int currentReadNb) {
		this.currentReadNb = currentReadNb;
	}
	
	/*
	 * every call from reader will increase the reading number by 1
	 * every call from writer will decrease the reading number to 0
	 * synchronized makes sure the atomicity of the adding action on the read nb
	 */
	synchronized void increaseCurrentReadNb(Actor actor){
		if (actor instanceof Reader) {
			currentReadNb++;
		}else if (actor instanceof Writer) {
			currentReadNb=0;
		}else {
			System.out.println("unknown actor type accedes!");
			return;
		}
		
	}
	
}
