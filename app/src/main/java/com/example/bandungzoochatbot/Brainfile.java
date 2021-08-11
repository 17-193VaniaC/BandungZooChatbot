package com.example.bandungzoochatbot;

public class Brainfile {
    public String pertanyaan;
    public String jawaban;

    public Brainfile(){}

    public Brainfile(String q, String a){
        this.pertanyaan = q;
        this.jawaban = a;
    }

    public String getPertanyaan() {
        return this.pertanyaan;
    }

    public String getJawaban() {
        return this.jawaban;
    }
}
