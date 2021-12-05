package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.*;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@SpringBootApplication
@RestController
public class WhereAreYouApplication {

    private static SpellChecker checker;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(WhereAreYouApplication.class, args);
        checker = createAndSetSpellIndex();
//        checker = setSpellIndex();
    }

    private static SpellChecker createAndSetSpellIndex() throws IOException {
        Directory directory = FSDirectory.open(Paths.get(Constants.spellIndexDir));
        PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get(Constants.dictionaryFile));
        SpellChecker checker = new SpellChecker(directory);

        checker.indexDictionary(txt_dict, new IndexWriterConfig(new KeywordAnalyzer()), false);
        directory.close();

        //checker.setStringDistance(new JaroWinklerDistance());
//        checker.setStringDistance(new LevenshteinDistance());
        //checker.setStringDistance(new LuceneLevenshteinDistance());
        checker.setStringDistance(new NGramDistance());

        return checker;
    }

    private static SpellChecker setSpellIndex() throws IOException {
        Directory directory = FSDirectory.open(Paths.get(Constants.spellIndexDir));
        SpellChecker checker = new SpellChecker(directory);
        checker.setSpellIndex(directory);
        return checker;
    }

    @GetMapping("/getrecords")
    public ResponseEntity<DblpRecordList> GetRecords(
            @RequestParam(name = "searchQuery") String searchQuery,
            @RequestParam(name = "field", defaultValue = "title") String field,
            @RequestParam(name = "count", defaultValue = "5") int count,
            @RequestParam(name = "fuzzy", defaultValue = "false") boolean flag_fuzzy,
            @RequestParam(name = "proximity", defaultValue = "false") boolean flag_proximity
    ) throws Exception {
        Constants.TopHits = 0;

        String[] terms = searchQuery.split(" ");
        if(flag_proximity){
            if(terms.length <= 1){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DblpRecordList(0,0,null,null,null,"", "Proximity cannot be applied for only one term"));
            }
        }
        if(flag_fuzzy){
            if(terms.length > 1){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DblpRecordList(0,0,null,null,null,"", "Fuzziness cannot be applied to phrases"));
            }
        }

        List<String> spellCheckList = new ArrayList<>();
        String correctSpelling = "";
        boolean spellCorrection = false;
        for(int i =0; i<terms.length;i++){
            boolean correct = true;
            spellCheckList = SpellChecker(terms[i]);
            if(spellCheckList.size() > 0 && !spellCheckList.contains(terms[i].toLowerCase())){
                spellCheckList = SpellChecker(terms[i] + "/");
                correct = false;
            }
            terms[i] = correct ? terms[i] : spellCheckList.get(0);
        }
        correctSpelling = String.join(" ", terms);
        if(!correctSpelling.equalsIgnoreCase(searchQuery)){
            searchQuery = correctSpelling;
            spellCorrection = true;
        }

        Index i = new Index();
        //DeleteIndex(Constants.IndexDir);
        //i.IndexFiles();

        Search s = new Search();
        List<DblpRecord> records = s.SearchFile(searchQuery, field, flag_fuzzy, flag_proximity, count);
        List<Word2Vec> word2Vecs = HttpHelper.expandQuery(searchQuery);
        List<Doc2Vec> doc2Vecs = HttpHelper.similarDocuments(searchQuery);

        records = RemoveDuplicates(records);

        if(records.size() <= 0){
            return ResponseEntity.ok().body(new DblpRecordList(0,0,null,null,null,spellCorrection ? "Did you mean: " + correctSpelling  + "</i>?" : "",  "No Records Found"));
        }

        DblpRecordList resultSet = new DblpRecordList(Constants.TopHits, records.size(), records, word2Vecs,doc2Vecs, spellCorrection ? "Did you mean: <i>" + correctSpelling + "</i>?" : "","");

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
    private List<String> SpellChecker(String searchQuery) throws IOException {
        String input_word = searchQuery.substring(0,searchQuery.length()-1);
        String[] checkedWords = checker.suggestSimilar(input_word, 100);
        List<String> checkList = new ArrayList<String>(Arrays.asList(checkedWords));
        return checkList;
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

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> Suggest(@RequestParam(name="query") String query) throws Exception {
        List<String> suggestions = HttpHelper.getAutoCompleteSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }


}
