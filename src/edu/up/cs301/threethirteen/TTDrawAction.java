/**
 * 
 */
package edu.up.cs301.threethirteen;

import edu.up.cs301.game.GamePlayer;

/**
 * A MoveAction subclass that represents a draw action by a player.This could
 * be a draw from the stock pile or the draw pile.
 * 
 * @author Alondra Audelo-Avendano
 * @version November 2014
 * @version 12/5/14
 */
public class TTDrawAction extends TTMoveAction {

	////////////////////////////////////////////////
	//************instance variables**************//
	////////////////////////////////////////////////
	
	private boolean fromDiscard;    // Set to true only when the user is requesting to draw 
									// from he discard pile.
	
	/**
	 * Constructor for TTDrawAction
	 * 
	 * @param player 		The source, the player requesting the action.
	 * @param drawFromD		Indicates whether or not the user will be drawing from the discard pile.
	 */
	public TTDrawAction(GamePlayer player, boolean drawFromD) {
		// Initialize the source with the superclass constructor.
		super(player);

		// Initialize fromDiscard to the parameter value.
		fromDiscard = drawFromD;
	}
	
	/**
	 * Method indicates that the action requested is a valid draw action.
	 * @return true
	 */
	@Override
	public boolean isDraw() {
		return true;
	}
	
	/**
	 * Getter method for boolean variable fromDiscard. 
	 * @return fromDiscard
	 */
	public boolean getFromDiscard() {
		return fromDiscard;
	}
}
