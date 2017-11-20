/*
 * LakshyaSystemTray.java
 *
 * Created on October 27, 2007, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.SystemTray;

import LakshyaDeskTopSearchEngine.Index.StartSearchingModifiedFile;
import LakshyaDeskTopSearchEngine.QueryServer.QueryWebServer;
import LakshyaDeskTopSearchEngine.QueryServer.ResultPageParse;
import LakshyaDeskTopSearchEngine.SupportFile.FileUpdate;
import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.*;
import org.jdesktop.jdic.tray.*;


public class LakshyaSystemTray {
    
        
    public static JMenuItem quit;
    private static String[] webString = { "Lakshya","Desktop" };
    
    public LakshyaSystemTray() {
        JPopupMenu menu = new JPopupMenu("Tray Icon Menu");
        
        // Add Menu for Index Time days
        JMenu timeIndexItem = new JMenu("Index Time");
        //JMenu submenu = new JMenu();
        
            //Add sub Menu to Index Time
            JMenuItem oneDayTimeIndexItem = new JMenuItem("Every Day"); //Every Day
            oneDayTimeIndexItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        FileUpdate.modifyPropertiesFile("IndexTimeDay","1");
                }
                
            });
            timeIndexItem.add(oneDayTimeIndexItem);
            JMenuItem weekDayTimeIndexItem = new JMenuItem("Every Week"); //Every WEEK
            weekDayTimeIndexItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        FileUpdate.modifyPropertiesFile("IndexTimeDay","7");
                }
                
            });
            timeIndexItem.add(weekDayTimeIndexItem);
            menu.add(timeIndexItem);
            
            
        // Add Menu to Re-Index files
        JMenuItem reIndexItem = new JMenuItem("Re-Index");
        reIndexItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        StartSearchingModifiedFile.main(webString);
                }
                
        });
        
        menu.add(reIndexItem);
        //See About Html
        menu.addSeparator();
        JMenuItem aboutHtmlItem = new JMenuItem("about");
        aboutHtmlItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        ResultPageParse.getaboutPage(); 
             
                }
                
        });
        menu.add(aboutHtmlItem);
        //Quit application 
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                        QueryWebServer.webServerStart = false;
                        System.exit(0);   
                }
                
        });
        menu.add(quitItem);
        
        // Resource file "duke.gif" must exist at the same directory
        // When the image file is in another package of the jar file.
        ClassLoader cl = this.getClass().getClassLoader();
        System.out.println("Image Icon " + cl.getResource("images/target2.gif"));
        ImageIcon icon = new ImageIcon(cl.getResource("images/target2.gif"));
        //ImageIcon icon = new ImageIcon("images/target2.gif");
                //"E:\\Images\\duke.gif");
        TrayIcon ti = new TrayIcon(icon, "Lakshya Server Started", menu);

        // Action listener for left click.
        ti.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    ResultPageParse.getHomePage(); 
            }
        });
               
        SystemTray tray = SystemTray.getDefaultSystemTray();
        tray.addTrayIcon(ti);
    }

    public static void main(String[] args) {
        new LakshyaSystemTray();
        //Start WebServer
        QueryWebServer.main(webString);
        //Start the configure File;
        //Check if properties File is in local folder
        File tempFile = new File(System.getProperty("java.io.tmpdir")+"/HelpFile.properties");
        if(tempFile.exists())
        {
            HashMap propertiesFileRead = new HashMap();
            propertiesFileRead = FileUpdate.getProperties();
            if(propertiesFileRead.containsKey("lakshyaIndexStore")){
                File lakshyaStore = new File(propertiesFileRead.get("lakshyaIndexStore").toString());
                if(lakshyaStore.exists()){
                    ResultPageParse.getHomePage(); 
                    
                    //Set Initial Porperties
                    EngineRequirements.systemProperties = FileUpdate.getProperties();
                    System.out.println(" the index Folder is " + EngineRequirements.systemProperties.get("lakshyaIndexingDirectory"));
                    EngineRequirements.indexFolder = new File(EngineRequirements.systemProperties.get("lakshyaIndexingDirectory").toString()); 
                    EngineRequirements.indexFileStore = new File(EngineRequirements.systemProperties.get("lakshyaIndexStore").toString());
                    
                    //Get Stop File Extension
                    FileUpdate.getStopExtensionFile();
                }
                
            }
            
        }else{
            
            ResultPageParse copyPropertyFileToLocalDirectory = new ResultPageParse();
            
            try {
                BufferedReader in = copyPropertyFileToLocalDirectory.htmlFileReader(EngineRequirements.supportFile + "/HelpFile.properties");
                if(tempFile.createNewFile()){
                    System.out.println(tempFile.exists() + " tempFile path " + tempFile.getPath() + " file created");
                    BufferedWriter out = new BufferedWriter(new FileWriter(tempFile.getPath()));
                    String str = "",strExtra = "";
                    while((str = in.readLine()) != null){
                        strExtra = strExtra + str + "\n";
                    }
                    out.write(strExtra);
                    out.close();
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                
            }
            
            
            ResultPageParse.getConfigPage();
            
            
        }
        
        
        
        
        
        
        
    }   
}