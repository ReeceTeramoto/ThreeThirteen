/**
 * 
 */
package edu.up.cs301.threethirteen;

import android.graphics.Color;
import android.util.Log;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.infoMsg.GameState;
import edu.up.cs301.threethirteen.Deck;

/**
 * Contains the state of a Three-Thirteen game. Sent by game 
 * when a player wants to inquire about the state of the game. (E.g., to display
 * it or to help figure out its next move.)
 * 
 * @author Alondra Audelo-Avendano
 * @version November 2014
 * @version December 2014
 * 
 * update by CC 11/13/14
 * update by CC 11/26 (added goOutMessage code)
 *
 */
public class TTState extends GameState {

	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////
	//************instance variables**************//
	////////////////////////////////////////////////

	private Deck[] everyPlayersHand;           // Holds all of the players' hands.

	private Deck discardPile;                  // The discard pile.

	private Deck drawPile;                     // The draw pile.

	private Card topDiscardCard;               // The card that is at the top of the discard pile.

	private int currentPlayer;                 // The number index number of the currentPlayer.

	private int roundNumber;                   // The number of the current round.

	private boolean canDraw;                   // Set to true only if currentPlayer is allows to draw a card.

	private boolean endOfRound;                // Returns true if it is time to calculate the scores.

	private String messageToPlayer;            // The message that will be sent to currentPlayer.

	public static final int MIN_PLAYERS = 2;   // Minimum number of players.

	public static final int MAX_PLAYERS = 2;   // Maximum number of players.

	private int numPlayers;                    // The current number of players.

	private String [] namesOfPlayers;           //The names of the players

	private int[] scoresOfPlayers;             // The scores of all the players.

	private boolean canDiscard;                // Boolean only set to true if currentPlayer is allowed to discard a card.

	private boolean canGoOut;                  // Boolean only set to true if curentPLayer is allowed to go out.    

	private boolean someoneHasGoneOut;		   // Returns true if somebody has gone out.

	private String goOutMessage;			   // Holds the message that will be displayed if someone
											   // goes out



	/**
	 *  Default constructor for objects of class TTState. Initializes for the beginning of the
	 *  game, with a random player as the currentPlayer.
	 */
	public TTState() {
		// Initialize numPLayers
		numPlayers = 2;

		// Create new decks for each of the player's hand, the draw pile, and the discard pile.
		everyPlayersHand = new Deck[numPlayers];

		discardPile = new Deck();

		drawPile = new Deck();

		// Initialize the currentPlayer to the firstPlayer.
		currentPlayer = 0;

		// Initialize the roundNumber to the first round.
		roundNumber = 1;

		// Initialize canDraw to true.
		canDraw = true;

		// Initialize endOfRound to false -- it's the beginning of the game!
		endOfRound = false;

		// Initialize the messageToPlayer.
		messageToPlayer = "No message";

		// Initialize the scoresOfPlayers to be the length of the total number of players.
		scoresOfPlayers = new int[numPlayers];

		// Initialize canDiscard, canGoOut, and someoneHasGoneOut to false -- the game has just begun!
		canDiscard = false;

		canGoOut = false;

		someoneHasGoneOut = false;

		// Randomly select the player to start
		currentPlayer = (int)(2*Math.random());

		// Initialize the goOutMessage
		goOutMessage = "ROUND 1!";
	}


	public TTState(int numberOfPlayers) {
		// Initialize numPLayers
		numPlayers = numberOfPlayers;

		// Create new decks for each of the player's hand, the draw pile, and the discard pile.
		everyPlayersHand = new Deck[numberOfPlayers];

		discardPile = new Deck();

		drawPile = new Deck();

		// Initialize the roundNumber to the first round.
		roundNumber = 1;

		// Initialize canDraw to true.
		canDraw = true;

		// Initialize endOfRound to false -- it's the beginning of the game!
		endOfRound = false;

		// Initialize the messageToPlayer.
		messageToPlayer = "No message";

		// Initialize the scoresOfPlayers to be the length of the total number of players.
		scoresOfPlayers = new int[numPlayers];

		// Initialize canDiscard, canGoOut, and someOneHasGoneOut to false -- the game has just begun!
		canDiscard = false;

		canGoOut = false;

		someoneHasGoneOut = false;

		// Randomly select the player to start.
		currentPlayer = (int)(numberOfPlayers*Math.random());

		// Initialize the goOutMessage.
		goOutMessage = "ROUND 1!";

		// Initialize the namesOfPlayers array.
		namesOfPlayers = new String [numberOfPlayers];
	}

	/**
	 * Copy constructor for TTState
	 * @param orig the "original" state of the game.
	 */
	public TTState(TTState orig) {
		// Set discardPile and drawPile to be exactly the same as the ones found in 
		// the parameter state.
		discardPile = new Deck(orig.discardPile);

		drawPile = new Deck(orig.discardPile);

		// Set the topDiscardCard to the one found orig.
		topDiscardCard = orig.topDiscardCard;

		// Set roundNumber to orig's roundNumber.
		roundNumber = orig.roundNumber;

		// Initialize canDraw.
		canDraw = orig.canDraw;

		// Initialize endOfRound.
		endOfRound = orig.endOfRound;

		// Initialize the currentPlayer to  the currentPlayer of orig.
		currentPlayer = orig.currentPlayer;

		// Copy the messageToPlayer in orig into this messageToPlayer.
		messageToPlayer = new String(orig.messageToPlayer);

		// Initialize numPLayers
		numPlayers = orig.numPlayers;

		// Initialize the scoresOfPlayers to that found in the parameter state.
		scoresOfPlayers = orig.scoresOfPlayers.clone();

		// Initialize canDiscard, canGoOut, and someOneHasGoneOut appropriately.
		canDiscard = orig.canDiscard;

		canGoOut = orig.canGoOut;

		someoneHasGoneOut = orig.someoneHasGoneOut;

		// Initialize everyPlayersHand to that of orig.
		everyPlayersHand = new Deck[orig.getNumPlayers()];

		for (int i = 0; i < everyPlayersHand.length; ++i) {
			everyPlayersHand[i] = new Deck(orig.everyPlayersHand[i]);
		}

		// Initialize the goOutMessage
		goOutMessage = orig.goOutMessage;

		// Initialize the namesOfPlayers array
		namesOfPlayers = orig.namesOfPlayers.clone();

	}

	/**
	 * Getter method for currentPlayer's hand.
	 * @param playerId currentPlayer's index within everyPlayersHand.
	 */
	public Deck getPlayersHand(int playerId) {
		// Make sure everyPlayersHand is not null
		if(everyPlayersHand != null) {
			return everyPlayersHand[playerId];

		}
		else {
			return null; // If it is, then just return null -- don't try to access a null array.
		}
	}

	/**
	 * Setter method for currentPlayer's hand.
	 * @param playersId currentPlayer's index within everyPlayersHand.
	 * @param newHand Deck that will form currentPlayer's hand.
	 */
	public void setPlayersHand(int playersId, Deck newHand) {
		everyPlayersHand[playersId] = newHand;
	}


	/**
	 * Getter method for the entire discard pile deck.
	 * @return discardPile
	 */
	public Deck getDiscardPile() {
		return discardPile;
	}

	/**
	 * Setter method for the entire discard pile.
	 * @param discardPileCards the cards that will be form the discard pile.
	 */
	public void setDiscardPile(Deck newDiscardPile) {
		discardPile = newDiscardPile;
	}

	/**
	 * Getter method the card at the top of the discard pile.
	 * @return topDiscardCard
	 */
	public Card getTopDiscardCard() {
		// Make sure the discardPile is not null
		if(discardPile != null) {
			return discardPile.peekAtTopCard();
		}
		else {
			return null;
		}
	}

	/**
	 * Setter method for the card at the top of the discard pile.
	 * @param topDiscardCard the card we will set topDiscardCard to.
	 */
	public void setTopDiscardCard(Card newTopDiscardCard) {
		topDiscardCard = newTopDiscardCard;
	}

	/**
	 * Getter method for the entire draw pile.
	 * @return drawPile
	 */
	public Deck getDrawPile() {
		return drawPile;
	}

	/**
	 *  Setter method for the entire draw pile.
	 * @param drawPileCards the cards that will be form the draw pile.
	 */
	public void setDrawPile(Deck newDrawPile) {
		drawPile = newDrawPile;
	}

	/**
	 * Getter method for currentPlayer's current number.
	 * @return currentPlayer
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Setter method for currentPlayer.
	 * @param newCurrentPlayer new int value of currentPlayer.
	 */
	public void setCurrentPlayer(int newCurrentPlayer) {
		currentPlayer = newCurrentPlayer;
	}

	/**
	 * Getter method for namesOfPlayers
	 * @return namesOfPlayers
	 */
	public String[] getNamesOfPlayers() {
		return namesOfPlayers;
	}

	/**
	 * Setter method for namesOfPlayers
	 * @param newNamesOfPLayers new String array of the players' names.
	 */
	public void setNamesOfPlayers(String[] newNamesOfPlayers) {
		this.namesOfPlayers = newNamesOfPlayers;
	}
	/**
	 * Getter method for the current roundNumber.
	 * @return roundNumber
	 */
	public int getRoundNumber() {
		return roundNumber;
	}

	/**
	 * Setter method for roundNumber.
	 * @param newRoundNumber new int value of roundNumber.
	 */
	public void setRoundNumber(int newRoundNumber) {
		roundNumber = newRoundNumber;
	}

	/**
	 * Getter method for boolean variable canDraw.
	 * @return canDraw
	 */
	public boolean getCanDraw() {
		return canDraw;
	}

	/**
	 * Setter method for canDraw.
	 * @param canDraw new boolean value of canDraw
	 */
	public void setCanDraw(boolean newCanDraw) {
		canDraw = newCanDraw;
	}

	/**
	 * Getter method for boolean variable endOfRound.
	 * @return endOfRound
	 */
	public boolean getEndOfRound() {
		return endOfRound;
	}

	/**
	 * Setter method for endOfRound.
	 * @param newEndOfRound new value of endOfRound
	 */
	public void setEndOfRound(boolean newEndOfRound) {
		endOfRound = newEndOfRound;
	}

	/**
	 * Getter method for messageToPlayer.
	 * @return messageToPlayer
	 */
	public String getMessage() {
		return messageToPlayer;
	}

	/**
	 * Setter method for messageToPlayer.
	 * @param newMessage the new String message that will be sent to the player.
	 */
	public void setMessage(String newMessage) {
		messageToPlayer = newMessage;
	}

	/**
	 * Getter for goOutMessage
	 * @return goOutMessage
	 */
	public String getGoOutMessage() {
		return goOutMessage;
	}


	/**
	 * Setter method for goOutMessage
	 * @param message the new message
	 */
	public void setGoOutMessage(String message) {
		// If the message is not null, then continue to set the new message.
		if (!message.equals("")) {
			this.goOutMessage = message;
		}
	}


	/**
	 * Getter method for a particular item in scoresOfPlayers.
	 * @param playerId currentPlayer's index value within this array.
	 * @return scoresOfPlayers[playerId]
	 */
	public int getPlayersScore(int playerId) {
		return scoresOfPlayers[playerId];
	}

	/**
	 * Setter method for scoresOfPlayers.
	 * @param playerId currentPlayer's index value within this array.
	 * @param newScore new score for currentPlayer.
	 */
	public void setPlayersScore(int playerId, int newScore) {
		scoresOfPlayers[playerId] = newScore;
	}

	/**
	 * Getter method for canDiscard.
	 * @return canDiscard
	 */
	public boolean getCanDiscard() {
		return canDiscard;
	}

	/**
	 * Setter method for canDiscard.
	 * @param player player requesting to discard a card. // PARAMETER REMOVED 11.9.2014 //
	 * @param canDiscard
	 */
	public void setCanDiscard(boolean newCanDiscard) {
		canDiscard = newCanDiscard;
	}

	/**
	 * Getter method for deck array, everyPlayersHand.
	 * @return everyPlayersHand
	 */
	public Deck[] getEveryPlayersHand () {
		return everyPlayersHand;
	}

	public void setSomeoneHasGoneOut(boolean newSomeoneHasGoneOut) {
		someoneHasGoneOut = newSomeoneHasGoneOut;
	}

	/**
	 * Getter method for boolean variable, someoneHasGoneOut.
	 * @return someoneHasGoneOut
	 */
	public boolean getSomeoneHasGoneOut() {
		return someoneHasGoneOut;
	}

	/**
	 * Getter method for numPLayers
	 * @return numPlayers
	 */
	public int getNumPlayers() {
		return numPlayers;
	}

	/**
	 * Setter method for numPlayers.
	 * @return numPlayers
	 */
	public void setNumPlayers(int newNumPlayers){
		numPlayers = newNumPlayers;
	}

	/**
	 * Method indicates that the currentPlayer has picked up/drawn a card.
	 * @return true 
	 */
	public boolean hasPickedUpCard() {
		return true;
	}

	/**
	 * Method determines if currentPlayer's requested move is valid or not.
	 * @param action the action being requested by the player.
	 * @return true if the move is valid; false otherwise.
	 */
	public boolean isValidMove(TTMoveAction action) {
		// If the action is an instance of any of our legal and recognized moveActions
		// then return true 
		if (action instanceof TTDiscardAction) {
			return canDraw(); // Or continue to analyze the action further to determine if it's legal.
		}
		else if (action instanceof TTDrawAction) {
			return canDiscard();
		}
		else if (action instanceof TTRearrangeAction) {
			return true;
		}
		// Otherwise, return false.
		else {
			return false;
		}
	}

	/**
	 * Method determines if currentPlayer can or cannot draw a card.
	 * @return canDraw (which would be previously initialized to 
	 * 				    either true or false appropriately).
	 */
	public boolean canDraw() {
		return canDraw;
	}

	/**
	 * Method determines if it is player's turn.
	 * @param player the player requesting the move.
	 * @return true if it is player's turn; false otherwise.
	 */
	public boolean isPlayersTurn(int player) {
		// If player is the current player, then return true.
		if (player == currentPlayer) {
			return true;
		}
		// Otherwise return false.
		return false;
	}

	/**
	 * Method determines if player can go out.
	 * @return canDiscard (which would have been previously initialized to 
	 * 					   either true or false appropriately).
	 */
	public boolean canDiscard() {
		return canDiscard;
	}

	/**
	 * Method determines is player can go out.
	 * @param player player requesting to go out.
	 * @return canGoOut
	 */
	public boolean canGoOut() {
		return canGoOut;
	}

	/**
	 * Method determines if the given cards form a run.
	 * @param cards The cards that need to be checked.
	 * @return this method always returns false.
	 */
	public boolean isARun(Deck cards) {
		return false;
	}

	/**
	 * Method determines if the given cards form a set.
	 * @param cards
	 * @return this method always returns false.
	 */
	public boolean isASet(Deck cards) {
		return false;
	}

	/**
	 * Setter method for canGoOut
	 * @param newCanGoOut the new boolean value of canGoOut.
	 */
	public void setCanGoOut(boolean newCanGoOut) {
		canGoOut = newCanGoOut;
	}

	/**
	 * Getter method for the scores of the players array
	 * @return scoresOfPlayers
	 */
	public int[] getScoresOfPlayers() {
		return scoresOfPlayers;
	}

	/**
	 * This method returns the scores of a two player game
	 * held in the scoresOfPlayers array in the form of a String.
	 * @return the scores of the players as a String.
	 */
	public String toString() {
		return "P1 score: " + scoresOfPlayers[0] + " P2 score: " + scoresOfPlayers[1];
	}
}
