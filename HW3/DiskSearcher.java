import sun.management.VMManagement;

import java.io.File;

public class DiskSearcher {
    static final int RESULTS_QUEUE_CAPACITY =100;
    static final int DIRECTORY_QUEUE_CAPACITY =100;
    static final int AUDITING_QUEUE_CAPACITY =100;
    public static void main(String [] args){
        long start = System.currentTimeMillis();
        boolean milestoneQueueFlag = Boolean.parseBoolean(args[0]);
        String prefix=args[1];
        String rootDirectoryPath=args[2];
        File rootDirectory=new File(rootDirectoryPath);
        String destinationDirectoryPath=args[3];
        File destinationDirectory=new File(destinationDirectoryPath);
        int numOfSearchers= Integer.parseInt(args[4]);
        int numOfCopiers= Integer.parseInt(args[5]);
        SynchronizedQueue<File> directoryQueue= new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue= new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);
        SynchronizedQueue<String> auditingQueue;
        if(milestoneQueueFlag){
            auditingQueue= new SynchronizedQueue<String>(AUDITING_QUEUE_CAPACITY);
        }else {
            auditingQueue = null;
        }

        Scouter scouter = new Scouter(directoryQueue,rootDirectory,auditingQueue,milestoneQueueFlag);
        Thread t_scouter = new Thread(scouter);
        t_scouter.start();

        Thread[] Searchers=new Thread[numOfSearchers];
        for (int i=0;i<numOfSearchers;i++){
            Searcher searcher = new Searcher(prefix,directoryQueue,resultsQueue,auditingQueue,milestoneQueueFlag);
            Thread t = new Thread(searcher);
            t.start();
            Searchers[i]=t;
        }
        Thread[] Copiers=new Thread[numOfCopiers];
        for (int i=0;i<numOfCopiers;i++){
            Copier copier = new Copier(destinationDirectory,resultsQueue,auditingQueue,milestoneQueueFlag);
            Thread t = new Thread(copier);
            t.start();
            Copiers[i]=t;
        }
//        joining all threads
        try{t_scouter.join();}
        catch(Exception e){System.out.println(e);}
        for (Thread t:Searchers) {
            try{t.join();}
            catch(Exception e){System.out.println(e);}
        }
        for (Thread t:Copiers) {
            try{t.join();}
            catch(Exception e){System.out.println(e);}
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        if(milestoneQueueFlag){
            auditingQueue.to_string();
        }
        System.out.print(timeElapsed+"  Milli Seconds.");
    }


}
