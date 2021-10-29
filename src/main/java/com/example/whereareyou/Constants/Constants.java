package com.example.whereareyou.Constants;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String IndexDir  = "/Users/kalyansabbella/Documents/Test/TestIndex";
    //public static final String IndexDir  = "/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/MainIndex";
    public static final String DblpDir = "/Users/kalyansabbella/Documents/Test/Doc";
    public static final String DblpFile = "/Users/kalyansabbella/Documents/Test/Doc/dblp.xml";
    //public static final String DblpFile = "/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/dblp.xml";
    public static final List<String> entryTypes = Arrays.stream(new String[]{"article", "inproceedings", "proceedings", "book",
            "incollection", "phdthesis", "mastersthesis", "www"}).toList();


}