package LakshyaDeskTopSearchEngine.Search;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import LakshyaDeskTopSearchEngine.Util.EngineRequirements;

/** Simple command-line based search demo. */
public class SearchFiles {

  /** Use the norms from one field for all fields.  Norms are read into memory,
   * using a byte of memory per document per searched field.  This can cause
   * search of large collections with a large number of fields to run out of
   * memory.  If all of the fields contain only a single token, then the norms
   * are all identical, then single norm vector may be shared. */
  private static class OneNormsReader extends FilterIndexReader {
    private String field;

    public OneNormsReader(IndexReader in, String field) {
      super(in);
      this.field = field;
    }

    public byte[] norms(String field) throws IOException {
      return in.norms(this.field);
    }
  }

  private SearchFiles() {}

  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
    

    String index = EngineRequirements.mainIndex.toString();
    String field = "contents";
    String queries = null;
    int repeat = 0;
    boolean raw = false;
    String normsField = null;
    
    
    
    IndexReader reader = IndexReader.open(index);

    if (normsField != null)
      reader = new OneNormsReader(reader, normsField);

    Searcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer();

    BufferedReader in = null;
    if (queries != null) {
      in = new BufferedReader(new FileReader(queries));
    } else {
      in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    }
      QueryParser parser = new QueryParser(field, analyzer);
    while (true) {
      if (queries == null)                        // prompt the user
        System.out.println("Enter query: ");

      String line = in.readLine();

      if (line == null || line.length() == -1)
        break;

      line = line.trim();
      if (line.length() == 0)
        break;
      
      Query query = parser.parse(line);
      System.out.println("Searching for: " + query.toString(field));

      Hits hits = searcher.search(query);
      
      if (repeat > 0) {                           // repeat & time as benchmark
        Date start = new Date();
        for (int i = 0; i < repeat; i++) {
          hits = searcher.search(query);
        }
        Date end = new Date();
        System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
      }

      System.out.println(hits.length() + " total matching documents");

      final int HITS_PER_PAGE = 10;
      for (int start = 0; start < hits.length(); start += HITS_PER_PAGE) {
        int end = Math.min(hits.length(), start + HITS_PER_PAGE);
        for (int i = start; i < end; i++) {

          if (raw) {                              // output raw format
            System.out.println("doc="+hits.id(i)+" score="+hits.score(i));
            continue;
          }

          Document doc = hits.doc(i);
          String path = doc.get("path");
          if (path != null) {
            System.out.println((i+1) + ". " + path);
            String title = doc.get("title");
            if (title != null) {
              System.out.println("   Title: " + doc.get("title"));
            }
          } else {
            System.out.println((i+1) + ". " + "No path for this document");
          }
        }

        if (queries != null)                      // non-interactive
          break;
        
        if (hits.length() > end) {
          System.out.println("more (y/n) ? ");
          line = in.readLine();
          if (line.length() == 0 || line.charAt(0) == 'n')
            break;
        }
      }
    }
    reader.close();
  }
}
