

package LakshyaDeskTopSearchEngine.Index;
/*
 * FileDocument.java
 *
 * Created on October 30, 2007, 11:48 PM
 * Author: Dushyant Gadewal
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileReader;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/** A utility for making Lucene Documents from a File. */

public class FileDocument {
  /** Makes a document for a File.
    <p>
    The document has three fields:
    <ul>
    <li><code>path</code>--containing the pathname of the file, as a stored,
    untokenized field;
    <li><code>modified</code>--containing the last modified date of the file as
    a field as created by and
    <li><code>contents</code>--containing the full contents of the file, as a
    Reader field;
    */
  public static Document Document(File f)
   {
        // make a new, empty document
            Document doc = new Document();
	try{
            // Add the path of the file as a field named "path".  Use a field that is 
            // indexed (i.e. searchable), but don't tokenize the field into words.
            System.out.println("path of file to index is " + f.getPath());
            doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.UN_TOKENIZED));

            // Add the path of the file as a field named "filename".  Use a field that is 
            // indexed (i.e. searchable), but don't tokenize the field into words.
            String fileName = f.getName();
            System.out.println("filename "+ fileName.substring(0,fileName.indexOf(".")) + " " + fileName.substring(fileName.indexOf(".")+1));
            if(fileName.indexOf(".")!=-1){
                doc.add(new Field("filename", fileName.substring(0,fileName.indexOf(".")) + " " + fileName.substring(fileName.indexOf(".")+1), Field.Store.YES, Field.Index.TOKENIZED));
            }else{
                doc.add(new Field("filename", fileName.substring(0,fileName.indexOf("\\"))  , Field.Store.YES, Field.Index.TOKENIZED));
            }

            // Add the last modified date of the file a field named "modified".  Use 
            // a field that is indexed (i.e. searchable), but don't tokenize the field
            // into words.
            doc.add(new Field("modified",
                String.valueOf(f.lastModified()),
                Field.Store.YES, Field.Index.NO));
            System.out.println("modified file " + f.lastModified());    

            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in the system's default encoding.
            // If that's not the case searching for special characters will fail.
            doc.add(new Field("contents", new FileReader(f)));
          }catch(Exception e){
              
          }
        
    // return the document
    return doc;
  }
  
  public static Document DocumentStopExt(File f)
       throws java.io.FileNotFoundException {
	 
    // make a new, empty document
    Document doc = new Document();

    // Add the path of the file as a field named "path".  Use a field that is 
    // indexed (i.e. searchable), but don't tokenize the field into words.
    doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    
    // Add the path of the file as a field named "filename".  Use a field that is 
    // indexed (i.e. searchable), but don't tokenize the field into words.
    String fileName = f.getName();
    System.out.println("filename "+ fileName.substring(0,fileName.indexOf(".")) + " " + fileName.substring(fileName.indexOf(".")+1));
    doc.add(new Field("filename", fileName.substring(0,fileName.indexOf(".")) + " " + fileName.substring(fileName.indexOf(".")+1), Field.Store.YES, Field.Index.TOKENIZED));
    
    // Add the last modified date of the file a field named "modified".  Use 
    // a field that is indexed (i.e. searchable), but don't tokenize the field
    // into words.
    doc.add(new Field("modified",
        String.valueOf(f.lastModified()),
        Field.Store.YES, Field.Index.NO));
    System.out.println("modified file " + f.lastModified());    
    
    // return the document
    return doc;
  }

  private FileDocument() {}
}
    
