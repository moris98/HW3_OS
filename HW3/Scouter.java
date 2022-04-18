/**
 * A scouter thread This thread lists all sub-directories from a given root path.
 * Each sub-directory is enqueued to be searched for files by Searcher threads.
 *
 */
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class Scouter implements Runnable{
    private long id;
    private SynchronizedQueue<File> directoryQueue;
    private File root;
    private SynchronizedQueue<String> auditingQueue;
    boolean isAudit;

    public Scouter(SynchronizedQueue<File> directoryQueue, File root, SynchronizedQueue<String> auditingQueue, boolean isAudit){
        this.directoryQueue=directoryQueue;
        this.root=root;
        if(isAudit) {
            this.auditingQueue = auditingQueue;
            this.auditingQueue.registerProducer();
        }
        this.isAudit=isAudit;
        this.directoryQueue.registerProducer();
        this.directoryQueue.enqueue(this.root);
    }

    public void run() {
        this.auditingQueue.enqueue("General, program has started the search");
        this.id=Thread.currentThread().getId();
        Queue<File> filesQueue= new LinkedList<File>();
        filesQueue.add(this.root);
        while (!filesQueue.isEmpty()) {
            File currentDirectoryToScan=filesQueue.remove();
            File[] files = currentDirectoryToScan.listFiles();
            for (int i=0;i<files.length;i++) {
                if (files[i].isDirectory()) {
                    filesQueue.add(files[i]);
                    this.directoryQueue.enqueue(files[i]);
                }
            }
            if(this.isAudit) {
                auditingQueue.enqueue("Scouter on thread id " + this.id + ": directory named " + currentDirectoryToScan.getName() + " was scouted");
//                System.out.println("Scouter on thread id " + this.id + ": directory named " + currentDirectoryToScan.getName() + " was scouted");
            }
        }
        this.directoryQueue.unregisterProducer();
        if(this.isAudit){
            this.auditingQueue.unregisterProducer();
        }

    }
}
