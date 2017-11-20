package LakshyaDeskTopSearchEngine.Util;

import LakshyaDeskTopSearchEngine.SupportFile.FileUpdate;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;

/*
 * EngineRequirements.java
 *
 * Created on October 30, 2007, 10:07 PM
 * @author Dushyant Gadewal
 * This File is to Modify the Properties and other File 
 * 
 */
import org.apache.log4j.Logger;

public class EngineRequirements {
    
  /** The time to pause between URL fetches (in seconds).  */
  static Logger myLogger =
               Logger.getLogger(EngineRequirements.class.getName());  
  public int pause_time = 5;

  public static String mainDir = "SupportFile/html";    // directory containing main index
  //                                       // and custom html files
  public static String mainIndex = mainDir +"/" + "main.txt" ;// main index
  public static String header = mainDir +"/" + "header.html" ;// header file
  public static String footer = mainDir +"/" + "footer.html" ;// footer file
  public static String notfound = mainDir +"/" + "notfound.html";  // query not found file
  public static String working_dir = "searchtmp" ; // temporary working directory
  public static File indexFolder ; //new File("H:\\TourismSite") ; //Folder to Index
  public static String supportFile = "SupportFile/html"   ; // directory containing PropertiesFile
  public static File indexFileStore  ;//new File("E:\\LakshyaIndex\\IndexFiles") ;//Path where Index file store
  
  public static HashMap systemProperties = new HashMap();
  
  String user_agent = "LakshyaSearch";          // name used when retrieving URLs
  String email_address = "dushyant_fst@rediffmail.com"; // administrator's email address
  public static int port = 8080;         // default web server port

  

}
