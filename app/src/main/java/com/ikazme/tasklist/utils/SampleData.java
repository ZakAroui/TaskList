package com.ikazme.tasklist.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SampleData {

    private static final String SAMPLE_TEXT_1 = "simple note";
    private static final String SAMPLE_TEXT_2 = "multiple \n line";
    private static final String SAMPLE_TEXT_3 = "this note is a very long one that even the writer of this note cannot\" +\n" +
            "                \"keep up with the speed of the rabbit who is running on the TV in front of him :P";




//    public static List<NoteEntity> getNotes(){
//        List<NoteEntity> notes = new ArrayList<>();
//        notes.add(new NoteEntity(1, getDate(0), SAMPLE_TEXT_1, Collections.<String>emptyList()));
//        notes.add(new NoteEntity(2, getDate(-1), SAMPLE_TEXT_2, Collections.<String>emptyList()));
//        notes.add(new NoteEntity(3, getDate(-2), SAMPLE_TEXT_3, Collections.<String>emptyList()));
//        return notes;
//    }

    private static Date getDate(int diff) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MILLISECOND, diff);
        return cal.getTime();
    }
}
