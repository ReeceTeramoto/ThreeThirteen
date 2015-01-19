package edu.up.cs301.threethirteen;

import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.card.*;

/**
 * This is an AI that randomly draws a card and discards a random card during its turn
 * 
 * @author Ashley Rosenberg
 * @version December 5, 2014
 * 
 */
public class TTComputerPlayerDumb extends TTComputerPlayer {

	private boolean triedToGoOut; //keeps track of whether the AI has tried to discard this turn or not
	
	/**
	 * Constructor for objects of type TTComputerPlayerDumb.  
	 * Uses the super constructor from TTComputerPlayer
	 * 
	 * @param name the name of the AI
	 */
	public TTComputerPlayerDumb(String name){
		super(name);
		triedToGoOut = false;
	}
	/**
	 * Chooses a random card from the AI's deck to discard 
	 * 
	 * @return indexOdDiscard  The index of the card the AI is to discard 
	 */
	private int cardToDiscard(){
		//the number of cards in the AI's hand is always 2 greater than the round number
		//therefore to get the number of cards in the AI's hand, add 2 to the round number
		int numCards = this.getGameState().getPlayersHand(getPlayerNum()).size();

		//randomly choose the index of a card to discard from the AI's hand
		int indexOfDiscard = (int) (numCards*Math.random());
		return indexOfDiscard;
	}

	@Override
	/**
	 * Overrrides TTComputerPlayer's receive info method. Player randomly draws a card and then 
	 * randomly discards a card
	 * 
	 */
	protected void receiveInfo(GameInfo info) {
		//receive the given game info and set it as the gameState
		super.receiveInfo(info);

		//check if it is the player's turn
		if((this.getGameState().getCurrentPlayer()) == this.playerNum){

			//Delay the AI for 3 seconds
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(this.getGameState().getCanDraw() == true){
				
				//randomly choose a pile to draw from
				int pileToDrawFrom = (int) (2*Math.random());

				//create boolean variables for pertaining to which pile to draw from for the TTDrawAction
				boolean fromDiscard;

				//set the boolean fromDiscard based on the randomly generated number
				if(pileToDrawFrom == 0){
					fromDiscard = false;
				}
				else{
					fromDiscard = true;
				}

				//send the TTDrawAction to the Game
				game.sendAction(new TTDrawAction(this, fromDiscard));
			}
			//if the draw is successful, move on to the discard phase
			else{
				//choose a card to discard
				int discardCard = this.cardToDiscard();

				//while the card chosen to be discarded is null, choose another card to discard
				while (this.getGameState().getPlayersHand(getPlayerNum()).
						getCardAtIndex(discardCard) == null)
				{
					discardCard = this.cardToDiscard();
				}

				//first try to go out and if that fails, just normally discard
				if(triedToGoOut == false){
					game.sendAction(new TTDiscardAction(this, discardCard, true));
					triedToGoOut = true;
				}
				else{
					//The AI attempts to discard and go out 
					game.sendAction(new TTDiscardAction(this, discardCard, false));
					triedToGoOut = false;
				}
			}
		}

	}
}
