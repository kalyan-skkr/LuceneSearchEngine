package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.AutoComplete;
import com.example.whereareyou.Model.DblpRecord;
import com.example.whereareyou.Model.DblpRecordList;
import com.example.whereareyou.Model.Word2Vec;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
public class WhereAreYouApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhereAreYouApplication.class, args);
    }

    @GetMapping("/getrecords")
    public ResponseEntity<DblpRecordList> GetRecords(
            @RequestParam(name = "searchQuery") String searchQuery,
            @RequestParam(name = "field") String field) throws Exception {
        Index i = new Index();
        //DeleteIndex(Constants.IndexDir);
        //i.IndexFiles();

        Search s = new Search();
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> call= restTemplate.getForEntity("http://127.0.0.1:5000/word2vec?query=potato",String.class);

        List<DblpRecord> records = s.SearchFile(searchQuery, field);

        if(call.getStatusCodeValue() == 200){
            List<Word2Vec> word2Vec = new Gson().fromJson(call.getBody(), new TypeToken<List<Word2Vec>>(){}.getType());
            if(word2Vec.size() > 0){
                for(Word2Vec w : word2Vec){
                    records.addAll(s.SearchFile(w.getKey(),field));
                }
            }
        }

        DblpRecordList resultSet = new DblpRecordList(0, records.size(), records);

        return ResponseEntity.ok(resultSet);
    }
    private void DeleteIndex(String dirPath){
        File dir = new File(dirPath);
        String[] files = dir.list();
        for(String s : files) {
            File curfile = new File(dir.getPath(),s);
            curfile.delete();
        }
    }
    @GetMapping("/spellcheck")
    public ResponseEntity<AutoComplete> SpellCheck(@RequestParam(name="query") String query) throws IOException {
        // Creating the index
        String input_word = query;
        Directory directory = FSDirectory.open(Paths.get("/Users/kalyansabbella/Documents/Test/SpellIndex"));
        PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get("/Users/kalyansabbella/Documents/Test/words.txt"));
        SpellChecker checker = new SpellChecker(directory);

        //checker.indexDictionary(txt_dict, new IndexWriterConfig(new KeywordAnalyzer()), false);
        directory.close();

        //checker.setStringDistance(new JaroWinklerDistance());
        //checker.setStringDistance(new LevenshteinDistance());
        checker.setStringDistance(new LuceneLevenshteinDistance());
        //checker.setStringDistance(new NGramDistance());

        String[] suggestions = checker.suggestSimilar(input_word, 5);

        AutoComplete ac = new AutoComplete(new ArrayList<String>(Arrays.asList(suggestions)));
        return ResponseEntity.ok(ac);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> Suggest(@RequestParam(name="query") String query) throws Exception {
        Suggest ac = new Suggest();
        DeleteIndex(Constants.AcIndexDir);
        List<String> res = ac.suggestTerms(query);

        return ResponseEntity.ok(res);
    }


    @GetMapping("/testrest")
    public List<Word2Vec> RestTest() throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> call= restTemplate.getForEntity("http://127.0.0.1:5000/word2vec?query=intelligence",String.class);
        List<Word2Vec> word2Vec = new Gson().fromJson(call.getBody(), new TypeToken<List<Word2Vec>>(){}.getType());
        return word2Vec;
    }

}
