package com.ikazme.tasklist.model;

import java.util.Date;
import java.util.List;

public class NoteEntity {

    private int id;
    private Date date;
    private String text;
    private List<String> audioFilenames;

    public NoteEntity() {
    }

    public NoteEntity(int id, Date date, String text, List<String> audioFilenames) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.audioFilenames = audioFilenames;
    }

    public NoteEntity(Date date, String text, List<String> audioFilenames) {
        this.date = date;
        this.text = text;
        this.audioFilenames = audioFilenames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getAudioFilenames() {
        return audioFilenames;
    }

    public void setAudioFilenames(List<String> audioFilenames) {
        this.audioFilenames = audioFilenames;
    }

    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", audioFilenames=" + audioFilenames +
                '}';
    }
}
