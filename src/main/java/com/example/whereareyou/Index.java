package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.*;

public class Index extends DefaultHandler {
    static IndexWriter writer;
    private int counter = 0;

    private Document doc;

    private StringBuilder elementBuffer = new StringBuilder();

    public void startElement(String uri, String localName,
                             String qName, Attributes atts)
            throws SAXException {
        String parentQName = qName;
        if(Constants.entryTypes.contains(qName)){
            doc = new Document();
            parentQName = "entryType";
            doc.add(new TextField("docId", Integer.toString(++counter),Field.Store.YES));
            doc.add(new TextField(parentQName, qName, Field.Store.YES));
        }
        if(!Constants.metaTags.contains(qName)){
            elementBuffer.setLength(0);
        }
        for (int i = 0; i < atts.getLength(); i++) {
            String key = parentQName + "." + atts.getQName(i);
            String value = "";
            if(doc.get(key)!=null){
                value = doc.get(key) + ";;" + atts.getValue(i);
                doc.removeField(key);
            }
            else{
                value = atts.getValue(i);
            }
            doc.add(new TextField(key, value, Field.Store.YES));
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(!Constants.entryTypes.contains(qName) && !qName.equals("dblp")){
            String value = "";
            if(doc.get(qName) != null){
                value = doc.get(qName) + ";;" + elementBuffer.toString();
                doc.removeField(qName);
            }
            else{
                value = elementBuffer.toString();
            }
            doc.add(new TextField(qName, value, Field.Store.YES));
        }

        if(Constants.entryTypes.contains(qName)){
            try {
                writer.addDocument(doc);
                System.out.println(counter + ") "+ doc.get("title"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void characters(char[] ch, int start, int length) throws SAXException{
        elementBuffer.append(ch, start, length);
    }
    public void IndexFiles() throws Exception {
        Directory dir = FSDirectory.open(Paths.get(Constants.IndexDir));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, iwc);


        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        XMLReader reader=saxParser.getXMLReader();
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        reader.setFeature("http://xml.org/sax/features/validation", false);
        saxParser.parse(new File(Constants.DblpFile), new Index());

        writer.close();

    }
}
