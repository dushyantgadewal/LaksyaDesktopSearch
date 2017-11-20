/*
 * ThreadModifiedFile.java
 *
 * Created on October 25, 2007, 11:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.Index;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Dushyant
 */
public class ThreadModifiedFile extends Thread {
    
        //Global Variable
        private File drive;
        
        
    
    /** Creates a new instance of ThreadModifiedFile */
    public ThreadModifiedFile() {
    }
    
    public ThreadModifiedFile(File drive) {
        this.drive = drive;
        //Thread.setName(drive.toString());
   }
        
    
      public void run() {
        synchronized(this){
            // Process only files under dir
            
            
            visitAllFiles(this.drive);
            
//            try {
//                sleep(aWhile);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//            System.out.println(Thread.currentThread().getName() + " the Date Diff is " + (Calendar.getInstance().getTimeInMillis() - IndexNewModifiedFile.getModifiedDate()));
//            System.out.println(Thread.currentThread().getName() + " the Date rev is " + (IndexNewModifiedFile.getModifiedDate() - Calendar.getInstance().getTimeInMillis()));
        }

    }
      
      //list File under Directory and Search for Modified File
      public static void visitAllFiles(File dir) {
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i=0; i<children.length; i++) {
                        visitAllFiles(new File(dir, children[i]));
                    }
                } else {
                    if(dir.lastModified()-IndexNewModifiedFile.getModifiedDate() > 0)
                    {
                        IndexFiles.IndexDocFile(dir,false);
                        Calendar calendarExtra = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat();
                        //setModifiedDate(calendarExtra.getTimeInMillis());
                        calendarExtra.setTimeInMillis(dir.lastModified());
                        System.out.println(" the last Modified Index " + dateFormat.format(calendarExtra.getTime()));
                        calendarExtra.setTimeInMillis(IndexNewModifiedFile.getModifiedDate());
                        System.out.println(" the file last Modified " + dateFormat.format(calendarExtra.getTime()));
                        //System.out.println(dir + " the Date Diff is " + (dir.lastModified() - IndexNewModifiedFile.getModifiedDate() ));
                        //System.out.println(dir + " the Date Diff is " + IndexNewModifiedFile.getModifiedDate() + " - " +  dir.lastModified());
                        //System.out.println("New File Added to Index " + dir);
                    }
                }
            }
      
      
}
