package edu.up.cs301.threethirteen;

import android.view.View;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.infoMsg.GameInfo;


/**
 * @author Ashley Rosenberg
 * @version December 5, 2014
 * 
 * update: CC 11/16/14 6:55pm
 */
public class TTComputerPlayer extends GameComputerPlayer {
	
	private TTState computerGameState; //holds the ComputerPlayer's version of the game's state

	/** 
	 * Constructor for objects of type TTComputerPlayer,
	 * calls GameComputerPlayer's constructor
	 * @param name
	 */
	public TTComputerPlayer(String name) {
		super(name);
	}

	@Override
	/**
	 * The AI receives a new GameState from the LocalGame
	 */
	protected void receiveInfo(GameInfo info) {
		//checks if the given GameInfo is of type TTState
		if (!(info instanceof TTState)) {
    			return;
    		}

		//if the given GameInfo is of type TTState, set it as the new TTState
		this.setGameState(info);
	}
	
	/**
	 * 
	 * @return the AI's TTState Variable
	 */
	protected TTState getGameState(){
		return this.computerGameState;
	}
	
	/**
	 * Sets the AI's TTstate variable to a new TTState object
	 * @param newState new TTState
	 */
	protected void setGameState(GameInfo newState){
		if(newState instanceof TTState){
			this.computerGameState = (TTState)newState;
		}
	}
	
	/**
	*
	*@return the player's index in the array of players
	*/
	public int getPlayerNum()
	{
		return this.playerNum;
	}

}
