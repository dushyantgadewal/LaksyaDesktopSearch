/*
 * ResultPageParse.java
 *
 * Created on October 24, 2007, 11:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.QueryServer;

import LakshyaDeskTopSearchEngine.Index.IndexFiles;
import LakshyaDeskTopSearchEngine.SupportFile.FileUpdate;
import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

/**
 *
 * @author Dushyant
 */
public class ResultPageParse implements Serializable {
    
    static Logger myLogger =
               Logger.getLogger(ResultPageParse.class.getName());
    /** Creates a new instance of ResultPageParse */
    public ResultPageParse() {
    }
    
    public static boolean getSearchResult(DataInputStream in, DataOutputStream out, String uri, String line
                        , EngineRequirements strPreference) throws IOException{
            
            myLogger.debug("this is uri " + uri);            
            Hashtable h = parsePairs(uri.substring(7));
            HashMap queryWord = new HashMap();

            queryWord.put("words",h.get("words"));
          
          if(h.get("pages")!=null)
              queryWord.put("pages",h.get("pages"));
          
//	  String words = (String) h.get("words");
//          myLogger.debug("The requested word is : " + words);
	  if (h.get("words") != null) {
	    
	    // process GET request
	    
	    myLogger.debug("the Udate line is 2" + line );
//	    if (line.indexOf(' ') != line.lastIndexOf(' ')) {
//	      while (line != null && !line.trim().equals("")) {
//		line = in.readLine();
//	      }
	      out.writeBytes("HTTP/1.0 200 OK\n");
	      out.writeBytes("MIME-Version: 1.0\n");
	      out.writeBytes("Server: LakshyaDesktopServer\n");
	      out.writeBytes("Content-Type: text/html\n");
	      out.writeBytes("\n");
	    //}
	    DBQuery dbq = new DBQuery(queryWord, strPreference);
            try {
                dbq.dumpResults(out);
                myLogger.debug("this is uri System check ");
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } catch (ParseException ex) {
                ex.printStackTrace();
                return false;
            }
	  }
          
          return true;
    }
    
    static boolean getRedirect(DataInputStream in, DataOutputStream out, String uri, String line, EngineRequirements strPreference) throws IOException {
            Hashtable h = parsePairs(uri.substring(10));
            //h.get("f");
            Runtime run = Runtime.getRuntime();
            try {
                run.exec("rundll32 url.dll,FileProtocolHandler file:///" + h.get("f"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            String uriUpdate = "/query?words=";  //&pages=1"
            if(h.get("words") != null){
                uriUpdate = uriUpdate + h.get("words");
            }
            if(h.get("pages") != null){
                uriUpdate = uriUpdate +"&pages=" +  h.get("pages");
            }
            myLogger.debug("the Udate url is " + uriUpdate );
            myLogger.debug("the Udate line is " + line );
            getSearchResult(in, out, uriUpdate, line
                        , strPreference);
            
        return true;
    }
    
     static boolean getConfigure(DataInputStream in, DataOutputStream out, String uri, String line, EngineRequirements strPreference) throws IOException {
         
            // process GET request
	    
	    myLogger.debug("the Udate line is 2" + line );

              Hashtable hashMap = parsePairs(uri.substring(11));
             HashMap queryWord = new HashMap();
             try {
                    BufferedWriter outFile = new BufferedWriter(new FileWriter(System.getProperty("java.io.tmpdir") + "/HelpFile.properties",true));
                    
                        String str;
                         // Iterate over the keys in the map
                            Iterator it = hashMap.keySet().iterator();
                            while (it.hasNext()) {
                                // Get key
                                String key = (String)it.next();
                                outFile.write(key+"=" + hashMap.get(key));
                                outFile.write("\n");
                            }
                        outFile.close();
                        //in.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
             
             
        //Set System Properties file 
            EngineRequirements.systemProperties = FileUpdate.getProperties();
            myLogger.debug(" the index Folder is " + EngineRequirements.systemProperties.get("lakshyaIndexingDirectory"));
            EngineRequirements.indexFolder = new File(EngineRequirements.systemProperties.get("lakshyaIndexingDirectory").toString()); 
            EngineRequirements.indexFileStore = new File(EngineRequirements.systemProperties.get("lakshyaIndexStore").toString());
            
        //Get Stop File Extension
        FileUpdate.getStopExtensionFile();
        //Check For First Time Index
        if(FileUpdate.getFirstTimeIndexInfo().equalsIgnoreCase("false")){
            String[] webString = { "Lakshya","Desktop" };
            // Start Index thread 
            Runnable index = new IndexFiles();
            Thread firstIndex = new Thread(index);
            firstIndex.start();
            
            FileUpdate.modifyPropertiesFile("FirstIndex","true");
        }
        
        //Return Home page
        ResultPageParse.getHtmlResult(out , "SupportFile/html/home.html");
           
          
          return true;
    }
    
    
    
    
    public static boolean getHtmlResult(DataOutputStream out, String fileToDisplay) throws IOException{
                
                out.writeBytes("HTTP/1.0 200 OK\n"+
                              "MIME-Version: 1.0\n"+
                              "Server: LakshyaDesktopServer\n"+
                              "Content-Type: text/html\n\n");
                
                String outResult = "", strDocument;
              try {
                    //Class Object to access
                    ResultPageParse resultHtml = new ResultPageParse();
                    BufferedReader in = resultHtml.htmlFileReader(fileToDisplay);
                    while ((strDocument = in.readLine()) != null) {
                        outResult = outResult + strDocument; 
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }   
	      
                out.writeBytes(outResult);
              
          
          return true;
    }

    public BufferedReader htmlFileReader(final String fileToDisplay) {
        ClassLoader cl = this.getClass().getClassLoader();
        myLogger.debug("the file to display " + fileToDisplay);
        
        //java.io.InputStream inputStream = cl.getResource(fileToDisplay);
        myLogger.debug("the file to display " + cl.getResource("SupportFile/html/HelpFile.properties"));
        myLogger.debug("the properties to display " + cl.getResourceAsStream(fileToDisplay));
//        BufferedReader in = null;
//        try {
//            in = new BufferedReader(new FileReader(cl.getResource(fileToDisplay).toString()));
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
        BufferedReader in = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(fileToDisplay)));
        return in;
    }
    
    
   public  static String getHomePage() {
            
            Runtime run = Runtime.getRuntime();
            try {
                run.exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080/home.html"); //file:///" + EngineRequirements.mainDir.toString() + "\\MainIndex.html");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
           return "started";  
    }
   
   /** 
   *  Return the about.html Page 
   *  
   */
   public  static String getaboutPage() {
            
            Runtime run = Runtime.getRuntime();
            try {
                run.exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080/about.html"); //file:///" + EngineRequirements.mainDir.toString() + "\\MainIndex.html");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
           return "started";  
    }
   
   
   /** 
   *  Return the LakshyaConfig.html Page 
   *  
   */
   public static  void getConfigPage() {
            
            Runtime run = Runtime.getRuntime();
            try {
                run.exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080/LakshyaConfig.html"); //file:///" + EngineRequirements.mainDir.toString() + "\\MainIndex.html");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
          // return true;  
    }
    

    
    
    /** Takes an url-encoded list of name value pairs as submitted via a
   *  form and decodes all the pairs into a Hashtable keyed by the
   *  names of the pairs.
   */
  private static Hashtable parsePairs(String line) {
    Hashtable h = new Hashtable(5);
    StringTokenizer st = new StringTokenizer(line, "&");
    int i;
    String s;
    while (st.hasMoreTokens()) {
      s = st.nextToken();
      i = s.indexOf('=');
      if (i > -1) {
	h.put(urlDecode(s.substring(0, i)), urlDecode(s.substring(i+1)));
      }
    }
    return h;
  }
  
  
  /** Decodes a string that has been url-encoded.  */
  private static String urlDecode(String encoded) {
    int targ = encoded.length();
    StringBuffer decoded = new StringBuffer();
    char c;
    try {
      for (int i = 0; i < targ; i++) {
	switch(c = encoded.charAt(i)) {
	case '+':
	  decoded.append(' ');
	  break;
	case '%':
	  decoded.append((char) (hexValue(encoded.charAt(i+1))*16+
				 hexValue(encoded.charAt(i+2))));
	  i += 2;
	  break;
	default:
	  decoded.append(c);
	  break;
	}
      }
    } catch (IndexOutOfBoundsException e) {}
    return new String(decoded);
  }

  /** Returns the hex value of "c" or -1 if there is no corresponding
   *  hex value.
   */
  private static int hexValue(char c) {
    if ('0' <= c && c <= '9') return c - '0';
    if ('A' <= c && c <= 'F') return c - 'A' + 10;
    if ('a' <= c && c <= 'f') return c - 'a' + 10;
    return -1;
  }

    static boolean getImageResult(DataOutputStream out, String imageFileName) {
         try {
            // Get image format in a file
            
            //String formatName = getFormatName(file);
            // e.g. gif
            ResultPageParse result = new ResultPageParse();
            result.pipe(imageFileName,out);
//            // Get image format from an input stream
//            InputStream is = new FileInputStream(file);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = is.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//                //formatName = getFormatName(is);
//            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    /** Pipes "in" to "out" until "in" is exhausted then closes "in".   */
    public  void pipe(String file, OutputStream out) throws IOException {
        
            
            String outResult = "", strDocument;
              try {
                    //Class Object to access
                    ClassLoader cl = this.getClass().getClassLoader();
                    myLogger.debug("the file to display " + file);
                    java.io.InputStream inputStream = cl.getResourceAsStream(file);
                    byte[] b = new byte[512];
                    int x = inputStream.read(b, 0, b.length);
                    while (x > 0) {
                      out.write(b, 0, x);
                      x = inputStream.read(b, 0, b.length);
                    }
                    inputStream.close();
//                    while ((strDocument = in.readLine()) != null) {
//                        outResult = outResult + strDocument; 
//                    }
//                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    //return false;
                }   
	      
                //out.writeBytes(outResult);
          
      }
    
    

    
    // Returns the format of the image in the file 'f'.
    // Returns null if the format is not known.
    public static String getFormatInFile(File f) {
        return getFormatName(f);
    }
    
    // Returns the format of the image in the input stream 'is'.
    // Returns null if the format is not known.
    public static String getFormatFromStream(InputStream is) {
        return getFormatName(is);
    }
    
    // Returns the format name of the image in the object 'o'.
    // 'o' can be either a File or InputStream object.
    // Returns null if the format is not known.
    private static String getFormatName(Object o) {
        try {
            // Create an image input stream on the image
            ImageInputStream iis = ImageIO.createImageInputStream(o);
    
            // Find all image readers that recognize the image format
            Iterator iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                // No readers found
                return null;
            }
    
            // Use the first reader
            ImageReader reader = (ImageReader)iter.next();
    
            // Close stream
            iis.close();
    
            // Return the format name
            return reader.getFormatName();
        } catch (IOException e) {
        }
        // The image could not be read
        return null;
    }

//    static boolean getConfigRedirect(DataInputStream in, DataOutputStream out, String uri, String line, EngineRequirements strPreference) {
//            Hashtable h = parsePairs(uri.substring(11));
//            myLogger.debug("this is uri " + uri);            
//            //HashMap queryWord = new HashMap();
//
//            FileUpdate.addPropertiesFile("lakshyaIndexingDirectory",h.get("lakshyaIndexingDirectory").toString());
//            FileUpdate.addPropertiesFile("lakshyaIndexStore",h.get("lakshyaIndexStore").toString());
//            //queryWord.put("lakshyaIndexStore",h.get("lakshyaIndexStore"));
//          
//
//        
//        
//        return true;
//    }
    
}
