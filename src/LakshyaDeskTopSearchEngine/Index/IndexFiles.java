package LakshyaDeskTopSearchEngine.Index;

/*
 * IndexFiles.java
 *
 * Created on October 30, 2007, 11:48 PM
 * Author: Dushyant Gadewal
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import LakshyaDeskTopSearchEngine.SupportFile.FileUpdate;
import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.util.Arrays;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** Index all text files under a directory. */
public class IndexFiles implements Runnable{
  
  public static boolean indexSession = false;  
    
  public IndexFiles() {}

  
 /** Index all text files under a directory. */
  public void run() {
    
          

    if (EngineRequirements.indexFileStore.exists()) {
      System.out.println("Cannot save index to '" + EngineRequirements.indexFileStore+ "' directory, please delete it first");
      System.exit(1);
    }
    
    if(EngineRequirements.systemProperties.get("lakshyaIndexingDirectory") != null &&
            EngineRequirements.systemProperties.get("lakshyaIndexingDirectory").toString().trim().equals(""))
    {
        File fileDrive[] = File.listRoots();
        for (int i = 0; i < fileDrive.length; i++) {
            System.out.println("Drive is " + fileDrive[i] + " and can Write: " + fileDrive[i].canWrite());
            if(fileDrive[i].canRead()){
                Date start = new Date();
                //Start Indexing the File 
                indexSession = true;
                IndexDocFile(fileDrive[i],true);
                //Stop Indexing file
                indexSession = false;
                Date end = new Date();
                System.out.println(end.getTime() - start.getTime() + " total milliseconds");
            }
            
        }
        //set current index time
            IndexNewModifiedFile.currentIndexDateSet();
        
    }else{
        final File docDir = EngineRequirements.indexFolder;
        if (!docDir.exists() || !docDir.canRead()) {
          System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
          
          //delete properties file 
          System.out.println(System.getProperty("java.io.tmpdir")+"/HelpFile.properties");
          File propertiesFile = new File(System.getProperty("java.io.tmpdir")+"/HelpFile.properties");
          propertiesFile.deleteOnExit();
          System.exit(1);
        }else{
            Date start = new Date();
            //Start Indexing the File 
            indexSession = true;
            IndexDocFile(docDir,true);
            //Stop Indexing file
            indexSession = false;
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");
            
            //set current index time
            IndexNewModifiedFile.currentIndexDateSet();
            
        }
    }
   
  }
  
  
    
  
    //Create Index files to specific folder, boolean true: create new index and false: modify exsisting
    public static void IndexDocFile(final File docDir, boolean createIndex) {
        try {
                  IndexWriter writer = new IndexWriter(EngineRequirements.indexFileStore, new StandardAnalyzer(), createIndex);
                  System.out.println("Indexing to directory '" + EngineRequirements.indexFileStore+ "'...");
                  indexDocs(writer, docDir);
                  System.out.println("Optimizing...");
                  writer.optimize();
                  writer.close();
            } catch (IOException e) {
              System.out.println(" caught a " + e.getClass() +
               "\n with message: " + e.getMessage());
            }
    }

  static void indexDocs(IndexWriter writer, File file)
    throws IOException {
            // do not try to index files that cannot be read
            if (file.canRead()) {
              if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                  for (int i = 0; i < files.length; i++) {
                    indexDocs(writer, new File(file, files[i]));
                  }
                }
              } else {
                
                String fileName = file.getName();
                try {
                    boolean stopExtension = Arrays.asList(FileUpdate.STOP_EXTENSION).contains(fileName.substring(fileName.indexOf(".")+1).toLowerCase());
                    if(!stopExtension){
                        System.out.println("adding Extension File " + file);
                        writer.addDocument(FileDocument.Document(file));
                    }else{
                        System.out.println("adding Stop Extension File " + file);
                        writer.addDocument(FileDocument.DocumentStopExt(file));
                    }
                }
                // at least on windows, some temporary files raise this exception with an "access denied" message
                // checking if the file can be read doesn't help
                catch (FileNotFoundException fnfe) {
                  ;
                }
              }
            }
  }
  

  
}
