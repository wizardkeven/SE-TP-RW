package jus.poc.rw.v2;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jus.poc.rw.Actor;
import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.Reader;
import jus.poc.rw.Resource;
import jus.poc.rw.ResourcePool;
import jus.poc.rw.Writer;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;
import jus.poc.rw.v1.Version1;

public class Version2 extends Version1{
	
	/*
	 * get paras from xml
	 */
	
	protected static final String OPTIONFILENAME = "option2.xml";
	/** the version of the protocol to be used */
	protected static String version;
	/** the number of readers involve in the simulation */
	protected static int nbReaders;
	/** the number of writers involve in the simulation */
	protected static int nbWriters;
	/** the number of resources involve in the simulation */
	protected static int nbResources;
	/** the number of resources used by an actor */
	protected static int nbSelection;
	/** the law for the reader using delay */
	protected static int readerAverageUsingTime;
	protected static int readerDeviationUsingTime;
	/** the law for the reader vacation delay */
	protected static int readerAverageVacationTime;
	protected static int readerDeviationVacationTime;
	/** the law for the writer using delay */
	protected static int writerAverageUsingTime;
	protected static int writerDeviationUsingTime;
	/** the law for the writer vacation delay */
	protected static int writerAverageVacationTime;
	protected static int writerDeviationVacationTime;
	/** the law for the writer number of iterations */
	protected static int writerAverageIteration;
	protected static int writerDeviationIteration;
	/** the chosen policy for priority */
	protected static String policy;

	protected volatile int currentReadNb = 0; //counter of readers accumulated by now
	protected volatile int timeLeftWriter = 0; //current writers' number in the system
	protected volatile int write = 0; // counter of writers being executing in the system
	protected volatile int read = 0; // counter of readers being executing in the system 
	private  boolean writersFinished = false;
	private volatile int finishedWritingNb=0;
	private volatile int finishedReadingNb=0;
	private  boolean readersFinished = false;
	private volatile boolean rwFinished=false;
	private volatile boolean remainOneWriter = false;
	volatile boolean tempFlag = true;
	private volatile int totalCurrentReaderNb = minNbReader;
	int nbGeneReaders = 0;
//	private boolean generateNewReader = false;
	private Map<Actor, Integer> writerTimeLeftMap;
	Aleatory aleaUsingReader;
	Aleatory aleaVacanReader;
	Aleatory aleaIterationWriter;
	volatile int readerFiniWriterWait = 0;
	
	public Version2(IDetector detector, IObservator observator) {
		super(detector, observator);
		// TODO Auto-generated constructor stub
		getParaFromXML();
		writerTimeLeftMap = new HashMap<Actor, Integer>();
	}

	private void getParaFromXML() {
		// TODO Auto-generated method stub
		// Retrieve the parameters of the application
				final class Properties extends java.util.Properties {
					private static final long serialVersionUID = 1L;
					public int get(String key){return Integer.parseInt(getProperty(key));}
					public Properties(String file) {
						try{
							loadFromXML(ClassLoader.getSystemResourceAsStream(file));
						}catch(Exception e){e.printStackTrace();}			
					}
				}
				Properties option = new Properties("jus/poc/rw/opt/"+OPTIONFILENAME);
				version = option.getProperty("version");
				nbReaders = Math.max(0,new Aleatory(option.get("nbAverageReaders"),option.get("nbDeviationReaders")).next());
				nbWriters = Math.max(0,new Aleatory(option.get("nbAverageWriters"),option.get("nbDeviationWriters")).next());
				nbResources = Math.max(0,new Aleatory(option.get("nbAverageResources"),option.get("nbDeviationResources")).next());
				nbSelection = Math.max(0,Math.min(new Aleatory(option.get("nbAverageSelection"),option.get("nbDeviationSelection")).next(),nbResources));
				readerAverageUsingTime = Math.max(0,option.get("readerAverageUsingTime"));
				readerDeviationUsingTime = Math.max(0,option.get("readerDeviationUsingTime"));
				readerAverageVacationTime = Math.max(0,option.get("readerAverageVacationTime"));
				readerDeviationVacationTime = Math.max(0,option.get("readerDeviationVacationTime"));
				writerAverageUsingTime = Math.max(0,option.get("writerAverageUsingTime"));
				writerDeviationUsingTime = Math.max(0,option.get("writerDeviationUsingTime"));
				writerAverageVacationTime = Math.max(0,option.get("writerAverageVacationTime"));
				writerDeviationVacationTime = Math.max(0,option.get("writerDeviationVacationTime"));
				writerAverageIteration = Math.max(0,option.get("writerAverageIteration"));
				writerDeviationIteration = Math.max(0,option.get("writerDeviationIteration"));
				policy = option.getProperty("policy");
				
				aleaUsingReader = new Aleatory(readerAverageUsingTime, readerDeviationUsingTime);
				aleaVacanReader = new Aleatory(readerAverageVacationTime, readerDeviationVacationTime);
				aleaIterationWriter = new Aleatory(writerAverageIteration,writerDeviationIteration);
				
	}

	@Override
	public synchronized void beginR(Actor reader) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
//		System.out.println("version2 reads");
		if (reader instanceof Reader) { //For assuring the right call
//			if (rwFinished) { //if system has finished enough read operation after the ending of the last writers, all the rest threads will do nothing
//				reader.stop();
//				return;
//			}
			while(write>0){
				wait(); //If there is a writer executing, readers should wait in line
			}
			
		}else {
			System.out.println("Abnormal read request!");
			return;
		}
		read++;
		reader.setAcquireTime(new Date());//mark the current time stamp
//		increaseCurrentReadNb(reader); //add one reading entry
		
//		System.out.println(observator.toString()+" constate que "+reader.getName()+
//				" commence à lire "+toString()+ " à "+reader.getAcquireTime());		
	}

	@Override
	public synchronized void beginW(Actor writer) throws InterruptedException,
			DeadLockException {
		// TODO Auto-generated method stub
		
//		if (readersFinished && !writersFinished) { // if the all the readers have ended their operations by writers not, we'll get minNbReader - currentReadNb new reader threads launched
//			generateReaders(minNbReader - currentReadNb);
//		}
		if (writer instanceof Writer) {
			readerFiniWriterWait++;
			if(!writerTimeLeftMap.containsKey(writer)){
				writerTimeLeftMap.put(writer, writer.getNbIteration()-writer.getAccessRank());
			}
			
			while (currentReadNb < minNbReader || write > 0 || read > 0){
				
//				if (readersFinished) {
//					
//					System.out.println("I am "+writer.getName()+" ready to create " );
//					timeLeftWriter = writer.getNbIteration() - writer.getAccessRank();
//					CallToGenerateReaders();
////					tempFlag =false;
//				}
				wait(); //if there are at least one reader or writer or not enough readers taking actions wait
//				break;
			}
			
		}else {
			return;
		}
		
//		if(generateNewReader){
//			wait();
//			generateNewReader = false;
//		}
		readerFiniWriterWait--;
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
		currentReadNb++;
		
		if (!readersFinished) {
			
			if (reader.getNbIteration() - reader.getAccessRank() == 1) { //if a reader finishes its task and quit, this counter increases by 1
				finishedReadingNb++;
				if ((finishedReadingNb == totalCurrentReaderNb) && !writersFinished) {// && readerFiniWriterWait != 0
					readersFinished = true;
					
					System.out.println("Readers created empty! "+readersFinished+ writersFinished+
							" finishedReadingNb "+finishedReadingNb+" totalCurrentReaderNb "+totalCurrentReaderNb+" timeLeftWriter "+timeLeftWriter+"\n\n");
//					if (generateNewReader) {
//					timeLeftWriter-=currentReadNb;
					
//					if (!remainOneWriter) { // if remains just one writer waiting, we leave creation work for writer
						tempFlag = CallToGenerateReaders();
//					}
				}
			}

		}else {
			finishedReadingNb++;
			if (writersFinished && (finishedReadingNb +1== totalCurrentReaderNb) && currentReadNb <= minNbReader) {
				System.out.println("this is the end of the execution! " + 
			readersFinished+" "+ writersFinished+ " "+ currentReadNb + " timeLeft "+(reader.getNbIteration() - reader.getAccessRank())+ minNbReader);
			}
		}
		
//		
//		if (currentReadNb >= minNbReader && read == 0 && !readersFinished) {
//			notifyAll();
//		}else {
//			notifyAll();
//		}
		
		int timeLeft = reader.getNbIteration() - reader.getAccessRank();
//		
//		if (writersFinished && currentReadNb == minNbReader) {
//			generateNewReader = true;
//		}
//
//		System.out.println(reader.getName()+ " timeLeft "+timeLeft
//				+ " readersinished "+readersFinished+" currentReadernb " + currentReadNb  + writersFinished  +" finishedReadingNb "+finishedReadingNb
//				+" totalCurrentReaderNb "+totalCurrentReaderNb+ " readerFiniWriterWait "+ readerFiniWriterWait+" iteNb "+(reader.getNbIteration() - reader.getAccessRank())+" "+write);
		reader.PrintMessage("lire" + " timeLeft "+timeLeft
				+ " readersinished "+readersFinished+" currentReadernb " + currentReadNb);

		if ( writersFinished && timeLeftWriter == 1 && currentReadNb>= minNbReader && readerFiniWriterWait == 0 && timeLeft == 1 ) { //if all the writers finished and there have been enough readers followed, system stops
			rwFinished = true;
		} 
		
		if (rwFinished) { //if all the read and write finished
			System.out.println("All the operations done, system quits!");
		}
		
		notifyAll();
	}
	

	
	@Override
	public synchronized void endW(Actor writer) throws InterruptedException {
		// TODO Auto-generated method stub
		writer.setReleaseTime(new Date());
		write--;
		currentReadNb=0;
		if ((writer.getNbIteration()-writer.getAccessRank()==1)&&!writersFinished) { //if a writer finishes its task and will quit, this counter increases by 1
			finishedWritingNb++;
			if (finishedWritingNb == minNbWriter) {// if all the writers have executed their operations, we'll say the writers finish
				writersFinished  = true;
//				if (generateNewReader) {
//					wait();
//					generateReaders(1, this,0,1);
//				}
				
			}
		}
		
		
		int leftTimes = writer.getNbIteration() - writer.accessRank();
//		int temp = 0;
		timeLeftWriter = 0;
		writerTimeLeftMap.put(writer,leftTimes);
		Iterator<Integer> iterator = writerTimeLeftMap.values().iterator();
		while (iterator.hasNext()) {
//			int temp1 = iterator.next();
			
			timeLeftWriter += iterator.next();	
		}
		
		notifyAll();
		
		while (writersFinished && !(finishedReadingNb == totalCurrentReaderNb)) {//&& !writer.iterationFinished
				wait();
		}
		
		if (writersFinished && finishedReadingNb == totalCurrentReaderNb) {
			generateReaders(1, this,0,1);
//			System.out.println("I am here to generate the last \n");
		}
		
		
//		System.out.println(writer.getName()+ " time left "+leftTimes+" maxTime "+timeLeftWriter+ 
//				" currentReadernb "+ currentReadNb+ " writersFinished "+writersFinished+" finishedReadingNb "+finishedReadingNb+" totalCurrentReaderNb "+totalCurrentReaderNb+" readersfinished "+readersFinished+ " readerFiniWriterWait "+ readerFiniWriterWait +"\n");
		writer.PrintMessage("écrire "+ " time left "+timeLeftWriter+" currentReadernb "+ currentReadNb);
	}
	
	/**
	 * when no more readers available in system
	 * call this method to deal with generating new readers 
	 */
	protected synchronized boolean CallToGenerateReaders() {
		
//		if(readerFiniWriterWait == 0 && currentReadNb >0){
////			readerFiniWriterWait = 1;
////			remainOneWriter = true;
//			return false; 
//		}
//		System.out.println("timeLeftWriter "+timeLeftWriter+ " currentReadNb "+ currentReadNb);
		nbGeneReaders = minNbReader*timeLeftWriter;// - currentReadNb;
		int n1 =minNbReader*timeLeftWriter/4;
		int n2 = nbGeneReaders - minNbReader*timeLeftWriter*3/4 ;//>=0)&&(currentReadNb<=minNbReader)?nbGeneReaders - minNbReader*timeLeftWriter*3/4:0;
		
		for (int i = 0; i < 3; i++) {
			generateReaders(1, this, 1,
					n1);
		}
			generateReaders(1, this, 1,
					n2);
			notifyAll();
			return true;
////					}
	}


	/*
	 * called only if the system doesn't work as the requirements 
	 */
	public void generateReaders(int nbNewReaders, Resource resource, int nbR,int nbIte) {
		// TODO Auto-generated method stub
		Actor[] newReaders = new Reader[nbNewReaders];
		IResource[] res = new Version2[2];
		res[0] = resource;
		if (nbNewReaders == 0 || nbIte == 0) {
			return;
		}
		for (int i = 0; i < nbNewReaders; i++) {
			if (nbR == 1) {
				newReaders[i] = new Reader(aleaUsingReader, aleaVacanReader,
						new Aleatory(nbIte, 0), res, observator,
						new ReentrantReadWriteLock());
//				System.out.println("Generate "+ nbNewReaders+" newReaders "+ nbIte+" each times");
			}else{
				newReaders[i] = new Reader(aleaUsingReader, aleaVacanReader,
					new Aleatory(minNbReader+1, 0), res, observator,
					new ReentrantReadWriteLock());
			}
			newReaders[i].start();
			readersFinished = false;
			totalCurrentReaderNb += nbNewReaders;
//			System.out.println("thread finished, create: "+ newReaders[i].getName());
//			Actor.yield();
//			generateNewReader = false;
			
		}
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
