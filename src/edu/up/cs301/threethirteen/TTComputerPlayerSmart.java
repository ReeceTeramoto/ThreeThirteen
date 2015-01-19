package edu.up.cs301.threethirteen;

import java.util.ArrayList;
import android.util.Log;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.card.Suit;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.threethirteen.Deck;

/**
* Class: TTComputerPlayerSmart
*
* An AI that arranges it's hand into runs and sets and chooses a random card that isn't a part of a run or set 
* or from a partial run or set if it only has partial runs and sets
*
* @author: Ashley Rosenberg
* @author: Reece Teramoto
* @version: December 5, 2014
*
* Known issue: The smart AI does not put null spaces between runs and sets and therefore 
* 			   in later rounds will have a less likely chance of being able to go out
*/
public class TTComputerPlayerSmart extends TTComputerPlayer {

	private ArrayList<Integer> priorityNums; //Corresponds to a Card at the same index of the AI's hand
	private boolean canGoOut; //A boolean that keeps track of whether the AI can go out or not
	private Deck idealDeck;  //A Deck that represents the best possible version of the AI's hand
	private boolean isEqual;  //a boolean that is true if the AI's hand matches the idealDeck
	
	/**
	 * Constructor for a TTComputerPlayerSmart object
	 */
	public TTComputerPlayerSmart(String name){
		super(name);
		priorityNums = new ArrayList<Integer>(); //initialize the priorityNums array
		canGoOut = false;
		isEqual = false;
	}

	/**
	 * Arranges the AI's hand into the "best" possible hand
	 * @param newTempDeck the deck to be arranged into the best possible hand
	 * 
	 * @return a Deck of the cards passed as a parameter that has been arranged
	 */
	private Deck arrangeHand(Deck newTempDeck){
		//create a copy of the Deck parameter
		Deck tempDeck = new Deck(newTempDeck);
		
		//create a numeric representation of the AI's hand
		int[][] numericHand = new int[13][4];
		for(Card c: tempDeck.getCards()){
			if(c != null){
				//get the numeric representation of the rank and suit
				int suit = c.getSuit().ordinal();
				int rank = c.getRank().ordinal();

				//add one to the index of the array that matches the card's rank and suit to represent that card
				numericHand[rank][suit]++;
			}
		}
		
		//create an ArrayList of ArrayLists of Cards to represent all the runs in the player's hand
		ArrayList<ArrayList<Card>> runs = new ArrayList<ArrayList<Card>>();

		for (int suit = 0; suit < 4; ++suit){
			ArrayList<Card> tempRun = new ArrayList<Card>();

			//this loop represents a single row in the table
			for (int rank = 0; rank < 13; ++rank){
				//if there is a card in this slot
				if (numericHand[rank][suit] > 0) {
					//generate a new card based on the rank and suit
					Card tempCard = new Card(Rank.values()[rank], Suit.values()[suit]);
					tempRun.add(tempCard);
					//if the element is the last in the row, check if a valid run
					if(rank == 12){
						if(tempRun != null){
							if (tempRun.size() > 2){
								//remove the cards from the numeric representation of the hand
								for(Card c: tempRun){
									numericHand[c.getRank().ordinal()][c.getSuit().ordinal()]--;
								}
								//Add run the the list of runs
								ArrayList<Card> tempRun2 = (ArrayList<Card>) tempRun.clone();
								runs.add(tempRun2);
							}
						}
					}
				}
				//it finds an empty entry
				else{
					//it is a valid run if there are 3+ cards
					if(tempRun != null){
						if (tempRun.size() > 2){
							//remove the cards from the numeric representation of the hand
							for(Card c: tempRun){
								numericHand[c.getRank().ordinal()][c.getSuit().ordinal()]--;
							}
							//Add run the the list of runs
							ArrayList<Card> tempRun2 = (ArrayList<Card>) tempRun.clone();
							Deck tempRun2Deck = new Deck(tempRun2);
							
							runs.add(tempRun2);
						}
					}
					//clear the tempRun for the next possible run
					tempRun.clear();
				}
			}
		}
		
		//create an ArrayList of ArrayLists of Cards to represent all the sets in the player's hand
		ArrayList<ArrayList<Card>> sets = new ArrayList<ArrayList<Card>>();

		for (int rank = 0; rank < 13; ++rank){
			ArrayList<Card> tempSet = new ArrayList<Card>();
			
			//this loop represents a single column in the table
			for (int suit = 0; suit < 4; ++suit){
				//if there is a card in this slot
				if (numericHand[rank][suit] > 0) {
					//generate a new card based on the rank and suit
					Card tempCard = new Card(Rank.values()[rank], Suit.values()[suit]);
					tempSet.add(tempCard);
					if(suit == 3){
						if(tempSet != null){
							if (tempSet.size() > 2) {
								//remove the cards from the numeric representation of the hand
								for(Card c: tempSet){
									numericHand[c.getRank().ordinal()][c.getSuit().ordinal()]--;
								}
								//Add set to the the list of sets
								ArrayList<Card> tempSet2 = (ArrayList<Card>) tempSet.clone();

								sets.add(tempSet2);
							}
						}
					}
				}
			}
		}
		
		//create an ArrayList of ArrayLists of Cards to represent all the partial runs in the player's hand
		ArrayList<ArrayList<Card>> partialRuns = new ArrayList<ArrayList<Card>>();

		for (int suit = 0; suit < 4; ++suit){
			ArrayList<Card> tempRun = new ArrayList<Card>();

			//this loop represents a single row in the table
			for (int rank = 0; rank < 13; ++rank){
				//if there is a card in this slot
				if (numericHand[rank][suit] > 0){
					//generate a new card based on the rank and suit
					Card tempCard = new Card(Rank.values()[rank], Suit.values()[suit]);
					tempRun.add(tempCard);
					if(rank == 12){
						if(tempRun != null){
							if (tempRun.size() == 2){
								//remove the cards from the numeric representation of the hand
								for(Card c: tempRun){
									numericHand[c.getRank().ordinal()][c.getSuit().ordinal()]--;
								}
								//Add run the the list of runs
								ArrayList<Card> tempRun2 = (ArrayList<Card>) tempRun.clone();
								partialRuns.add(tempRun2);
							}	
						}
					}
				}
				//it finds an empty entry
				else{
					//it is a partial run because there are less than 3 cards
					if(tempRun != null){
						if (tempRun.size() == 2){
							//remove the cards from the numeric representation of the hand
							for(Card c: tempRun){
								numericHand[c.getRank().ordinal()][c.getSuit().ordinal()]--;
							}
							//Add run the the list of runs
							ArrayList<Card> tempRun2 = (ArrayList<Card>) tempRun.clone();
							partialRuns.add(tempRun2);
							
						}
					}
					tempRun.clear();
				}
			}
		}

		//create an ArrayList of ArrayLists of Cards to represent all the partial sets in the player's hand
		ArrayList<ArrayList<Card>> partialSets = new ArrayList<ArrayList<Card>>();

		for (int rank = 0; rank < 13; ++rank){
			ArrayList<Card> tempSet = new ArrayList<Card>();

			//this loop represents a single column in the table
			for (int suit = 0; suit < 4; ++suit){
				//if there is a card in this slot
				if (numericHand[rank][suit] > 0) {
					//generate a new card based on the rank and suit
					Card tempCard = new Card(Rank.values()[rank], Suit.values()[suit]);
					tempSet.add(tempCard);
					if(suit == 3){
						if(tempSet != null){
							if (tempSet.size() == 2) {
								//remove the cards from the numeric representation of the hand
								for(Card c: tempSet){
									numericHand[c.getRank().ordinal()][c.getSuit().ordinal()]--;
								}
								//Add set to the the list of sets
								ArrayList<Card> tempSet2 = (ArrayList<Card>) tempSet.clone();

								partialSets.add(tempSet2);
							}
						}
					}
				}
			}
		}
		
		//empty out the temporary deck
		while(!tempDeck.deckIsEmpty()){
			tempDeck.removeTopCard();
		}
		priorityNums.clear();
		//add all the full runs to the parameter deck
		Deck deckToMove = new Deck();
		for(ArrayList<Card> r: runs){
			Deck d = new Deck();
			for(Card c: r){
				d.add(c);
			}
			for(int i = 0; i < d.size(); i++){
				priorityNums.add(0);
			}
			
			d.moveAllCardsTo(deckToMove);
		}
		deckToMove.moveAllCardsTo(tempDeck);
		//add all full sets to the parameter deck
		for(ArrayList<Card> s: sets){
			Deck d = new Deck();
			for(Card c: s){
				d.add(c);
			}
			for(int i = 0; i < d.size(); i++){
				priorityNums.add(0);
			}
			
			d.moveAllCardsTo(deckToMove);
		}
		deckToMove.moveAllCardsTo(tempDeck);
		//add all partial runs to the parameter deck
		for(ArrayList<Card> pr: partialRuns){
			Deck d = new Deck();
			for(Card c: pr){
				d.add(c);
			}
			for(int i = 0; i < d.size(); i++){
				priorityNums.add(1);
			}
			
			d.moveAllCardsTo(deckToMove);
		}
		deckToMove.moveAllCardsTo(tempDeck);
		//add all partial sets to the parameter deck
		for(ArrayList<Card> ps: partialSets){
			Deck d = new Deck();
			for(Card c: ps){
				d.add(c);
			}
			for(int i = 0; i < d.size(); i++){
				priorityNums.add(1);
			}
		
			d.moveAllCardsTo(deckToMove);
		}
		deckToMove.moveAllCardsTo(tempDeck);
		//add all remaining cards to the parameter deck
		for(int rank = 0; rank < 13; rank++){
			for(int suit = 0; suit < 4; suit++){
				for(int i = 0; i < numericHand[rank][suit]; i++){
					Card c = new Card(Rank.values()[rank], Suit.values()[suit]);
					deckToMove.add(c);
					priorityNums.add(2);
				}
			}
		}

		deckToMove.moveAllCardsTo(tempDeck);

		//return the tempDeck
		return tempDeck;
	}

	/**
	 * Based on the runs and sets the AI can make from it's hand, it chooses a card to discard
	 * from the highest priority number in it's hand
	 * 
	 * @return The index of the card that is to be discarded
	 */
	private int cardToDiscard(){
		Deck tempDeck = new Deck(this.getGameState().getPlayersHand(this.getPlayerNum()));

		for(int i = tempDeck.size()-1; i >=0; i--){
			if(tempDeck.getCardAtIndex(i) == null){
				tempDeck.removeCardAtIndex(i);
			}
			else{
				break;
			}
		}
		
		//find the first and last index of the cards with a priority number of 2
		int startOfTwos = priorityNums.indexOf(2);
		int endOfTwos = priorityNums.lastIndexOf(2);

		//find the first and last index of the cards with a priority number of 1
		int startOfOnes = priorityNums.indexOf(1);
		int endOfOnes = priorityNums.lastIndexOf(1);
		
		//if there are no partial runs and sets and only one card not in a run or set, then the AI can go out
		if((startOfOnes == -1) && (startOfTwos == endOfTwos)){
			canGoOut = true;
		}

		//If there are cards not in a run or set, then randomly choose one of those cards to discard
		if(startOfTwos != -1){
			int discardIdx = (int)((endOfTwos+1)*Math.random());

			while(discardIdx < startOfTwos || discardIdx > endOfTwos || discardIdx > tempDeck.size()-1){
				discardIdx = (int)((endOfTwos+1)*Math.random());

				while (this.getGameState().getPlayersHand(this.getPlayerNum())
					.getCardAtIndex(discardIdx) == null)
				{
					discardIdx = (int)((endOfOnes+1)*Math.random());
				}
			}
			return discardIdx;
		}

		//If there are no cards not in a complete or partial run or set, than randomly choose a card to discard from a partial set
		else{
			if(startOfOnes != -1){
				int discardIdx = (int)((endOfOnes+1)*Math.random());
				while(discardIdx < startOfOnes || discardIdx > endOfOnes || discardIdx > tempDeck.size()-1){
					discardIdx = (int)((endOfOnes+1)*Math.random());

					while (this.getGameState().getPlayersHand(this.getPlayerNum())
							.getCardAtIndex(discardIdx) == null)
					{
						discardIdx = (int)((endOfOnes+1)*Math.random());
					}
				}

				return discardIdx;
			}
			//if there are only cards in full sets, then return -1
			else{
				return -1;
			}
		}
	}

	/**
	 * This method calls the cardToDiscard method and then sends a discard action
	 *
	 */
	private void discard(){
		//have the AI wait 1.5 seconds before moving
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int discardCard = this.cardToDiscard();

		//The AI attempts to go out if it can, otherwise it just normally discards
		if(this.canGoOut){
			game.sendAction(new TTDiscardAction(this, discardCard, true));
		}

		//If the AI cannot go out, just discard the card
		else{
			game.sendAction(new TTDiscardAction(this, discardCard, false));
		}
	}

	@Override
	/**
	 * Overrides TTComputerPlayer's receiveInfo method.  The AI randomly draws a card, rearranges it's hand into runs and sets,
	 * then chooses a card to discard
	 *
	 */
	protected void receiveInfo(GameInfo info) {
		super.receiveInfo(info);

		//check if it is the player's turn
		if((this.getGameState().getCurrentPlayer()) == this.playerNum){
			
			if(isEqual == false){
				idealDeck = new Deck(arrangeHand(this.getGameState().getPlayersHand(this.getPlayerNum())));
			}

			if(this.getGameState().getCanDraw() == true){
				//have the AI wait 1.5 seconds before moving
				try {
					Thread.sleep(1500);
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
				if(this.getGameState().getTopDiscardCard() == null){
					fromDiscard = false;
				}
				//send the TTDrawAction to the Game
				game.sendAction(new TTDrawAction(this, fromDiscard));
				
				isEqual = false;
			}
			else if(idealDeck != null){
				Deck currentHand = this.getGameState().getPlayersHand(this.getPlayerNum());

				int idxOfCardToSwap;
				int numOfCards = this.getGameState().getRoundNumber() + 3;

				for(int i = 0; i < numOfCards; i++){
					if((idealDeck.getCardAtIndex(i) != null) && (currentHand.getCardAtIndex(i) != null) 
							&&(!(idealDeck.getCardAtIndex(i).equals(currentHand.getCardAtIndex(i))))){
						for(int j = 0; j < currentHand.size(); j++){
							if(currentHand.getCardAtIndex(j) != null){
								if(currentHand.getCardAtIndex(j).equals(idealDeck.getCardAtIndex(i))){
									idxOfCardToSwap = j;
									game.sendAction(new TTRearrangeAction(this, i, idxOfCardToSwap, true));
									return;
								}
							}
						}
					}
				}
				isEqual = true;
				//if the cards of the AI's Deck are not in the same positions as the idealDeck's cards, set isEqual to false
				for(int i = 0; i < numOfCards; i++){
					if(!(idealDeck.getCardAtIndex(i).equals(currentHand.getCardAtIndex(i)))){
						isEqual = false;
					}
				}
				//if the two decks are equal, discard a card
				if(isEqual == true){
					idealDeck = null;

					this.discard();
					return;
				}
			}
		}
	}
}