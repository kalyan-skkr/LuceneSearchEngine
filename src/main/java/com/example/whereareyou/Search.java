package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.DblpRecord;
import com.example.whereareyou.Model.Entry;
import com.example.whereareyou.Model.Title;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Search {
    public void SearchFile(String searchQuery) throws Exception {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.IndexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse(searchQuery);
        TopDocs results = searcher.search(query,5);
        List<DblpRecord> recordList = GetRecords(results, searcher);
        System.out.println(results.totalHits + " total matching documents");
        System.out.println(recordList.stream().findFirst().get().getEntry().getValue());
        System.out.println(recordList.stream().findFirst().get().getTitle().getValue());
//        for (int i = 0; i < results.scoreDocs.length; i++) {
//            Document doc = searcher.doc(results.scoreDocs[i].doc);
//            String path = doc.get("path");
//            System.out.println((i + 1) + ". " + path);
//            String title = doc.get("title");
//            if (title != null) {
//                System.out.println("   Title: " + doc.get("title"));
//            }
//            List<IndexableField> v = doc.getFields();
//            for ( IndexableField field : v )
//            {
//                System.out.println("        " + field.name()+": "+doc.get(field.name()));
//            }
//
//        }
        reader.close();
    }
    private List<DblpRecord> GetRecords(TopDocs results, IndexSearcher searcher) throws IOException {
        List<DblpRecord> recordList = new ArrayList<DblpRecord>();
        for(int i = 0; i < results.scoreDocs.length; i++){
            Document doc = searcher.doc(results.scoreDocs[i].doc);
            Entry entry = new Entry(doc.get("entryType"), doc.get("entryType.mdate"),doc.get("entryType.key"),doc.get("entryType.publtype"), doc.get("entryType.cdate"));
            Title title = new Title(doc.get("title"), doc.get("title.bibTex"));
            DblpRecord rec = new DblpRecord();
            rec.setId(Integer.parseInt(doc.get("docId")));
            rec.setEntry(entry);
            rec.setTitle(title);

            recordList.add(rec);
        }
        return recordList;
    }
}
