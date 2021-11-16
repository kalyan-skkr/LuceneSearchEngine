package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Suggest {
    private static final String GRAMMED_WORDS_FIELD = "words";

    private static final String SOURCE_WORD_FIELD = "sourceWord";

    private static final String COUNT_FIELD = "count";

    private static final String[] ENGLISH_STOP_WORDS = {
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "i", "if", "in", "into", "is",
            "no", "not", "of", "on", "or", "s", "such",
            "t", "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"
    };

    static IndexWriter writer;
    public IndexReader reader;
    public IndexSearcher searcher;


    public void AutoCompleteIndex() throws Exception {
        try {
            String[] fieldsToAutocomplete = {"title"};
            Directory dir = FSDirectory.open(Paths.get(Constants.AcIndexDir));
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(Constants.IndexDir)));


            Analyzer analyzer2 = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents(String fieldName) {
                    Tokenizer source = new StandardTokenizer();
                    TokenStream stream = new LowerCaseFilter(source);
                    stream = new ASCIIFoldingFilter(stream);
                    stream = new NGramTokenFilter(stream,1,20,true);
                    return new TokenStreamComponents(source, stream);
                }
            };

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer2);
            writer = new IndexWriter(dir, iwc);


            Map<String, Integer> wordsMap = new HashMap<String, Integer>();
            for(String fieldToAutocomplete : fieldsToAutocomplete){
                LuceneDictionary dict = new LuceneDictionary(reader, fieldToAutocomplete);


                InputIterator iter = dict.getEntryIterator();
                while (iter.next() != null) {
                    String word = iter.next().utf8ToString();
                    System.out.println(word);

                    int len = word.length();
                    if (len < 3) {
                        continue; // too short we bail but "too long" is fine...
                    }

                    if (wordsMap.containsKey(word)) {
                        throw new IllegalStateException(
                                "This should never happen");
                        // wordsMap.put(word, wordsMap.get(word) + 1);
                    } else {
                        // use the number of documents this word appears in
                        wordsMap.put(word, reader.docFreq(new Term(
                                fieldToAutocomplete, word)));
                    }
                }
            }

            for (String word : wordsMap.keySet()) {
                // ok index the word
                Document doc = new Document();
                doc.add(new TextField(SOURCE_WORD_FIELD, word, Field.Store.YES)); // orig term
                doc.add(new TextField(GRAMMED_WORDS_FIELD, word, Field.Store.YES)); // grammed
                doc.add(new SortedDocValuesField(COUNT_FIELD, new BytesRef(wordsMap.get(word)))); // count
                System.out.println(wordsMap.get(word)+ "-" + word);
                writer.addDocument(doc);
            }
            writer.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            writer.close();
        }
    }

    public List<String> suggestTerms(String term) throws Exception {
        // get the top 5 terms for query
        Query query = new TermQuery(new Term(GRAMMED_WORDS_FIELD, term));
        Sort sort = new Sort(SortField.FIELD_SCORE,new SortField(COUNT_FIELD, SortField.Type.STRING));

        AutoCompleteIndex();

        Directory dir = FSDirectory.open(Paths.get(Constants.AcIndexDir));
        IndexReader acreader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(acreader);

        TopDocs docs = searcher.search(query,5, sort);
        List<String> suggestions = new ArrayList<String>();
        for(int i = 0; i < docs.scoreDocs.length; i++){
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            suggestions.add(doc.get(SOURCE_WORD_FIELD));
        }
        return suggestions;
    }
}
