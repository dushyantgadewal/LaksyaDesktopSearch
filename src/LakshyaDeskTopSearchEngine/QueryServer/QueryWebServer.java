/*
 * QueryWebServer.java
 *
 * Created on October 27, 2007, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package LakshyaDeskTopSearchEngine.QueryServer;

import java.net.ServerSocket;
import java.io.IOException;
import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 *
 * @author Dushyant
 */
public class QueryWebServer extends Thread implements Serializable {

  static Logger myLogger =
               Logger.getLogger(QueryWebServer.class.getName());  
  int port = EngineRequirements.port;  // the TCP port number for this server
  EngineRequirements strPreference;  // preferences
  public static boolean webServerStart = false;

  /** Starts a new QueryWebServer on the default port.  */
  public QueryWebServer() {
    this(EngineRequirements.port, new EngineRequirements());
  }

  /** Starts a new QueryWebServer on the specified port.  */
  public QueryWebServer(int port) {
    this(port, new EngineRequirements());
  }

  /** Starts a new QueryWebServer on the specified port with the given
   *  preferences.  */
  public QueryWebServer(int port, EngineRequirements strPreference) {
    this.strPreference = strPreference;
    this.port = port;
    start();
  }

  /** This is where connections are accepted.  */
  public void run() {
      
        webServerStart = true;
        try {
          ServerSocket sock = new ServerSocket(port);
                    
          while (webServerStart) {
            try {
                myLogger.debug("Server is running");
                new QueryConnection(sock.accept(), strPreference);
                //sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception exp){
                exp.printStackTrace();
            }
          }
          
          sock.close();
        } catch (IOException e2) {
          e2.printStackTrace();
          System.err.println("\n\nWarning:  could not bind to port "+port);
          System.err.println("The web server won't work now.\n");
        }
        
        
  }

  /** This is the method that is called when the server is started from
   *  the command line.
   */
  public static void main(String arg[]) {
    new QueryWebServer();
  }

}
