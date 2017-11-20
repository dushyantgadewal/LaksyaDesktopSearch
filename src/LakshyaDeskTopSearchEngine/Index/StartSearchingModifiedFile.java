/*
 * StartSearchingModifiedFile.java
 *
 * Created on October 25, 2007, 11:40 PM
 *Author: Dushyant Gadewal
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.Index;

import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Dushyant
 */
public class StartSearchingModifiedFile {
    
    /** Creates a new instance of StartSearchingModifiedFile */
    public StartSearchingModifiedFile() {
    }
    
    public static void main(String arg[]){
        
        // Create the object with the run() method
        Runnable runnable = new IndexNewModifiedFile();

        // Create the thread supplying it with the runnable object
        Thread thread = null;
        File docDir = EngineRequirements.indexFolder;
        
        IndexNewModifiedFile.setModifiedDate(docDir.lastModified());
            // Start the thread
            
            
            
//        for (int i = 0; i < 2; i++) {
            Date start = new Date();
            System.out.println("Main thread is Started " );
            thread = new Thread(runnable);
            thread.start();
            try {
                thread.join();
                System.out.println("Main thread is End " );
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");
//        }
        
    }
    
}
