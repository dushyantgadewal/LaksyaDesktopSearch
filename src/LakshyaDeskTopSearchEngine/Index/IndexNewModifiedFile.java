/*
 * IndexNewModifiedFile.java
 *
 * Created on October 25, 2007, 11:10 PM
 *Author: Dushyant Gadewal
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.Index;

import LakshyaDeskTopSearchEngine.SupportFile.FileUpdate;
import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Dushyant
 */
class IndexNewModifiedFile implements Runnable {
    
    /**
     * Creates a new instance of IndexNewModifiedFile
     */
    public IndexNewModifiedFile() {
    }
    
        
    //getter setter method for modified Date
      private static long modifiedDate;
      public static void setModifiedDate(long modified){
          FileUpdate.addPropertiesFile("lastUpdate",modified+"");
          modifiedDate = modified;
          EngineRequirements.systemProperties = FileUpdate.getProperties();
      }
      public static long getModifiedDate(){
          if(modifiedDate != 0){
                return modifiedDate;
          }
           return Long.parseLong(EngineRequirements.systemProperties.get("lastUpdate").toString());
      }
      
     public void run() {
        
        
     if(EngineRequirements.systemProperties.get("lakshyaIndexingDirectory") != null &&
            EngineRequirements.systemProperties.get("lakshyaIndexingDirectory").toString().trim().equals(""))
    {// start of if part
        File fl[] = File.listRoots();
        Thread driveThread[] = new ThreadModifiedFile[fl.length];
        
        for (int i = 0; i < fl.length; i++) {
            System.out.println("Drive is " + fl[i] + " and can Write: " + fl[i].canWrite());
            if(fl[i].canWrite()){
                driveThread[i] = new ThreadModifiedFile(fl[i]);
                driveThread[i].start();
                System.out.println("Wait for Thread to Dead");
                    try {
                        driveThread[i].join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                
            }
            
        }
        
        
        
//        for (int j = 0; j<fl.length; j++) {
//            try {
//                driveThread[j].join();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
//            
    }//end of if part
    else{ //start of else part
             
             File fl = EngineRequirements.indexFolder;
             System.out.println("The Folder is Re- Index");
             Thread driveThread = null;
             if(fl.canWrite()){
                    driveThread = new ThreadModifiedFile(fl);
                    driveThread.start();
                }
             try {
                    driveThread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
        
    }//end of Else part
         
         //set current index time
         currentIndexDateSet();
         
        
        
        
        
    }
     
     public static void currentIndexDateSet(){
         //Set current index time
            Calendar calendarExtra = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        setModifiedDate(calendarExtra.getTimeInMillis());
        calendarExtra.setTimeInMillis(getModifiedDate());
        System.out.println(" the last Modified Index " + dateFormat.format(calendarExtra.getTime()));
     }

//    public void run() {
//        File fl[] = File.listRoots();
//        Thread driveThread[] = new ThreadModifiedFile[fl.length];
//        
//        for (int i = 0; i < fl.length; i++) {
//            System.out.println("Drive is " + fl[i] + " and can Write: " + fl[i].canWrite());
//            if(fl[i].canWrite()){
//                driveThread[i] = new ThreadModifiedFile(fl[i]);
//                driveThread[i].start();
//            }
//            
//        }
//        
//        
//        
//        for (int j = 0; j<fl.length; j++) {
//            try {
//                driveThread[j].join();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
//        
//        Calendar calendarExtra = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat();
//        setModifiedDate(calendarExtra.getTimeInMillis());
//        calendarExtra.setTimeInMillis(getModifiedDate());
//        System.out.println(" the last Modified Index " + dateFormat.format(calendarExtra.getTime()));
//        
//        
//        
//    }
    
}
