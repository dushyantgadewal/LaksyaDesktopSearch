/*
 * DeleteIndex.java
 *
 * Created on October 30, 2007, 11:48 PM
 * Author: Dushyant Gadewal
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package LakshyaDeskTopSearchEngine.Index;

import LakshyaDeskTopSearchEngine.Util.EngineRequirements;
import java.io.IOException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.LockObtainFailedException;

/**
 *
 * @author Dushyant
 */
public class DeleteIndex {
    
    /** Creates a new instance of DeleteIndex */
    public DeleteIndex() {
    }
    
    public static void deleteSingleIndex(String filePath){
        
        try {
            IndexReader indexReader = IndexReader.open(EngineRequirements.indexFileStore);
            ///indexReader.delete(1);
            indexReader.deleteDocuments(new Term("path",filePath));
            indexReader.close();
        } catch (LockObtainFailedException ex) {
            ex.printStackTrace();
        } catch (StaleReaderException ex) {
            ex.printStackTrace();
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
    }
    
}
