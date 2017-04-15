package com.circlepix.android.beans;

import com.circlepix.android.types.NarrationType;
import com.circlepix.android.types.BackgroundMusicType;

import java.io.Serializable;

/**
 * Created by relly on 5/1/15.
 */
public class ApplicationSettings implements Serializable {

    private boolean displayCompanyLogo = false;
    private boolean displayCompanyName = false;
    private boolean displayAgentImage = false;
    private boolean displayAgentName = false;
    private NarrationType narration = NarrationType.female;
    //July 9, 2015: KBL
    private BackgroundMusicType music = BackgroundMusicType.song1;
    private boolean applyToExistingPres = false;

    public boolean isDisplayCompanyLogo() {
        return displayCompanyLogo;
    }

    public void setDisplayCompanyLogo(boolean displayCompanyLogo) {
        this.displayCompanyLogo = displayCompanyLogo;
    }

    public boolean isDisplayCompanyName() {
        return displayCompanyName;
    }

    public void setDisplayCompanyName(boolean displayCompanyName) {
        this.displayCompanyName = displayCompanyName;
    }

    public boolean isDisplayAgentImage() {
        return displayAgentImage;
    }

    public void setDisplayAgentImage(boolean displayAgentImage) {
        this.displayAgentImage = displayAgentImage;
    }

    public boolean isDisplayAgentName() {
        return displayAgentName;
    }

    public void setDisplayAgentName(boolean displayAgentName) {
        this.displayAgentName = displayAgentName;
    }

    public NarrationType getNarration() {
        return narration;
    }

    public void setNarration(NarrationType narration) {
        this.narration = narration;
    }


    public BackgroundMusicType getMusic() {
        return music;
    }

    public void setMusic(BackgroundMusicType music) {
        this.music = music;
    }

    public boolean isApplyToExistingPres() {
        return applyToExistingPres;
    }

    public void setApplyToExistingPres(boolean applyToExistingPres) {
        this.applyToExistingPres = applyToExistingPres;
    }
}
