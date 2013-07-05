package be.kuleuven.noiseapp.soundbattle;

import java.io.Serializable;
import java.util.ArrayList;

import be.kuleuven.noiseapp.location.SoundBattleLocation;

public class ActiveSoundBattle implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long soundBattleID;
	private long opponentID;
	private ArrayList<SoundBattleLocation> SBLs;
	
	public ActiveSoundBattle(long soundBattleID){
		this.soundBattleID = soundBattleID;
	}

	/**
	 * @return the soundBattleID
	 */
	public long getSoundBattleID() {
		return soundBattleID;
	}

	/**
	 * @return the opponentID
	 */
	public long getOpponentID() {
		return opponentID;
	}

	/**
	 * @param opponentID the opponentID to set
	 */
	public void setOpponentID(int opponentID) {
		this.opponentID = opponentID;
	}

	/**
	 * @return the sBLs
	 */
	public ArrayList<SoundBattleLocation> getSBLs() {
		return SBLs;
	}

	/**
	 * @param sBLs the sBLs to set
	 */
	public void setSBLs(ArrayList<SoundBattleLocation> sBLs) {
		SBLs = sBLs;
//		new SaveSoundBattleLocations().execute(this);
	}

}
