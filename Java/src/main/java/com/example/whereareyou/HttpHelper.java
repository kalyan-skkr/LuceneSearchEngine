package com.example.whereareyou;

import com.example.whereareyou.Model.Doc2Vec;
import com.example.whereareyou.Model.PaperType;
import com.example.whereareyou.Model.Word2Vec;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpHelper {
    private static String domain = "http://127.0.0.1:5000/";
    public static List<Word2Vec> expandQuery(String searchQuery) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        List<Word2Vec> word2Vecs = new ArrayList<>();
        try{
            ResponseEntity<String> call = restTemplate.getForEntity(domain + "word2vec?query="+searchQuery,String.class);
            if(call.getStatusCodeValue() == 200){
                word2Vecs = new Gson().fromJson(call.getBody(), new TypeToken<List<Word2Vec>>(){}.getType());
            }
        }
        catch (Exception e){
            return word2Vecs;
        }
        return word2Vecs;
    }

    public static List<Doc2Vec> similarDocuments(String searchQuery) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        List<Doc2Vec> doc2Vecs = new ArrayList<>();
        try{
            ResponseEntity<String> call= restTemplate.getForEntity(domain + "doc2vec?query="+searchQuery,String.class);
            if(call.getStatusCodeValue() == 200){
                doc2Vecs = new Gson().fromJson(call.getBody(), new TypeToken<List<Doc2Vec>>(){}.getType());

            }
        }
        catch (Exception e){
            return doc2Vecs;
        }
        return doc2Vecs;
    }

    public static PaperType getPaperType(String searchQuery) throws Exception {
        int paperType = 0;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> call;
        try {
            call = restTemplate.getForEntity("http://127.0.0.1:5000/classify?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8), String.class);
            if(call.getStatusCodeValue() == 200){
                paperType = Integer.parseInt(new Gson().fromJson(call.getBody(), String.class));
            }
        }
        catch (Exception e){
            return PaperType.Other;
        }
        PaperType paperType_enum = paperType == 1 ? PaperType.Other : PaperType.SoftwareEngineering;
        return paperType_enum;
    }
    public static List<String> getAutoCompleteSuggestions(String searchQuery) throws Exception {
        List<String> suggestions = new ArrayList<String>();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> call;
        try {
            call = restTemplate.getForEntity("http://127.0.0.1:5000/autocomplete?query=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8), String.class);
            if(call.getStatusCodeValue() == 200){
                suggestions = new Gson().fromJson(call.getBody(), new TypeToken<List<String>>(){}.getType());
            }
        }
        catch (Exception e){
            return suggestions;
        }
        return suggestions;
    }
}
