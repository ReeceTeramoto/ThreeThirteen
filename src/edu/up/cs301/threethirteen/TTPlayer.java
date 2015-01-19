package edu.up.cs301.threethirteen;

import edu.up.cs301.game.GamePlayer;

/**
 * Abstract class TTPlayer
 * @author Reece Teramoto
 * @version 12/5/14
 *
 */


abstract class TTPlayer implements GamePlayer {
	
	// returns the player name
	public abstract String getPlayerName();
	
	// returns the player number
	public abstract int getPlayerNum();
	
	// sets the player number
	public abstract boolean setPlayerNum();
}
