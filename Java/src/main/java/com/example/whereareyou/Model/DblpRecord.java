package com.example.whereareyou.Model;

import java.util.List;

public class DblpRecord {
    public DblpRecord() {
    }

    public DblpRecord(int id, float score, Entry entry,
                      Title title, List<Author> author,
                      String year, String school,
                      Publisher publisher, String number,
                      String pages, ISBN isbn, EE ee,
                      String month, Series series,
                      String volume, List<Note> notes,
                      String bookTitle, String crossRef,
                      Url url, Editor editor, Cite cite,
                      String cdRom, String address,
                      String chapter, String publnr) {
        this.id = id;
        this.score = score;
        this.entry = entry;
        this.title = title;
        this.author = author;
        this.year = year;
        this.school = school;
        this.publisher = publisher;
        this.number = number;
        this.pages = pages;
        this.isbn = isbn;
        this.ee = ee;
        this.month = month;
        this.series = series;
        this.volume = volume;
        this.note = notes;
        this.bookTitle = bookTitle;
        this.crossRef = crossRef;
        this.url = url;
        this.editor = editor;
        this.cite = cite;
        this.cdRom = cdRom;
        this.address = address;
        this.chapter = chapter;
        this.publnr = publnr;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    private Entry entry;

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
    private Title title;

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    private List<Author> author;

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    private String year;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    private String school;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
    private Publisher publisher;

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    private String pages;

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
    private ISBN isbn;

    public ISBN getIsbn() {
        return isbn;
    }

    public void setIsbn(ISBN isbn) {
        this.isbn = isbn;
    }
    private EE ee;

    public EE getEe() {
        return ee;
    }

    public void setEe(EE ee) {
        this.ee = ee;
    }
    private String month;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
    private Series series;

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }
    private String volume;

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
    private List<Note> note;

    public List<Note> getNote() {
        return note;
    }

    public void setNote(List<Note> note) {
        this.note = note;
    }
    private String bookTitle;

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    private String crossRef;

    public String getCrossRef() {
        return crossRef;
    }

    public void setCrossRef(String crossRef) {
        this.crossRef = crossRef;
    }
    private Url url;

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }
    private Editor editor;

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }
    private Cite cite;

    public Cite getCite() {
        return cite;
    }

    public void setCite(Cite cite) {
        this.cite = cite;
    }
    private String cdRom;

    public String getCdRom() {
        return cdRom;
    }

    public void setCdRom(String cdRom) {
        this.cdRom = cdRom;
    }
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    private String chapter;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }
    private String publnr;

    public String getPublnr() {
        return publnr;
    }

    public void setPublnr(String publnr) {
        this.publnr = publnr;
    }
}
