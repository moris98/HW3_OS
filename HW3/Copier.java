/**
 * A Copier thread. goes over the queue made by the Searcher and copies the files to another destiantioon.
 *
 */
import java.io.*;

public class Copier implements Runnable{
    private long id;
    private File destination;
    private SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> auditingQueue;
    private boolean isAudit;

    public Copier(File destination, SynchronizedQueue<File> resultsQueue, SynchronizedQueue<String> auditingQueue, boolean isAudit){
        this.destination=destination;
        this.resultsQueue=resultsQueue;
        if(isAudit) {
            this.auditingQueue = auditingQueue;
            this.auditingQueue.registerProducer();
        }
        this.isAudit=isAudit;
    }

    public void run() {
        this.id=Thread.currentThread().getId();
        File currentFileToCopy =this.resultsQueue.dequeue();
        while(currentFileToCopy!=null){
            if(currentFileToCopy.renameTo(new File(this.destination+"/"+currentFileToCopy.getName()))){
                if(this.isAudit) {
                    auditingQueue.enqueue("Copier from thread id " + this.id + ": file named " + currentFileToCopy.getName() + " was copied");
//                    System.out.println("Copier from thread id " + this.id + ": file named " + currentFileToCopy.getName() + " was copied");
                }
                currentFileToCopy.delete();
            }
            currentFileToCopy = this.resultsQueue.dequeue();
        }
        this.resultsQueue.unregisterProducer();
        if(this.isAudit){
            this.auditingQueue.unregisterProducer();
        }
    }
}
