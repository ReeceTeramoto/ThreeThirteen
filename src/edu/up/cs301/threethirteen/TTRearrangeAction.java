package edu.up.cs301.threethirteen;

import edu.up.cs301.game.GamePlayer;

/**
 * Class TTRearrangeAction. Rearranges cards.
 * @author Reece Teramoto
 * @version 12/5/14
 * 
 * update: CC 11/29/14 (added code for wantSwap)
 *
 */
public class TTRearrangeAction extends TTMoveAction{

	private static final long serialVersionUID = -3353298727395854517L;
	private int moveFrom;
    private int moveTo;
    
    //true if player wants the rearrange action to be a swap (card at moveFrom and
    //card at moveTo switch places)/false if player wants the rearrange action
    //to be an insert (card at moveFrom gets inserted in front of the card at moveTo)
    private boolean wantSwap;


    /*
     * constructor
     * @myPlayer
     * @from
     * @to
     * @swapPref whether the player wants a swap or insert
     */

    public TTRearrangeAction(GamePlayer myPlayer, int from, int to, boolean swapPref) {
    	super(myPlayer);
    	moveFrom = from;
    	moveTo = to;
        wantSwap = swapPref;
    }
    
    public final boolean isRearrange() {
    	return true;
    }

    public final int getMoveFrom() {
    	return moveFrom;
    }

    public final int getMoveTo() {
    	return moveTo;
    }
    
    public boolean getWantSwap()
    {
    		return wantSwap;
    }

}
