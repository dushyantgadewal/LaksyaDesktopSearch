/*
 * FileUpdate.java
 *
 * Created on October 30, 2007, 10:07 PM
 *
 * This File is to Modify the Properties and other File 
 * 
 */

package LakshyaDeskTopSearchEngine.SupportFile;

import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author Dushyant
 */
public class FileUpdate {
    
   //Retrive the Stop Extension file 
   public static String[] STOP_EXTENSION = null;
   public static String FIRST_INDEX = null;
    
    /** Creates a new instance of FileUpdate */
    public FileUpdate() {
    }
    
    public static void modifyPropertiesFile(String modifyKey, String modifyValue) {
        String fileModification = null;
                //Fire mail
                    //Check Properties file  for Hold Time (Operation Team)
        try {
        BufferedReader in = new BufferedReader(new FileReader(System.getProperty("java.io.tmpdir")+"/HelpFile.properties"));
        // Create temp file.
            File temp = File.createTempFile("PropTemp", ".Properties", new File(System.getProperty("java.io.tmpdir")));
            // Delete temp file when program exits.
            temp.deleteOnExit();
            // Write to temp file
            fileModification = temp.getName();
            System.out.println("File name is " + temp.getName());
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            String str;
            while ((str = in.readLine()) != null) {
                if(str.indexOf(modifyKey)!=-1)
                {
                    out.write(modifyKey + " = " + modifyValue);
                }else{
                    out.write(str);
                }
                out.write("\n");
            }
            out.close();
            in.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
       
       
        //Delete File
        boolean success = (new File(System.getProperty("java.io.tmpdir")+"/HelpFile.properties")).delete();
        if (!success) {
            // Deletion failed
        }
        // File (or directory) with old name
        File file = new File(System.getProperty("java.io.tmpdir")+"/" + fileModification);
 
        // File (or directory) with new name
        File file2 = new File(System.getProperty("java.io.tmpdir")+"/HelpFile.properties");
 
        // Rename file (or directory)
        boolean success1 = file.renameTo(file2);
        if (!success1) {
            System.out.println("file Renaming is not Possibel");
        }else{
            System.out.println("file Renaming completed");
        }
    }
    
    public static void addPropertiesFile(String addKey, String addValue) {
        String fileModification = null;
                //Fire mail
                    //Check Properties file  for Hold Time (Operation Team)
        try {
        BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("java.io.tmpdir")+"/HelpFile.properties",true));
             //       BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                out.write(addKey + " = " + addValue);
                out.write("\n");
            out.close();
            //in.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
       
       
     
    }
    
    
    public static void getStopExtensionFile(){
      //Adding Properties File
      //HashMap properties = getProperties();
      StringTokenizer strToken = new StringTokenizer(EngineRequirements.systemProperties.get("StopExtension").toString(),",");
      STOP_EXTENSION = new String[strToken.countTokens()+3];
        try{
                int count = 0;
                while(strToken.hasMoreTokens())
                {
                    STOP_EXTENSION[count] = strToken.nextToken();
                    count++;
                 }
            }
         catch(Exception e)
            {
                //e.printStackTrace();
            }
  }
    
     public static String getFirstTimeIndexInfo(){
      //Adding Properties File
      HashMap properties = getProperties();
      
      return (String) properties.get("FirstIndex");
  }
     
    //Return Properties file
     public static HashMap getProperties(){
                HashMap returnProperties = new HashMap();
                try {
                        String strDocument = "";
                        BufferedReader in = new BufferedReader(new FileReader(System.getProperty("java.io.tmpdir")+"/HelpFile.properties"));
                        while ((strDocument = in.readLine()) != null) {
                            System.out.println("str Date " + strDocument);
                            if(strDocument.indexOf("=")!=-1){
                                returnProperties.put(strDocument.substring(0,strDocument.indexOf("=")).trim(),strDocument.substring(strDocument.indexOf("=")+1).trim());                                 
                            }

                    }
                    in.close();
                } catch (IOException e) {
                }   
          return returnProperties;      
  }
    
}
