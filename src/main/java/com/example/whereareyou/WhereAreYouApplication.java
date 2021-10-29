package com.example.whereareyou;

import com.example.whereareyou.Constants.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;

@SpringBootApplication
@RestController
public class WhereAreYouApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhereAreYouApplication.class, args);
    }

    @GetMapping("/test")
    public void test(@RequestParam(name = "searchQuery") String searchQuery) throws Exception {
        //testcommit
        DBLP i = new DBLP();
        DeleteIndex();
        long startTime = System.currentTimeMillis();
        System.out.println("Started at: " + LocalDateTime.now());
        i.IndexFiles();
        long endTime = System.currentTimeMillis();
        System.out.println("Ended at: " + LocalDateTime.now());
        long ms = endTime - startTime;
        System.out.println("Time taken: " +ms/(1000)+" seconds");
        Search s = new Search();
        s.SearchFile(searchQuery);
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
