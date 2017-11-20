/*
 * QueryConnection.java
 *
 * Created on October 24, 2007, 11:03 PM
 *Author: Dushyant Gadewal
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package LakshyaDeskTopSearchEngine.QueryServer;

import java.net.InetAddress;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.util.StringTokenizer;
//import bdd.search.Monitor;
import org.apache.log4j.Logger;


public class QueryConnection extends Thread {

  Socket sock;   // the socket to use for communication
  EngineRequirements strPreference;  // preferences
  static Logger myLogger =
               Logger.getLogger(QueryConnection.class.getName());
  
  public QueryConnection(Socket sock, EngineRequirements strPreference) {
    this.sock = sock;
    this.strPreference = strPreference;
    start();
  }

  /** Read a query and respond appropriately.  */
  public void run() {

    // open streams

    DataInputStream in = null;
    DataOutputStream out = null;
    try {
        //figure out what ipaddress the client commes from, just for show!
        InetAddress client = this.sock.getInetAddress();
        myLogger.debug(" The Client : " + client.getHostAddress() + " Connected to Lakshya Server ");
        
      in = new DataInputStream(sock.getInputStream());
      out = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
      myLogger.debug("Waiting for Input ");
      String line = in.readLine();
      if (line == null) throw new IOException();
      String lower_case = line.toLowerCase();
      myLogger.debug("The requested Url is : " + lower_case);
      boolean found = false,startSearch = false;
      if (lower_case.startsWith("get")) {
	StringTokenizer st = new StringTokenizer(line);
	st.nextToken();
	String uri = st.nextToken();
	if (uri.startsWith("/query?")) {
            found = ResultPageParse.getSearchResult(in , out ,uri, line , strPreference);
	}
        if (uri.startsWith("/redirect?")) {
            found = ResultPageParse.getRedirect(in, out ,uri, line , strPreference);
	}

        if (uri.startsWith("/configure?")) {
            found = ResultPageParse.getConfigure(in, out, uri, line , strPreference);
          }
        
        if (uri.indexOf("html")!=-1) {
            myLogger.debug(EngineRequirements.mainDir.toString() + uri.substring(uri.indexOf("/"),uri.indexOf("html")+4) + " the lsd flsa");
            found = ResultPageParse.getHtmlResult(out , "SupportFile/html" + uri.substring(uri.indexOf("/"),uri.indexOf("html")+4) );
	} 
        if (uri.indexOf("gif")!=-1) {
            found = ResultPageParse.getImageResult(out , "images" + uri.substring(uri.indexOf("/"),uri.indexOf("gif")+3) );
	} 
        

      }
      
//      if(startSearch){
//          ResultPageParse.getHomePage();
//      }      
      
      if (!found) {
	
	// output a missing page response in case of error
        
	  out.writeBytes("HTTP/1.0 404 Not Found\n");
	  out.writeBytes("MIME-Version: 1.0\n");
	  out.writeBytes("Server: LakshyaDesktopServer\n");
	  out.writeBytes("Content-Type: text/html\n");
	  out.writeBytes("\n");
	
	out.writeBytes("<html><head>\n");
	out.writeBytes("<title>Lakshya Page not found</title>\n");
	out.writeBytes("</head><body>\n");
	out.writeBytes("Lakshya: The page you requested was not found.\n");
	out.writeBytes("</body></html>\n");
      }
      out.flush();
    } catch (IOException e) {e.printStackTrace();}

    // Close all connections

    try {
      sock.close();
      in.close();
      out.close();
    } catch (IOException e2) {
        e2.printStackTrace();
    }
  }

  
  
  

}
