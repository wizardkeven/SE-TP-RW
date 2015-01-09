package jus.poc.rw.v3;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jus.poc.rw.Actor;
import jus.poc.rw.Reader;
import jus.poc.rw.Resource;
import jus.poc.rw.Writer;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;
import jus.poc.rw.v2.Version2;

public class Version3 extends Version2 {

	
//	protected ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	protected Lock rwConditionalLock = new ReentrantLock();
	protected Condition onWritingCondition = rwConditionalLock.newCondition(); // condition tested for writing
	protected Condition onReadingCondition = rwConditionalLock.newCondition(); // condition tested for reading
//	protected Condition minNbReaderAccessCondition = rwConditionalLock.newCondition(); // lock to control minimal readers access before a writer can write
	protected Condition writerPriorityCondition = rwConditionalLock.newCondition(); // condition to  guarantee the high priority writer accede before a lower one when two or more writers waiting
	protected Condition writerFinishedWaitingCreateReaderCondition= rwConditionalLock.newCondition();
	//	protected Map<Actor, Integer> writerPriorityMap = new HashMap();
	protected volatile boolean currentWritingPriority = false;
	public Version3(IDetector detector, IObservator observator) {
		super(detector, observator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beginR(Actor reader) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		
		if (reader instanceof Reader) { //For assuring the right call

//			System.out.println("before read lock "+ reader.getName()+rwConditionalLock.toString()+ rwLock.readLock().toString()+"\n");
			rwConditionalLock.lock();
			
			
//			System.out.println(reader.getName()+" is waiting for writer with a higher priority finishing! "+currentWritingPriority+" \n");
//			try {
				while(write>0){				
					onWritingCondition.await();
				}
				
				while(getCurrentWritingPrio()){
					System.out.println(reader.getName()+" is waiting for writer with a higher priority finishing! \n");
					writerPriorityCondition.await();
				}
				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}finally{
				read++;
				reader.setAcquireTime(new Date());//mark the current time stamp
//				rwConditionalLock.unlock();
//			}
			
		}else {
			System.out.println("Abnormal read request!");
			return;
		}
		

		
	}

	@Override
	public void beginW(Actor writer) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		if (writer instanceof Writer) {
//			try {
				rwConditionalLock.lock();
				while(read>0){
					onReadingCondition.await();
				}
				
				while(write>0){
					onWritingCondition.await();
				}
				
//				System.out.println(writer.getName()+" is waiting for writer with a higher priority finishing! "+currentWritingPriority+" \n");

				if (writer.getPriority() == Thread.MAX_PRIORITY) {
					System.out.println(writer.getName()+currentWritingPriority+" with a higher priority passing!"+rwConditionalLock.toString());
					currentWritingPriority = true;
//					rwConditionalLock.unlock();
//				rwConditionalLock.notifyAll();					
//					System.out.println(writer.getName()+"sets "+currentWritingPriority+" \n");
//				rwConditionalLock.unlock();
				}
//				while (!rwConditionalLock.tryLock()) {
//					
//				}
			
				while(getCurrentWritingPrio() && !(writer.getPriority() == Thread.MAX_PRIORITY)){
					System.out.println(writer.getName()+" is waiting for writer with a higher priority finishing! \n");
					writerPriorityCondition.await();
					System.out.println(writer.getName()+" passes for writer with a higher priority finished! \n");
				}
			
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}finally{
//			
				write++;
				writer.setAcquireTime(new Date()); //mark the current time stamp
//				rwConditionalLock.unlock();
//			}
			

//			rwConditionalLock.tryLock();
		}else {
			System.out.println("Wrong actor calling! This is available for Writer! \n");
			return;
		}
		
	}

	/**
	 * @param currentValue 
	 * 
	 */
	protected void setCurrentWritingPrio(boolean currentValue) {
			currentWritingPriority = currentValue;
	}

	protected boolean getCurrentWritingPrio() {
		return currentWritingPriority;
	}
	@Override
	public void endR(Actor reader) throws InterruptedException {
		// TODO Auto-generated method stub
//		rwConditionalLock.lock();
//		try {
			reader.setReleaseTime(new Date());				
			read--;
			onReadingCondition.signal();
			
//		System.out.println(reader.getName()+ " timeLeft "+timeLeft
//				+ " readersinished "+readersFinished+" currentReadernb " + currentReadNb  + writersFinished  +" finishedReadingNb "+finishedReadingNb
//				+" totalCurrentReaderNb "+totalCurrentReaderNb+ " readerFiniWriterWait "+ readerFiniWriterWait+" iteNb "+(reader.getNbIteration() - reader.getAccessRank())+" "+write);
			reader.PrintMessage("lire"+ " readersinished "+readersFinished+" currentReadernb " + currentReadNb);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
			rwConditionalLock.unlock();
//		}


		
	}

	@Override
	public void endW(Actor writer) throws InterruptedException {
		// TODO Auto-generated method stub
//		rwConditionalLock.lock();
		
//		try {
			writer.setReleaseTime(new Date());
			
			write--;
			onWritingCondition.signal();
			
//		System.out.println(writer.getName()+ " time left "+leftTimes+" maxTime "+timeLeftWriter+ 
//				" currentReadernb "+ currentReadNb+ " writersFinished "+writersFinished+" finishedReadingNb "+finishedReadingNb+" totalCurrentReaderNb "+totalCurrentReaderNb+" readersfinished "+readersFinished+ " readerFiniWriterWait "+ readerFiniWriterWait +"\n");
			writer.PrintMessage("écrire "+writer.getPriority()+ " time left "+timeLeftWriter+" currentReadernb "+ currentReadNb +" "+ Thread.MAX_PRIORITY);	
			
			
			
			if (getCurrentWritingPrio()) {
				
				currentWritingPriority = false;
				writerPriorityCondition.signal();
			}
				

//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
			rwConditionalLock.unlock();
//		}
		
		
	}

	@Override
	public void init(Object arg0) throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	public boolean isCurrentWritingPriority() {
		return currentWritingPriority;
	}

	public void setCurrentWritingPriority(boolean currentWritingPriority) {
		this.currentWritingPriority = currentWritingPriority;
	}

}
