/**
 * <i>java</i> 
 * <i>utilities</i> 
 * for <i>students</i>
 */
package jus.poc.rw;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jus.poc.rw.Aleatory;
import jus.poc.rw.IResource;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.v1.Version1;

/**
 * Define the global behavior of an actor in Reader/writer protocol.
 * @author morat 
 */
public abstract class Actor extends Thread{
	private static int identGenerator=0;
	/** the identificator of the actor */
	protected int ident;
	/** le pool of resources to be used */
	protected IResource[] resources;
	/** the ramdomly service for use delay*/
	protected Aleatory useLaw;
	/** the ramdomly service for vacation delay*/
	protected Aleatory vacationLaw;
	/** the observator */
	protected IObservator observator;
	/** the number of iteration to do, -1 means infinity */
	protected int nbIteration;
	/** the rank of the last access done or under execution */
	protected int accessRank;
	
	protected Resource resPre;//une resource utilisé pour objectif 1
	private int occupyTime;
	private String AcquireTime; 
	SimpleDateFormat formatDate; //date formatting tool
	public ReentrantReadWriteLock lock;//lock for all members
	private String releaseTime;
	public boolean iterationFinished = false;
	
	
	
	/**
	 * Constructor
	 * @param useLaw the gaussian law for using delay
	 * @param vacationLaw the gaussian law for the vacation delay
	 * @param iterationLaw the gaussian law for the number of iteration do do
	 * @param selection the resources to used
	 * @param observator th observator of the comportment
	 */
	public Actor(Aleatory useLaw, Aleatory vacationLaw, 
			Aleatory iterationLaw, IResource[] selection, IObservator observator,ReentrantReadWriteLock readWriteLock){
		this.ident = identGenerator++;
		resources = selection;
		this.useLaw = useLaw;
		this.vacationLaw = vacationLaw;
		nbIteration=iterationLaw.next();
		setName(getClass().getSimpleName()+"-"+ident());
		this.observator=observator;
		formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		lock = readWriteLock;
	}
	/**
	 * the behavior of an actor accessing to a resource.
	 */
	public void run(){
		// to be completed
		observator.startActor(this);
		System.out.println(observator.toString()+" see that "+getName()+" is starting! \n");
		resPre = (Resource) resources[0];
		for(accessRank=1; accessRank!=nbIteration; accessRank++) {
			temporizationVacation(vacationLaw.next());
			try {
				acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DeadLockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setOccupyTime(useLaw.next());
			temporizationUse(getOccupyTime());
			try {
				release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (nbIteration == 1) {
			temporizationVacation(vacationLaw.next());
			try {
				acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DeadLockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setOccupyTime(useLaw.next());
			temporizationUse(getOccupyTime());
			try {
				release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		observator.stopActor(this);
		int time = nbIteration - accessRank;
		iterationFinished = true;
		System.out.println(observator.toString()+" see that "+getName()+" stops! "+time+"\n");
	}
	/**
	 * the temporization for using the ressources.
	 */
	private void temporizationUse(int delai) {
		try{Thread.sleep(delai);}catch(InterruptedException e1){e1.printStackTrace();}		
	}
	/**
	 * the temporization between access to the resources.
	 */
	private void temporizationVacation(int delai) {
		try{Thread.sleep(delai);}catch(InterruptedException e1){e1.printStackTrace();}		
	}
	/**
	 * the acquisition stage of the resources.
	 * @throws DeadLockException 
	 * @throws InterruptedException 
	 */
	private void acquire() throws InterruptedException, DeadLockException{
		// to be completed
		//Resource res = (Resource) resources[0]; 
		observator.requireResource(this, resPre);
		acquire(resPre);
	}
	/**
	 * the release stage of the resources prevously acquired
	 * @throws InterruptedException 
	 */
	private void release() throws InterruptedException{
		// to be completed
		observator.releaseResource(this, resPre);
		release(resPre);
		
	}
	/**
	 * Restart the actor at the start of his execution, having returned all the resources acquired.
	 * @param resource the resource at the origin of the deadlock.
	 */
	protected void restart(IResource resource) {
		// to be completed
	}
	/**
	 * acquisition proceeding specific to the type of actor.
	 * @param resource the required resource
	 * @throws InterruptedException
	 * @throws DeadLockException
	 */
	protected abstract void acquire(IResource resource) throws InterruptedException, DeadLockException;
	/**
	 * restitution proceeding specific to the type of actor.
	 * @param resource
	 * @throws InterruptedException
	 */
	protected abstract void release(IResource resource) throws InterruptedException;
	/**
	 * return the identification of the actor
	 * return the identification of the actor
	 */
	public final int ident(){return ident;}
	/**
	 * the rank of the last access done or under execution.
	 * @return the rank of the last access done or under execution
	 */
	public final int accessRank(){return accessRank;}
	
	public void PrintMessage(String action){
		System.out.println( getName() + " commence à "+action+": " + " à "+ AcquireTime+" et fini à: "+
				releaseTime+" pendant: "+occupyTime+" ms"+"\n");//+resPre.toString()+ " ID: "+ getId() 
	}
	
	public String getAcquireTime() {
		return AcquireTime;
	}
	public void setAcquireTime(Date date) {
		AcquireTime = formatDate.format(date);
	}
	
	public String getReleaseTime() {
		return releaseTime;
	}
	public void setReleaseTime(Date date) {
		this.releaseTime = formatDate.format(date);
	}
	public int getOccupyTime() {
		return occupyTime;
	}
	public void setOccupyTime(int occupyTime) {
		this.occupyTime = occupyTime;
	}
	public int getNbIteration() {
		return nbIteration;
	}
	public int getAccessRank() {
		return accessRank;
	}
	public Resource getResPre() {
		return resPre;
	}
	
}
