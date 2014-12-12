/**
 * <i>java</i> 
 * <i>utilities</i> 
 * for <i>students</i>
 */
package jus.poc.rw.v1;

import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;

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
	/**
	 * Constructor
	 * @param useLaw the gaussian law for using delay
	 * @param vacationLaw the gaussian law for the vacation delay
	 * @param iterationLaw the gaussian law for the number of iteration do do
	 * @param selection the resources to used
	 * @param observator th observator of the comportment
	 */
	public Actor(Aleatory useLaw, Aleatory vacationLaw, Aleatory iterationLaw, IResource[] selection, IObservator observator){
		this.ident = identGenerator++;
		resources = selection;
		this.useLaw = useLaw;
		this.vacationLaw = vacationLaw;
		nbIteration=iterationLaw.next();
		setName(getClass().getSimpleName()+"-"+ident());
		this.observator=observator;
	}
	/**
	 * the behavior of an actor accessing to a resource.
	 */
	public void run(){
		// to be completed
		for(accessRank=1; accessRank!=nbIteration; accessRank++) {
			temporizationVacation(vacationLaw.next());
			acquire();
			temporizationUse(useLaw.next());
			release();
		}
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
	 */
	private void acquire(){
		// to be completed
	}
	/**
	 * the release stage of the resources prevously acquired
	 */
	private void release(){
		// to be completed
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
}