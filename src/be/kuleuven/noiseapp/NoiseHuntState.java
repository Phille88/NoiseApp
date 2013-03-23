package be.kuleuven.noiseapp;

import android.app.Application;

public class NoiseHuntState extends Application {
	private boolean walkInTheParkDone;
	private boolean blitzkriegDone;
	private boolean partyTimeDone;
	private boolean riversideDone;
	private boolean trainspottingDone;
	private boolean morningGloryDone;

    public boolean isWalkInTheParkDone() {
        return walkInTheParkDone;
    }

    public void setWalkInTheParkDone(boolean walkInTheParkDone) {
        this.walkInTheParkDone = walkInTheParkDone;
    }

	public boolean isBlitzkriegDone() {
		return blitzkriegDone;
	}

	public void setBlitzkriegDone(boolean blitzkriegDone) {
		this.blitzkriegDone = blitzkriegDone;
	}

	public void setPartyTimeDone(boolean partyTimeDone) {
		this.partyTimeDone = partyTimeDone;
	}

	public boolean isPartyTimeDone() {
		return partyTimeDone;
	}

	public void setRiversideDone(boolean riversideDone) {
		this.riversideDone = riversideDone;
	}

	public boolean isRiversideDone() {
		return riversideDone;
	}

	public void setTrainspottingDone(boolean trainspottingDone) {
		this.trainspottingDone = trainspottingDone;
	}

	public boolean isTrainspottingDone() {
		return trainspottingDone;
	}

	public void setMorningGloryDone(boolean morningGloryDone) {
		this.morningGloryDone = morningGloryDone;
	}

	public boolean isMorningGloryDone() {
		return morningGloryDone;
	}

}
