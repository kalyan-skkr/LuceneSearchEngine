package com.example.whereareyou.Constants;

import java.util.Arrays;
import java.util.List;

public class Constants {
    //public static final String IndexDir  = "/Users/kalyansabbella/Documents/Test/TestIndex";
    public static final String IndexDir  = "/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Index";
    public static final String DblpDir = "/Users/kalyansabbella/Documents/Test/Doc";
    //public static final String DblpFile = "/Users/kalyansabbella/Documents/Test/Doc/dblp.xml";
    public static final String DblpFile = "/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/dblp.xml";
    public static final List<String> entryTypes = Arrays.stream(new String[]{"article", "inproceedings", "proceedings", "book",
            "incollection", "phdthesis", "mastersthesis", "www"}).toList();
    public static final List<String> metaTags = Arrays.stream(new String[]{"ref","sup","sub","i","tt"}).toList();

    public static final List<String> authorAtts = Arrays.stream(new String[]{"bibtex","orcid","aux"}).toList();
    public static final List<String> noteAtts = Arrays.stream(new String[]{"type","label"}).toList();
    public static final String NaN = "NaN";

    public static final String dictionaryFile = "/Users/kalyansabbella/Documents/Test/words_alpha.txt";
    public static final String spellIndexDir = "/Users/kalyansabbella/Documents/Test/SpellIndex";

    public static int TopHits = 0;


}
