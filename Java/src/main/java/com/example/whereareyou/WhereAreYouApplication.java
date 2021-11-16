package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.print.Doc;
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
            @RequestParam(name = "field", defaultValue = "title") String field,
            @RequestParam(name = "count", defaultValue = "5") int count,
            @RequestParam(name = "word2vec", defaultValue = "false") boolean flag_word2vec,
            @RequestParam(name = "doc2vec", defaultValue = "false") boolean flag_doc2vec,
            @RequestParam(name = "fuzzy", defaultValue = "false") boolean flag_fuzzy,
            @RequestParam(name = "proximity", defaultValue = "false") boolean flag_proximity
    ) throws Exception {
        Constants.TopHits = 0;

        String[] terms = searchQuery.split(" ");
        if(flag_proximity){
            if(terms.length <= 1){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DblpRecordList(0,0,null,"","", "Proximity cannot be applied for only one term"));
            }
        }
        if(flag_fuzzy){
            if(terms.length > 1){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DblpRecordList(0,0,null,"","", "Fuzziness cannot be applied to phrases"));
            }
        }

        List<String> spellCheckList = new ArrayList<>();
        boolean spellCorrection = false;
        if(terms.length == 1){
            spellCheckList = SpellChecker(searchQuery);
            if(!spellCheckList.contains(searchQuery.toLowerCase())){
                spellCorrection = true;
                searchQuery += "~";
                spellCheckList = SpellChecker(searchQuery + "/");
            }
        }

        Index i = new Index();
        //DeleteIndex(Constants.IndexDir);
        //i.IndexFiles();

        Search s = new Search();
        List<DblpRecord> records = s.SearchFile(searchQuery, field, flag_fuzzy, flag_proximity, count);

        List<String> msgList_Vectors = new ArrayList<>();
        List<Word2Vec> word2Vecs = new ArrayList<>();
        if(flag_word2vec){
            word2Vecs = AddWord2VecRecords(searchQuery, field, records, count);

            for(Word2Vec w : word2Vecs){
                msgList_Vectors.add(w.getKey());
                records.addAll(s.SearchFile(w.getKey(),field, false, false, count));
            }
        }

        List<Doc2Vec> doc2Vecs = new ArrayList<>();
        if(flag_doc2vec){
            doc2Vecs = AddDoc2VecRecords(searchQuery, field, records, count);

            for(Doc2Vec w : doc2Vecs){
                msgList_Vectors.add(w.getKey());
                records.addAll(s.SearchFile(w.getKey(),field, false, false, count));
            }
        }

        if(flag_word2vec){
            records = RemoveDuplicates(records);
        }

        if(records.size() <= 0){
            return ResponseEntity.ok().body(new DblpRecordList(0,0,null,"",spellCorrection ? "Did you mean: " + String.join(" / ", spellCheckList) : "",  "No Records Found"));
        }

        DblpRecordList resultSet = new DblpRecordList(Constants.TopHits, records.size(), records, String.join(", ", msgList_Vectors), spellCorrection ? "Did you mean: " + String.join(" / ", spellCheckList) : "","");

        return ResponseEntity.ok(resultSet);
    }
    private List<Word2Vec> AddWord2VecRecords(String searchQuery, String field, List<DblpRecord> records, int count) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        Search s = new Search();
        List<Word2Vec> word2Vecs = new ArrayList<>();

        ResponseEntity<String> call= restTemplate.getForEntity("http://127.0.0.1:5000/word2vec?query="+searchQuery,String.class);
        if(call.getStatusCodeValue() == 200){
            word2Vecs = new Gson().fromJson(call.getBody(), new TypeToken<List<Word2Vec>>(){}.getType());

        }
        return word2Vecs;
    }
    private List<Doc2Vec> AddDoc2VecRecords(String searchQuery, String field, List<DblpRecord> records, int count) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        Search s = new Search();
        List<Doc2Vec> doc2Vecs = new ArrayList<>();

        ResponseEntity<String> call= restTemplate.getForEntity("http://127.0.0.1:5000/doc2vec?query="+searchQuery,String.class);
        if(call.getStatusCodeValue() == 200){
            doc2Vecs = new Gson().fromJson(call.getBody(), new TypeToken<List<Doc2Vec>>(){}.getType());

        }
        return doc2Vecs;
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
        Directory directory = FSDirectory.open(Paths.get(Constants.spellIndexDir));
        PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get(Constants.dictionaryFile));
        SpellChecker checker = new SpellChecker(directory);

        checker.indexDictionary(txt_dict, new IndexWriterConfig(new KeywordAnalyzer()), false);
        directory.close();

        //checker.setStringDistance(new JaroWinklerDistance());
        //checker.setStringDistance(new LevenshteinDistance());
        //checker.setStringDistance(new LuceneLevenshteinDistance());
        checker.setStringDistance(new NGramDistance());

        String[] checkedWords = checker.suggestSimilar(input_word, 10);

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
        List<String> res = new ArrayList<>();
        if(query.length() >= 3){
            Suggest suggest = new Suggest();
            DeleteIndex(Constants.AcIndexDir);
            res = suggest.suggestTerms(query);
        }
        return ResponseEntity.ok(res);
    }


}
