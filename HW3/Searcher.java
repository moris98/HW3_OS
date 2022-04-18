/**
 * A searcher thread. Searches for files with a given prefix in all directories listed in a directory queue.
 *
 */
import java.io.File;

public class Searcher implements Runnable{
    private long id;
    private String prefix;
    private SynchronizedQueue<File> directoryQueue;
    private  SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> auditingQueue;
    private boolean isAudit;

    public Searcher(String prefix, SynchronizedQueue<File> directoryQueue, SynchronizedQueue<File> resultsQueue, SynchronizedQueue<String> auditingQueue, boolean isAudit){
        this.prefix=prefix;
        this.directoryQueue=directoryQueue;
        this.resultsQueue=resultsQueue;
        if(isAudit){
            this.auditingQueue=auditingQueue;
            this.auditingQueue.registerProducer();
        }
        this.isAudit=isAudit;
        this.resultsQueue.registerProducer();
    }

    public void run() {
        this.id=Thread.currentThread().getId();
        File currentDirectory =this.directoryQueue.dequeue();
        while(currentDirectory!=null){
            File[] files = currentDirectory.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().indexOf(this.prefix)==0) {
                    this.resultsQueue.enqueue(file);
                    if(this.isAudit) {
                        auditingQueue.enqueue("Searcher on thread id " + this.id + ": file named " + file.getName() + " was found");
//                        System.out.println("Searcher on thread id " + this.id + ": file named " + file.getName() + " was found");
                    }
                }
            }
            currentDirectory = this.directoryQueue.dequeue();
        }
        this.directoryQueue.unregisterProducer();
        this.resultsQueue.unregisterProducer();
        if(this.isAudit) {
            this.auditingQueue.unregisterProducer();
        }

    }
}
