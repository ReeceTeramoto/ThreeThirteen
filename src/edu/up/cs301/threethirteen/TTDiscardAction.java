/**
 * 
 */
package edu.up.cs301.threethirteen;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * A MoveAction subclass that represents a discard action by a player.This could
 * be a regular discard action or a "going out" discard action.
 * 
 * @author Alondra Audelo-Avendano
 * @version November 2014
 * @version 12/5/14
 *
 */
public class TTDiscardAction extends TTMoveAction {
	////////////////////////////////////////////////
	//************instance variables**************//
	////////////////////////////////////////////////
	
	private int discardCardIndex;    // The card the user has requested to discard.
	
	private boolean goOut;           // Set to true only when the user is discarding a card AND going out.
	
	
	/**
	 * Constructor for TTDiscardAction
	 * 
	 * @param player			source, the player requesting the move.
	 * @param cardToDiscard		the card player wishes to discard.
	 * @param isGoingOut		indicates whether or not player is also going out.
	 */
	public TTDiscardAction(GamePlayer player, int cardToDiscard, boolean isGoingOut) {
		// Initialize with the superclass constructor.
		super(player);

		// Initialize discardCardIndex and goOut to the parameter values.
		discardCardIndex = cardToDiscard;
		goOut = isGoingOut;
	}
	
	/**
	 * Method indicates that the action requested is a  valid discard action.
	 * @return true
	 */
	@Override
	public boolean isDiscard() {
		return true;
	}
	
	/**
	 * Getter method for boolean variable isGoingOut.
	 * @return goOut
	 */
	public boolean getIsGoingOut() {
		return goOut;
	}
	
	/**
	 * Getter method for the card the player wants to discard.
	 * @return discardCard
	 */
	public int getDiscardCardIndex() {
		return discardCardIndex;
	}
}
