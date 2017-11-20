/*
 * PageWiseDisplay.java
 *
 * Created on October 8, 2007, 2:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.Util;

import org.apache.log4j.Logger;

/**
 *
 * @author Dushyant
 */
public class PageWiseDisplay {
    
    static Logger myLogger =
               Logger.getLogger(PageWiseDisplay.class.getName());
    
    /** Creates a new instance of PageWiseDisplay */
    public PageWiseDisplay() {
    }
    
    public static void main(String arg[]){
        int total = 234005;
        int perPage = 10;
        int startPage = 6;
        
        final int HITS_PER_PAGE = 10;
        
        int start = 233800;
        int end = Math.min(total, start + HITS_PER_PAGE);
        
        myLogger.debug("Current page the Start=" + start + "  end="+end);
        
        if((Math.min(total, start + HITS_PER_PAGE)-10) > 0)
            myLogger.debug("The Prev " + (Math.min(total, start + HITS_PER_PAGE)-10));
        
        if((Math.min(total, start + HITS_PER_PAGE)+100) < total)
            myLogger.debug("The Next " + (Math.min(total, start + HITS_PER_PAGE)+100));
        
        int pageWise = start;
        while(pageWise < (end+100)){
            myLogger.debug(" Page No. " + (pageWise/10) +  "   page wise the Start=" + pageWise + "  end="+ Math.min(total, pageWise+10));
            pageWise = pageWise + 10;
        }
        
        
        //myLogger.debug((double)(231/10.0));
//        for (int i = start; i < end; i++) {
//            myLogger.debug("doc= "+i+"      score= "+i);
//          }
        
      }
        
    }
