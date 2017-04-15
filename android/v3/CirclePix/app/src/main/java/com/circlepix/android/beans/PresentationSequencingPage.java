package com.circlepix.android.beans;

/**
 * Created by relly on 3/27/15.
 */
public class PresentationSequencingPage {

    private String narrationType;
    private Class classFile;
    private int maleAudioFile;
    private int femaleAudioFile;

    public PresentationSequencingPage(String narrationType, Class classFile, int maleAudioFile, int femaleAudioFile) {
        this.narrationType = narrationType;
        this.classFile = classFile;
        this.maleAudioFile = maleAudioFile;
        this.femaleAudioFile = femaleAudioFile;
    }

    public String getNarrationType() {
        return narrationType;
    }

    public void setNarrationType(String narrationType) {
        this.narrationType = narrationType;
    }

    public Class getClassFile() {
        return classFile;
    }

    public void setClassFile(Class classFile) {
        this.classFile = classFile;
    }

    public int getMaleAudioFile() {
        return maleAudioFile;
    }

    public void setMaleAudioFile(int maleAudioFile) {
        this.maleAudioFile = maleAudioFile;
    }

    public int getFemaleAudioFile() {
        return femaleAudioFile;
    }

    public void setFemaleAudioFile(int femaleAudioFile) {
        this.femaleAudioFile = femaleAudioFile;
    }

}
