package jus.poc.rw;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sun.nio.cs.ext.IBM037;
import jus.poc.rw.Aleatory;
import jus.poc.rw.control.ControlException;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.control.Observator;
import jus.poc.rw.deadlock.Detector;
import jus.poc.rw.v1.*;

/**
 * Main class for the Readers/Writers application. This class firstly creates a pool of read/write resources  
 * implementing interface IResource. Then it creates readers and writers operating on these resources.
 * @author P.Morat & F.Boyer
 */
public class Simulator{
	protected static final String OPTIONFILENAME = "option.xml";
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
	
//	Version res; // Resources to be used
	static Detector detector; // detector unused in v1
	static Observator observator;
	static ResourcePool rePool;
	static ReentrantReadWriteLock readWriteLock;
	
	
	
	/**
	 * make a permutation of the array
	 * @param array the array to be mixed
	 */
	protected static void mixe(Object[] array) {
		int i1, i2;
		Object a;
		for(int k = 0; k < 2 * array.length; k++){
			i1 = Aleatory.selection(1, array.length)[0];
			i2 = Aleatory.selection(1, array.length)[0];
			a = array[i1]; array[i1] = array[i2]; array[i2] = a;
		}
	}
	/**
	 * Retreave the parameters of the application.
	 * @param file the final name of the file containing the options. 
	 */
	protected static void init(String file) {
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
		Properties option = new Properties("jus/poc/rw/opt/"+file);
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
	}
	public static void main(String... args) throws Exception{
		// set the application parameters
		init((args.length==1)?args[0]:OPTIONFILENAME);
		//to be completed
		Version1.minNbReader=nbReaders;
		Version1.minNbWriter=nbWriters;
		detector = new Detector();
		observator = new Observator(new Observator(null));
		observator.init(nbReaders+nbWriters, nbResources);
		rePool = new ResourcePool(nbResources, detector, observator,version);
		Aleatory aleaUsingReader = new Aleatory(readerAverageUsingTime, readerDeviationUsingTime);
		Aleatory aleaVacanReader = new Aleatory(readerAverageVacationTime, readerDeviationVacationTime);
		Aleatory aleaUsingWiriter = new Aleatory(writerAverageUsingTime, writerDeviationUsingTime);
		Aleatory aleaVacanWriter = new Aleatory(writerAverageVacationTime, writerDeviationVacationTime);
		Aleatory aleaIterationWriter = new Aleatory(writerAverageIteration, writerDeviationIteration);
		
		Actor[] readers = new Actor[nbReaders];
		Actor[] writers = new Actor[nbWriters];
		readWriteLock = new ReentrantReadWriteLock();
		for (int i = 0; i < nbReaders; i++) {
			readers[i] = new Reader(aleaUsingReader, aleaVacanReader,aleaIterationWriter, 
					rePool.selection(1), observator,readWriteLock);
//			IResource[] resVersion= rePool.selection(1);
//			readers[i].acquire(resVersion[0]);
			readers[i].start();
//			System.out.println(readers[i].getName() + " of ID: " + readers[i].getId());
		}
		for (int i = 0; i < nbWriters; i++) {
			writers[i] = new Writer(aleaUsingWiriter, aleaVacanWriter, 
					aleaIterationWriter, rePool.selection(1), observator,readWriteLock);
//			IResource[] resVersion= rePool.selection(1);
//			writers[i].acquire(resVersion[0]);
			writers[i].start();
//			System.out.println(writers[i].getName() + " of ID: " + writers[i].getId());
		}
		System.out.println("Il y a "+nbReaders+" readers, "+nbWriters+" writers et "+nbResources+" resources");
		
		
//		ExecutorService rWManager = Executors.newCachedThreadPool() ;
		
		
		//action and iteration to be completed
		
//		Version res = new Version(detector, observator) ;
		
		
		
	}
}
