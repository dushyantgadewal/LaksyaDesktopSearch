/*
 * DBQuery.java
 *
 * Created on October 24, 2007, 11:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.QueryServer;

import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

/**
 *
 * @author Dushyant
 */
public class DBQuery implements Serializable {
    
    static Logger myLogger =
               Logger.getLogger(DBQuery.class.getName());
    
  HashMap query;
  EngineRequirements strPreference;
  int HITS_PER_PAGE = 20;
  String DISPLAY_RESULT = "<table style='width: 605px;' border='0' cellpadding='0' cellspacing='0'><tbody><tr><td rowspan='2' valign='top'>&nbsp;&nbsp;&nbsp;</td><td valign='top' width='98%'> "+ //<img style='vertical-align: middle;' src='' border='0' height='16' width='16'>
                            " Heading_File <br><font ><span id='show9398'> "+ //<font >Brief_Detail</font>
                            " Open_folder </font> - Modified_Time </font></td></tr><tr><td></td></tr><tr><td></td><td></td></tr><tr><td height='14'><img height='1'></td></tr></tbody></table>";

  public DBQuery(HashMap query, EngineRequirements strPreference) {
    this.query = query;
    this.strPreference = strPreference;
  }

  public void dumpResults(OutputStream stream) throws IOException, ParseException {
    
    // create a DataOutputStream

    DataOutputStream out;
    if (stream instanceof DataOutputStream) {
      out = (DataOutputStream) stream;
    } else {
      out = new DataOutputStream(stream);
    }
    
    // determine results of query
    //By Searching the Index
    //String index = "E:\\LakshyaIndex\\IndexFiles";
    String queries = null;
    int repeat = 0;
    boolean raw = false;
    String normsField = null;
    IndexReader reader = IndexReader.open(EngineRequirements.indexFileStore);
    Searcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer();
    
    //date formatter
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance(); 
        //Query Parser on Particular Field
        QueryParser content = new QueryParser("contents", analyzer);
        QueryParser filename = new QueryParser("filename", analyzer);
        
      //Parse request on Query
      Query contentParser = content.parse(query.get("words").toString());
      Query filenameParser = filename.parse(query.get("words").toString());
      myLogger.debug("Searching for: " + contentParser.toString("contents"));

        Date start = new Date();
        //Find the Hit Result
        ArrayList hitsArrayList = new ArrayList();
        Hits contentHits = searcher.search(contentParser);
        Hits filenameHits = searcher.search(filenameParser);
        hitsArrayList.add(0,contentHits);
        hitsArrayList.add(1,filenameHits);
        Date end = new Date();
        myLogger.debug("Time: "+(end.getTime()-start.getTime())+"ms");
      
      
// Searching Index is Over Here 
        int calculateTotalResult = contentHits.length() + filenameHits.length() ;
    // handle the case where no results are found

    //File f;
    ResultPageParse resultPage = new ResultPageParse(); // handle outStream data
    if (calculateTotalResult < 1) {
        myLogger.debug("here v r ");
      //f = EngineRequirements.notfound;//strPreference.getNotFoundFile();
      myLogger.debug("here v r ");
      //if (f.exists()) {
	//pipe(new FileInputStream(f), stream);
          resultPage.pipe(EngineRequirements.notfound, stream);
      //}
      return;
    }
    
    // output header file
        //f = EngineRequirements.header;//strPreference.getHeaderFile();
        //if (f.exists()) {
          //pipe(new FileInputStream(f), stream);
            resultPage.pipe(EngineRequirements.header, stream);
        //}
        
        //Dislplay total hits
        //the Current Page count
        int pageCurrent = 0; 
              if(query.get("pages")!=null){
                 pageCurrent = Integer.parseInt(query.get("pages").toString());
              } 
        //Include Css File
        //out.writeBytes("<link rel=StyleSheet href='Image/FileTypeIcon.css' type=\"text/css\">");
        out.writeBytes("<table border='0' cellpadding='0' cellspacing='0' width='100%'><tbody><tr bgcolor='#3399cc'><td align='center' height='1'><img height='1' width='1'></td></tr></tbody></table> "+
                        "<table bgcolor='#e8f4f7' border='0' cellpadding='0' cellspacing='0' width='100%'> "+
                        "<tbody><tr bgcolor='#e8f4f7'>"+
                        "<td width='80%'>  There were "+ calculateTotalResult +" results for <b> " + query.get("words")+ "</b></td> "+
                        "<td width='20%'>Time Taken to Find: "+ ((end.getTime()-start.getTime())/1000.0)+" Sec </td></tr></tbody></table> ");
 
        //out.writeBytes("There were "+ calculateTotalResult +" results for <b> " + query.get("words")+ "</b>      Time Taken to Find: "+ Math.abs((end.getTime()-start.getTime())/1000.0)+" Sec ");
        //out.writeBytes("</td>");
        getHitResult(out, calendar, dateFormat, hitsArrayList, calculateTotalResult, pageCurrent*20);
      
      
      //Pages wise Display
      
      double totalPages = Math.ceil(calculateTotalResult/20.0); //Total No. of Pages
      myLogger.debug("total No. of Pages " + totalPages);
      
      //Calculate Start Page
      int pageStart = (query.get("pages")!=null)?Integer.parseInt(query.get("pages").toString()):0;
      if((pageStart-5) < 0){
          pageStart = 0;
      }else{
          pageStart = pageStart-5;
          
      }
      
      out.writeBytes("<b> Result Page: </b>");
      
      //calculate EndPage
      int pageEnd = (pageStart == 0)?Math.min(10,(int)totalPages):Math.min(pageStart+10,(int)totalPages);
      if((pageEnd+5) < totalPages ){
          pageEnd = pageEnd + 5;
      }
      
      
      
      myLogger.debug(" start " + pageStart + " end " + pageEnd);
      //Previous Page
      if( pageStart > 0){
                  out.writeBytes("<a href='http://localhost:8080/query?words=" + query.get("words") + "&pages=" + (pageStart-1) + "'>Prev</a> &nbsp;");
                }
      //Whole page
      while(pageStart < pageEnd) {
                if(pageStart == pageCurrent){
                    out.writeBytes((pageStart+1) + "&nbsp;");
                }else{
                    out.writeBytes("<a href='http://localhost:8080/query?words=" + query.get("words") + "&pages=" + (pageStart) + "'>" + (pageStart+1) + "</a> &nbsp;");
                }
              pageStart++;
         }
      //Next Page
      if(pageEnd < totalPages){
                  out.writeBytes("<a href='http://localhost:8080/query?words=" + query.get("words") + "&pages=" + (pageEnd) + "'>Next</a> &nbsp;");
                }
      
            
      out.writeBytes("<br>");
      //String helpWord = "";
//    while (en.hasMoreElements()) {
//        helpWord = (String) en.nextElement();
//      out.writeBytes("   "+( helpWord) + "<br>\n");
//      myLogger.debug("   "+(helpWord) + "<br>\n");
//    }
//    out.writeBytes("</pre>\n");

    // output footer file
    
    //f =  EngineRequirements.footer;//strPreference.getFooterFile();
    //if (f.exists()) {
      //pipe(new FileInputStream(f), stream);
        resultPage.pipe(EngineRequirements.footer, stream);
    //}

  }

    private void getHitResult( final DataOutputStream out, final Calendar calendar, final java.text.SimpleDateFormat dateFormat,final ArrayList hitsArrayList, final int hitsTotalResult, final int startPage) throws NumberFormatException, FileNotFoundException, IOException {
        
        
        
    // output query results
        int endPage = Math.min(hitsTotalResult, startPage + HITS_PER_PAGE);;
        int hitCounter = 0;
        //out.writeBytes("<table");
        String displayResult = "";
        Hits hits = null;
        for (int index = 0; index < hitsArrayList.size(); index++) {
            hits = (Hits) hitsArrayList.get(index);
            myLogger.debug("total No of hits Array List length " + hits.length());
            if(hits.length()!=0){    
                    for (int i = 0; i < hits.length(); i++) {
                    displayResult = "<table border='0' cellpadding='0' cellspacing='0'><tbody><tr><td rowspan='2' valign='top'>&nbsp;&nbsp;&nbsp;</td><td valign='top' width='98%'> ";
                    Document doc = hits.doc(i);
                    String path = doc.get("path").toString();
                    path = path.replace('\\','/');
                    //myLogger.debug("the path is " + path.replace('\\','/'));
                    if (path != null && (hitCounter >= startPage && hitCounter < endPage)) {
                        
                        displayResult = displayResult + (hitCounter+1) + ".    <a class='" + path.substring(path.lastIndexOf("/")) + "' href='redirect?f=" + path + "&words="+query.get("words").toString() + "'><nobr>" + path.substring(path.lastIndexOf("/")+1) + "</nobr></a> <br><font ><span id='show9398'>" ; //.substring(path.lastIndexOf("/")+1)
                        
                        displayResult = displayResult + "<a href='redirect?f=" +  path.substring(0,path.lastIndexOf("/")) + "&words="+query.get("words").toString() + "'>Open Folder</a> </font> -";
                          //out.writeBytes();
                          String modified = doc.get("modified");
                          if (modified != null) {
                              calendar.setTimeInMillis(Long.parseLong(doc.get("modified")));  //Set the Modified to proper format
                              displayResult = displayResult  + " Modified on : " + dateFormat.format(calendar.getTime()) + " </font></td></tr> " +
                                 " <tr><td> Path : " +  path + " </td></tr><tr><td></td><td></td></tr><tr><td height='14'><img height='1'></td></tr></tbody></table> ";
                            //out.writeBytes("   Modified on : " + doc.get("modified"));
                          }
                          
                          //out.writeBytes("<br>&nbsp;<a href='redirect?f=" +  path.substring(0,path.lastIndexOf("\\")) + "&words="+query.get("words").toString() + "'>Open Folder</a>" );
                          
                          //add extension file
                          //displayResult = displayResult.replaceAll("Open_folder",path.substring(path.lastIndexOf(".")));
                                                
                          out.writeBytes(displayResult + "<br>");
                          //out.writeBytes("<br>");

                          displayResult = "";
                        }
                    hitCounter++;
                  }
             }
          }
    }

  /** Returns a copy of "word" where all non-alphanumeric characters are
   *  converted to spaces (ascii 32).
   */
  String makeAlphaNumeric(String word) {
    char[] w = word.toCharArray();
    for (int i = w.length - 1; i >= 0; i--) {
      if (!Character.isLetterOrDigit(w[i])) w[i] = ' ';
    }
    return new String(w);
  }



      

    
}
