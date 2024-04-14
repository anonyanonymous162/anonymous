package anonym.anonyms.anonym.algo;

import java.util.concurrent.*;

public class ParallelTasks {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // Create and submit 10 tasks to be executed concurrently
        for (int i = 0; i < 10; i++) {
            final int taskNum = i;
            executor.submit(new Runnable() {
                public void run() {
                    System.out.println("Task " + taskNum + " started");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }
                    System.out.println("Task " + taskNum + " finished");
                }
            });
        }
        
        // Shut down the executor
        executor.shutdown();
    }
}

