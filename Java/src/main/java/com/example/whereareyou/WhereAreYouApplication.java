package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.AutoComplete;
import com.example.whereareyou.Model.DblpRecord;
import com.example.whereareyou.Model.DblpRecordList;
import com.example.whereareyou.Model.Word2Vec;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@SpringBootApplication
@RestController
public class WhereAreYouApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhereAreYouApplication.class, args);
    }

    @GetMapping("/getrecords")
    public ResponseEntity<DblpRecordList> GetRecords(
            @RequestParam(name = "searchQuery") String searchQuery,
            @RequestParam(name = "field", defaultValue = "") String field,
            @RequestParam(name = "word2vec", defaultValue = "false") boolean flag_word2vec) throws Exception {

        searchQuery = SpellChecker(searchQuery);

        Index i = new Index();
        //DeleteIndex(Constants.IndexDir);
        //i.IndexFiles();

        Search s = new Search();
        List<DblpRecord> records = s.SearchFile(searchQuery, field);

        if(flag_word2vec){
            records = AddWord2VecRecords(searchQuery, field, records);
        }

        Map<Integer, DblpRecord> mapList = new HashMap<Integer, DblpRecord>();
        for(DblpRecord rec : records){
            if(!mapList.containsKey(rec.getId())){
                mapList.put(rec.getId(),rec);
            }
        }

        records = RemoveDuplicates(records);

        DblpRecordList resultSet = new DblpRecordList(0, records.size(), records);

        return ResponseEntity.ok(resultSet);
    }
    private List<DblpRecord> AddWord2VecRecords(String searchQuery, String field, List<DblpRecord> records) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        Search s = new Search();

        ResponseEntity<String> call= restTemplate.getForEntity("http://127.0.0.1:5000/word2vec?query="+searchQuery,String.class);
        if(call.getStatusCodeValue() == 200){
            List<Word2Vec> word2Vec = new Gson().fromJson(call.getBody(), new TypeToken<List<Word2Vec>>(){}.getType());
            if(word2Vec.size() > 0){
                for(Word2Vec w : word2Vec){
                    records.addAll(s.SearchFile(w.getKey(),field));
                }
            }
        }
        return records;
    }
    private void DeleteIndex(String dirPath){
        File dir = new File(dirPath);
        String[] files = dir.list();
        for(String s : files) {
            File curfile = new File(dir.getPath(),s);
            curfile.delete();
        }
    }
    private String SpellChecker(String searchQuery) throws IOException {
        String input_word = searchQuery.substring(0,searchQuery.length()-1);
        Directory directory = FSDirectory.open(Paths.get(Constants.spellIndexDir));
        PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get(Constants.dictionaryFile));
        SpellChecker checker = new SpellChecker(directory);

        //checker.indexDictionary(txt_dict, new IndexWriterConfig(new KeywordAnalyzer()), false);
        directory.close();

        //checker.setStringDistance(new JaroWinklerDistance());
        //checker.setStringDistance(new LevenshteinDistance());
        //checker.setStringDistance(new LuceneLevenshteinDistance());
        checker.setStringDistance(new NGramDistance());

        String[] checkedWords = checker.suggestSimilar(input_word, 10);

        List<String> checkList = new ArrayList<String>(Arrays.asList(checkedWords));
        if(!checkList.contains(searchQuery.toLowerCase())){
            searchQuery += "~";
        }
        return searchQuery;
    }
    private List<DblpRecord> RemoveDuplicates(List<DblpRecord> records){
        Map<Integer, DblpRecord> mapList = new HashMap<Integer, DblpRecord>();
        for(DblpRecord rec : records){
            if(!mapList.containsKey(rec.getId())){
                mapList.put(rec.getId(),rec);
            }
        }
        return mapList.values().stream().toList();
    }
    @GetMapping("/spellcheck")
    public ResponseEntity<AutoComplete> SpellCheck(@RequestParam(name="query") String query) throws IOException {
        // Creating the index
        String input_word = query;
        Directory directory = FSDirectory.open(Paths.get(Constants.spellIndexDir));
        PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get(Constants.dictionaryFile));
        SpellChecker checker = new SpellChecker(directory);

        //checker.indexDictionary(txt_dict, new IndexWriterConfig(new KeywordAnalyzer()), false);
        directory.close();

        //checker.setStringDistance(new JaroWinklerDistance());
        //checker.setStringDistance(new LevenshteinDistance());
        //checker.setStringDistance(new LuceneLevenshteinDistance());
        checker.setStringDistance(new NGramDistance());

        String[] suggestions = checker.suggestSimilar(input_word, 10);

        AutoComplete ac = new AutoComplete(new ArrayList<String>(Arrays.asList(suggestions)));
        return ResponseEntity.ok(ac);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> Suggest(@RequestParam(name="query") String query) throws Exception {
        Suggest ac = new Suggest();
        //DeleteIndex(Constants.AcIndexDir);
        List<String> res = ac.suggestTerms(query);

        return ResponseEntity.ok(res);
    }


}
