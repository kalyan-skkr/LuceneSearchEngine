package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.*;
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
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Search {
    public DblpRecordList SearchFile(String searchQuery, String field) throws Exception {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.IndexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(searchQuery);
        int count = 5;
        TopDocs results = searcher.search(query,count);
        List<DblpRecord> recordList = GetRecords(results, searcher);
        reader.close();
        int totalHits = Integer.parseInt(Arrays.stream(results.totalHits.toString().split(" ")).toList().get(0));
        DblpRecordList result = new DblpRecordList(totalHits, recordList.size(), recordList);
        return result;
    }
    private List<DblpRecord> GetRecords(TopDocs results, IndexSearcher searcher) throws IOException {
        List<DblpRecord> recordList = new ArrayList<DblpRecord>();
        for(int i = 0; i < results.scoreDocs.length; i++){
            Document doc = searcher.doc(results.scoreDocs[i].doc);
            float score = results.scoreDocs[i].score;
            recordList.add(GetRecord(doc, score));
        }
        return recordList;
    }

    private DblpRecord GetRecord(Document doc, Float score){
        int docId = Integer.parseInt(doc.get("docId"));
        Entry entry = new Entry(doc.get("entry"), doc.get("entry.mdate"),doc.get("entry.key"),doc.get("entry.publtype"), doc.get("entry.cdate"));
        Title title = (doc.get("title") != null) ? new Title(doc.get("title"), doc.get("title.bibTex")) : null;
        List<Author> authorList = GetAuthorList(doc);
        String year = doc.get("year");
        String school = doc.get("school");
        Publisher publisher = (doc.get("publisher") != null) ? new Publisher(doc.get("publisher"), doc.get("publisher.href")) : null;
        String number = doc.get("number");
        String pages = doc.get("pages");
        ISBN isbn = (doc.get("isbn") != null) ? new ISBN(doc.get("isbn"),doc.get("isbn.type")) : null;
        EE ee = (doc.get("ee") != null) ? new EE(doc.get("ee"),doc.get("ee.type")) : null;
        String month = doc.get("month");
        Series series = (doc.get("series") != null) ? new Series(doc.get("series"), doc.get("series.href")) : null;
        String volume = doc.get("volume");
        List<Note> noteList = GetNoteList(doc);
        String bookTitle = doc.get("booktitle");
        String crossRef = doc.get("crossref");
        Url url = (doc.get("url") != null) ? new Url(doc.get("url"),doc.get("url.type")) : null;
        Editor editor = (doc.get("editor") != null) ? new Editor(doc.get("editor"), doc.get("editor.orcid")) : null;
        Cite cite = (doc.get("cite") != null) ? new Cite(doc.get("cite"),doc.get("cite.label")) : null;
        String cdRom= doc.get("cdrom");
        String address= doc.get("address");
        String chapter= doc.get("chapter");
        String publnr= doc.get("publnr");

        DblpRecord rec = new DblpRecord(docId, score, entry,
                title, authorList, year, school, publisher, number, pages, isbn, ee, month, series,
                volume, noteList, bookTitle,crossRef, url, editor, cite, cdRom, address, chapter, publnr);

        return rec;
    }

    private List<Author> GetAuthorList(Document doc){
        String[] authorNames = doc.getValues("author");
        String[] authorBibtex = doc.getValues("author.bibtex");
        String[] authorOrcid = doc.getValues("author.orcid");
        String[] authorAux = doc.getValues("author.aux");

        List<Author> authors = new ArrayList<Author>();
        for(int i = 0; i < authorNames.length; i++){
            Author a = new Author(authorNames[i],
                    authorBibtex[i].equals(Constants.NaN) ? null : authorBibtex[i],
                    authorOrcid[i].equals(Constants.NaN) ? null : authorOrcid[i],
                    authorAux[i].equals(Constants.NaN) ? null : authorAux[i]);
            authors.add(a);
        }
        return authors;
    }

    private List<Note> GetNoteList(Document doc){
        List<Note> noteList = new ArrayList<Note>();
        String[] noteNameList = doc.getValues("note");
        String[] noteTypeList = doc.getValues("note.type");
        String[] noteLabelList = doc.getValues("note.label");

        for(int i = 0; i < noteNameList.length; i++){
            Note a = new Note(noteNameList[i],
                    noteTypeList[i].equals(Constants.NaN) ? null : noteTypeList[i],
                    noteLabelList[i].equals(Constants.NaN) ? null : noteLabelList[i]);
            noteList.add(a);
        }

        return noteList;
    }
}
