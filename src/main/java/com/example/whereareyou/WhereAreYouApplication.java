package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.AutoComplete;
import com.example.whereareyou.Model.DblpRecord;
import com.example.whereareyou.Model.DblpRecordList;
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
        DeleteIndex();
        i.IndexFiles();

        Search s = new Search();
        DblpRecordList records = s.SearchFile(searchQuery, field);
        return ResponseEntity.ok(records);
    }
    private void DeleteIndex(){
        File dir = new File(Constants.IndexDir);
        String[] files = dir.list();
        for(String s : files) {
            File curfile = new File(dir.getPath(),s);
            curfile.delete();
        }
    }
    @GetMapping("/autocomplete")
    public ResponseEntity<AutoComplete> AutoCompleteSuggestions(@RequestParam(name="query") String query) throws IOException {
        // Creating the index
        String input_word = query;
        Directory directory = FSDirectory.open(Paths.get("/Users/kalyansabbella/Documents/Test/SpellIndex"));
        PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get("/Users/kalyansabbella/Documents/Test/words.txt"));
        SpellChecker checker = new SpellChecker(directory);

        //checker.indexDictionary(txt_dict, new IndexWriterConfig(new KeywordAnalyzer()), false);
        directory.close();

        //checker.setStringDistance(new JaroWinklerDistance());
        checker.setStringDistance(new LevenshteinDistance());
        //checker.setStringDistance(new LuceneLevenshteinDistance());
        //checker.setStringDistance(new NGramDistance());

        String[] suggestions = checker.suggestSimilar(input_word, 5);

        AutoComplete ac = new AutoComplete(new ArrayList<String>(Arrays.asList(suggestions)));
        return ResponseEntity.ok(ac);
    }

}
