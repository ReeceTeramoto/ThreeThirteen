package edu.up.cs301.threethirteen;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A game-move object that a 3-13 player sends to the game to make
 * a move.
 * 
 * @author Reece Teramoto
 * @version 5 December 2014 11:05 PM
 * 
 * update: CC 11/13/14
 */


abstract class TTMoveAction extends GameAction {

	private static final long serialVersionUID = -3107101671012188849L;

    /*
     * Constructor for TTMoveAction
     * @param player
     */
    public TTMoveAction(GamePlayer player) {
    	super(player);
    }

    public boolean isDiscard() {
    	return false;
    }

    public boolean isGoOut() {
    	return false;
    }

    public boolean isDraw() {
    	return false;
    }

    public boolean isRearrange() {
    	return false;
    }


}
