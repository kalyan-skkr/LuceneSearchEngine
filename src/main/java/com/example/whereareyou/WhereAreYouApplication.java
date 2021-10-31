package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import com.example.whereareyou.Model.DblpRecord;
import com.example.whereareyou.Model.DblpRecordList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;
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

}
