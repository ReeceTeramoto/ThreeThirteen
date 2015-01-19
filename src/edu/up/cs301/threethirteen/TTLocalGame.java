package edu.up.cs301.threethirteen;

import java.util.ArrayList;
import java.util.Hashtable;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.threethirteen.Deck;

/**
 * Class TTLocalGame
 * 
 * Keeps the state of the 3-13 "game table" and enforces the rules
 * @author Reece Teramoto
 * @version 5 December 2014 11:05 PM
 *
 *update: CC 11/26 (discardCard)
 *update: CC 11/29 (makeMove--rearrangeAction)
 */

public class TTLocalGame extends LocalGame implements TTGame{

	private TTState masterGameState; //the official state for the entire game
	private Hashtable messagesToPlayers = new Hashtable<Integer, String>(4); //messages to each player
	private int numPlayers; //number of players in the game
	private boolean someoneHasGoneOut = false; //set to true once someone successfully "goes out"
	
	//initialized when someone goes out; says how many turns left until the new round starts
	private int turnCountdown; 
	
	private int playerWhoHasGoneOut; //the index of the player who has "gone out"
	
	public TTLocalGame() //default constructor 
	{
		Log.i("TTLocalGame", "creating game with default constructor (you should not see this log)");
        //this should never be called
	}
	
	public void start(GamePlayer[] myPlayers) 
	{
		super.start(myPlayers); 
		
		Log.i("Start", "Got to the start method, called super.");
		
		//create a new master game state with the given number of players
		masterGameState = new TTState(myPlayers.length); 
		
		//set the namesOfPlayers array in the state
		masterGameState.setNamesOfPlayers(this.playerNames);
		
		//DEAL THE CARDS HERE
		// 1 means round 1
		dealCards(myPlayers, 1);
		
		//log messages
		for (int i = 0; i < myPlayers.length; ++i)
		{
			if (masterGameState.getPlayersHand(i) == null)
				Log.i("TTLocalGame", "Player " + i + "'s hand is null");
			else
				Log.i("TTLocalGame", "Player " + i + "'s hand is NOT null");
		}
	}
	
	/*
	 * deals cards to each player for the round
	 * @players
	 * @roundNum
	 */
	private void dealCards(GamePlayer[] players, int roundNum) {
		//the number of cards in a player's hand will always be 2 more than the 
		//round number
		int numCards = roundNum + 2;
		
		//get the official game deck
		Deck officialDeck = new Deck();
		officialDeck.add52();
		//use 2 decks if there are 3 to 4 players
		if (masterGameState.getNumPlayers() > 2)
		{
			Log.i("TTLocalGame - dealCards", "Using 2 decks for this game "
					+ "since there are " + masterGameState.getNumPlayers()
					+ " players.");
			officialDeck.add52();
		}
		officialDeck.shuffle(); //officialDeck is now a shuffled, full deck
		
		//index of the player in the players array
		int indexOfPlayer = 0;
		
		//iterate through the game players
		for (GamePlayer p : players)
		{
			Deck newHand = new Deck(); //new player's hand
			//add cards to the player's hand
			for (int i = 0; i < numCards; ++i)
			{
				officialDeck.moveTopCardTo(newHand);
			}
			
			//add blank spaces to the player's hand
			int numBlankSpacesToAdd = 16 - roundNum;
			for (int i = 0; i < numBlankSpacesToAdd; ++i)
			{
				newHand.add(null);
			}
			
			//modify every player's hand in the master game state
			masterGameState.setPlayersHand(indexOfPlayer, newHand);
			indexOfPlayer++;
		}
		
		//the remaining cards in the officialDeck become the draw pile
		masterGameState.setDrawPile(officialDeck);
		
		//reset the discard pile
		Deck newDiscardPile = new Deck();
		masterGameState.setDiscardPile(newDiscardPile);
		
		Log.i("LocalGame", "The official deck has " + officialDeck.getCards().size()
				+ " cards.");
	}


	/*
	 * ++currentPlayer after each turn
	 * if currentPlayer = numPlayers, then currentPlayer = 0.
	 * 
	 */
	private final void updateCurrentPlayer() {
		//number of players in the game
		int numPlayers = masterGameState.getNumPlayers();
		int currentPlayer = masterGameState.getCurrentPlayer();
		
		//set the current player back to 0 if it is already at the last player
		if (currentPlayer == numPlayers - 1){
			masterGameState.setCurrentPlayer(0); 
		}
		//if not, increment the current player by 1
		else{
			masterGameState.setCurrentPlayer(++currentPlayer);
		}
		
		//new player is allowed to draw a card
		masterGameState.setCanDraw(true);
		
		Log.i("updateCurrentPlayer", "Updated current player. Current player "
				+ "is now player " + currentPlayer);
	}

	/*
	 * if the player isGoingOut, then checkForWin
	 * (if going out) check the player's hand and see if they can actually go out.  
	 * If successful, discard the discardCard and announce to the other players 
	 * that the player is going out.  If unsuccessful, show an error message 
	 * and user is allowed to make another discard move.
	 * else just discard the card
	 * calls the canDiscard method
	 * 
	 * @player
	 * @discardCard
	 * @isGoingOut
	 */

	private final boolean discardCard(int player, int discardCard, boolean isGoingOut) {
		int currentPlayer = player; //current player index
		
		Deck currentPlayersHand = masterGameState.getPlayersHand(currentPlayer);
		Deck discardPile = masterGameState.getDiscardPile();
		
		//if the player isGoingOut, then checkForWin
		if (isGoingOut)
		{
			Deck[] everyPlayersHand = masterGameState.getEveryPlayersHand();
			Deck playersHand = everyPlayersHand[currentPlayer];
			boolean playerWins = checkForWin(playersHand, discardCard);
			
			if (playerWins) //the player can, indeed, go out
			{
				//discard the selected card
				transferCard(currentPlayersHand, discardCard, discardPile);
				//update master game state
				masterGameState.setPlayersHand(currentPlayer, currentPlayersHand);
				masterGameState.setDiscardPile(discardPile);
				
				//someone has gone out
				someoneHasGoneOut = true;
				masterGameState.setSomeoneHasGoneOut(true);
				//each player (excluding the current player) has 1 turn left
				turnCountdown = masterGameState.getNumPlayers() - 1; 
				
				Log.i("discardCard", "initiated turnCountdown");
				
				//the "go out" message
				String message;
				String[] playerNames = this.playerNames;
				String playerGoingOut = playerNames[currentPlayer];
				
				//create the string of the players' hand who is going out
				if(masterGameState.getRoundNumber() + 1 <= 11) 
				{
					ArrayList<Card> displayedHand = (ArrayList<Card>) currentPlayersHand.getCards().clone();
					//iterate backwards through the array
					for (int lastIndex = displayedHand.size()-1; lastIndex >= 0;
							--lastIndex)
					{
						if (displayedHand.get(lastIndex) == null)
						{
							//remove all excess null cards
							displayedHand.remove(lastIndex);
						}
						else //break once it finds a non-null entry
						{
							break;
						}
					}
					Deck displayedDeck = new Deck(displayedHand);
					
					//announce to the other players that the player is going out
					message = playerGoingOut + " has gone out with: " + displayedDeck.toString()
									+ "! Round " + (masterGameState.getRoundNumber() + 1) 
									+ " here we come!";
				}
				else
				{
					//the next round number is greater than 11 and the game is about to end
					//announce to the other players that the player is going out
					message = playerGoingOut + " is going out with: " + currentPlayersHand.toString()
									+ "! The game is about to end!";
				}
				
				
				//set the goOutMessage in the masterGameState
				masterGameState.setGoOutMessage(message);
				
				Log.i("discardCard", "Player has successfully gone out.");
				return true;
			}
			//if the player doesn't actually win
			//show an error message 
			//and user is allowed to make another discard move.
			else
			{
				messagesToPlayers.put(currentPlayer, 
						"You cannot go out with this hand.");
				return false;
			}
		}
		else //the player is not trying to go out, so just discard the card
		{
			//discard the selected card
			transferCard(currentPlayersHand, discardCard, discardPile);
			currentPlayersHand.add(null);
			//update master game state
			masterGameState.setPlayersHand(currentPlayer, currentPlayersHand);
			masterGameState.setDiscardPile(discardPile);
			return true;
		}
	}

	/*
	 * Called if the draw pile is empty; reshuffles the 
	 * discard pile and sets it to the draw pile 
	 */
	private final void resetDrawPile()
	{
		//the new draw pile will be the old discard pile
		Deck newDrawPile = new Deck(masterGameState.getDiscardPile());
		Log.i("resetDrawPile", "copied discard pile to new deck");
		
		//shuffle it
		newDrawPile.shuffle();
		Log.i("resetDrawPile", "shuffled new deck");
		
		//set the new draw pile
		masterGameState.setDrawPile(newDrawPile);
		Log.i("resetDrawPile", "set new deck to drawPile");
		
		//reset the discard pile to an empty deck
		Deck newDiscardPile = new Deck();
		masterGameState.setDiscardPile(newDiscardPile);
		Log.i("resetDrawPile", "reset discardPile to empty deck");
	}
	
	/*
	 * exemptCard = the card to be discarded
	 * make sure to only call this if the player isGoingOut
	 * @hand
	 * @exemptCardIdx
	 */
	private final boolean checkForWin(Deck hand, int exemptCardIdx) {
		//make a copy of the parameter hand
		Deck playersHand = new Deck(hand);
		Log.i("checkForWin", ""+playersHand);

		//remove the discardCard
		playersHand.removeCardAtIndex(exemptCardIdx);
		Log.i("checkForWin (without discard)", ""+playersHand);

		//Let's make an ArrayList to hold each "group" as a separate entry
		ArrayList<ArrayList<Card>> cardGroups = turnDeckIntoCardGroups(playersHand);
		
		//turn the ArrayList into an array
		ArrayList<Card>[] cardGroupArray = cardGroups.toArray(new ArrayList[0]);
		
		//go through each card group
		for (int i = 0; i < cardGroupArray.length; ++i)
		{
			//if the card group is a set or a run, set it to null
			if (isaSet(cardGroupArray[i]) || isaRun(cardGroupArray[i]))
			{
				cardGroupArray[i] = null;
			}
		}
		
		boolean playerWins = true;
		//if the whole array is null, then the player wins!
		for (int i = 0; i < cardGroupArray.length; ++i)
		{
			if (cardGroupArray[i] != null)
			{
				playerWins = false;
			}
		}
		Log.i("checkForWin returns: ", ""+playerWins);
		return playerWins;
	}
	
	/*
	 * given a deck, turn it into an array list of card groups
	 * (each card group is an array list of cards)
	 * 
	 * @param Deck d
	 * @return ArrayList<ArrayList<Card>> cardGroups
	 */
	private ArrayList<ArrayList<Card>> turnDeckIntoCardGroups(Deck d)
	{
		ArrayList<Card> cards = d.getCards();
		//We now have an array list of the cards in the player's hand.
		//Blank spaces are represented by null entries.
		
		//Let's make an ArrayList to hold each "group" as a separate entry
		ArrayList<ArrayList<Card>> cardGroups = new ArrayList<ArrayList<Card>>();

		boolean endOfGroup = false;
		ArrayList<Card> tempList = new ArrayList<Card>();
		
		for (Card c : cards) //go through all the cards
		{
			
			//make a temporary list of cards
			
			if (!endOfGroup) //if the group is not complete yet
			{
				if (c != null) //if current card is not null
				{
					tempList.add(c); //add it to the temporary list
				}
				else //if current card is null (blank space), this is the 
					//end of the group
				{
					endOfGroup = true;

					//if the list isn't empty, add it 
					if (!tempList.isEmpty()) 
					{
						ArrayList<Card> tempList2 = (ArrayList<Card>) tempList.clone();
						cardGroups.add(tempList2);
						Log.i("turnDeckIntoCardGroups", "added a card group with length "
								+ tempList2.size());
						tempList.clear();
					}
				}
			}
		}
		return cardGroups;
	}
	
	/*
	 * Checks to see if a card ArrayList is a set
	 * (all cards are of the same value)
	 * 
	 * @param ArrayList<Card> cardGroup
	 * @return true if the given array list is a set of cards
	 */
	private boolean isaSet(ArrayList<Card> cardGroup)
	{
		boolean isaSet = true;
		
		//must be 3 or more cards
		if (cardGroup.size() < 3)
		{
			isaSet = false;
		}
		
		//aces are low
		int aceVal = 1;
		//the card value that we will be testing
		int cardVal = cardGroup.get(0).getRank().value(aceVal);
		
		for (Card c : cardGroup)
		{
			//if any card does not equal the card value of the 1st card,
			//this is not a set
			if (c.getRank().value(aceVal) != cardVal)
			{
				isaSet = false;
			}
		}
		//testing 
		//isaSet = true;
		return isaSet;
	}
	
	/*
	 * 
	 * Checks to see if a card ArrayList is a run
	 * aka
	 * the absolute value of the difference between any two
	 * consecutive cards must be 1
	 * 
	 * @param ArrayList<Card> cardGroup
	 * @return true if the given array list of cards is a run
	 */
	private boolean isaRun(ArrayList<Card> cardGroup)
	{ 	
		
		//turn the cardGroup into an array
		Card[] group = cardGroup.toArray(new Card[0]);
		
		
		
		boolean isaRun = true;
		
		//must be 3 or more cards
		if (cardGroup.size() < 3)
		{
			isaRun = false;
		}
		
		//aces are low
		int aceVal = 1;
		
		String suit = group[0].getSuit().longName();
		
		for (int i = 0; i < group.length - 1; ++i)
		{
			//if the absolute value
			//of the difference between the current card and the next card
			int difference = group[i].getRank().value(aceVal) - 
							 group[i+1].getRank().value(aceVal);
			//does not equal 1, this is not a run
			if (Math.abs(difference) != 1)
			{
				isaRun = false;
			}
			
			//each card must be the same suit
			String cardSuit = group[i].getSuit().longName();
			if (!cardSuit.equals(suit))
			{
				isaRun = false;
			}
		}
		//testing 
		//isaRun = true;
		return isaRun;
	}

	/* 
	 * drawCard
	 * 
	 * draws a card from a given pile
	 * 
	 * @param fromDiscard
	 * @param playerId
	 */
	private final void drawCard(boolean fromDiscard, int playerId) {
		//get the discard pile, draw pile, and current player's hand
		Deck discardPile = masterGameState.getDiscardPile();
		Deck drawPile = masterGameState.getDrawPile();
		Deck currentPlayersHand = masterGameState.getPlayersHand(playerId);
		
		if (fromDiscard) //user wants to draw from the discard pile
		{
			transferCard(discardPile, discardPile.size()-1, currentPlayersHand);
		}
		else //user wants to draw from the draw pile
		{
			transferCard(drawPile, drawPile.size()-1, currentPlayersHand);
		}
		
		//set the draw pile, discard pile, and current player's hand
		masterGameState.setDrawPile(drawPile);
		masterGameState.setDiscardPile(discardPile);
		masterGameState.setPlayersHand(playerId, currentPlayersHand);
	}


	/*
	 * transferCard
	 * 
	 * transfers a card from one deck to another
	 * 
	 * @param deckWhereCardIsFrom
	 * @param idxOfCardToBeGiven
	 * @param whereCardIsGoing
	 */
	private final void transferCard(Deck deckWhereCardIsFrom, int idxOfCardToBeGiven, 
			Deck whereCardIsGoing) {
		//makes a copy of the card to move
		Card cardToMove = deckWhereCardIsFrom.getCardAtIndex(idxOfCardToBeGiven);
		
	
		//adds the card to the new deck
		whereCardIsGoing.addToFirstNullSpot(cardToMove);
		
		//removes the card from the original deck
		deckWhereCardIsFrom.removeCardAtIndex(idxOfCardToBeGiven);
	}

	/*
	 * isEndOfRound
	 * 
	 * Sets the isEndOfRound boolean to true in the state
	 */
	private void isEndOfRound() {
		masterGameState.setEndOfRound(true);
		computeScores();
		
		//increment the round number and set it
		int newRoundNum = masterGameState.getRoundNumber() + 1;
		masterGameState.setRoundNumber(newRoundNum);
		
		//re deal the cards if newRoundNum is not 12
		if (newRoundNum != 12)
		{
			dealCards(this.players, newRoundNum);
		}
	}

	private final void computeScores() {
		//gets each player's hand
		Deck[] everyPlayersHand = masterGameState.getEveryPlayersHand();
		
		Log.i("computeScores", "is being called");
		
		//note: every player's hand is in its final state
		
		//calculate the score for each player
		int aceVal = 1;
		
		Log.i("computeScores", "about to go through the computing scores loop");
		Log.i("computeScores", "there are " + masterGameState.getNumPlayers() + "players");
		
		//iterate through the players' scores
		for (int playerNum = 0; playerNum < masterGameState.getNumPlayers(); ++playerNum)
		{
			Log.i("computeScores", "Inside the loop now.");
			
			//get the player's hand
			Deck playersHand = new Deck(everyPlayersHand[playerNum]);
			
			Log.i("computeScores", "analyzing player " + playerNum + "'s hand");
			Log.i("computeScores", "" + playersHand);
			
			//separate the "groups" that the player made with blank spaces
			ArrayList<ArrayList<Card>> cardGroups = turnDeckIntoCardGroups(playersHand);
			
			for (ArrayList<Card> c : cardGroups) //go through each card group
			{
				if (!isaSet(c) && !isaRun(c)) //if the group is not a set or a run,
				{
					//add the rank of each card to the score
					int addToScore = 0;
					for (Card card : c)
					{
						addToScore = addToScore + card.getRank().value(aceVal);
					}
					int playersCurrentScore = masterGameState.getPlayersScore(playerNum);
					int updatedScore = playersCurrentScore + addToScore;
					masterGameState.setPlayersScore(playerNum, updatedScore);
					
					Log.i("computeScores", "Player " + playerNum + "'s score is " +
							updatedScore);
				}
			}
		}
	}


	/*
	 * makeMove
	 * 
	 * processes the GameActions that come in from various players
	 * @action
	 * @return true if a legal move is made
	 */

	public final boolean makeMove(GameAction action) {
		Log.i("makeMove", "Received an action.");
		// check that we have TTMoveAction; if so cast it
		if (!(action instanceof TTMoveAction)) {
			Log.i("makeMove", "Action is invalid.");
			return false;
		} 
		
		boolean isValidMove = false;
		//cast the action to a TTMoveAction
		TTMoveAction ttma = (TTMoveAction) action;
		//current player
		GamePlayer player = action.getPlayer();
		//current player's Id number
		int playerId = this.getPlayerIdx(player);
		
		//returns true if it is the given player's turn
		boolean itIsYourTurn = false;
		if (masterGameState.getCurrentPlayer() == playerId)
		{
			itIsYourTurn = true;
		}
		
		//discard action
		if (ttma.isDiscard() && masterGameState.getCanDiscard()
				&& itIsYourTurn) 
		{
			
			TTDiscardAction discardAction = (TTDiscardAction) ttma;
			boolean isGoingOut = discardAction.getIsGoingOut();
			int discardCardIndex = discardAction.getDiscardCardIndex();
			
			Log.i("makeMove is about to process discardAction for player " + playerId, "isGoingOut: " + isGoingOut);
			
			//current card CANNOT be null
			Card selectedCard = masterGameState.getPlayersHand(playerId)
					.getCardAtIndex(discardCardIndex);
			
			if (selectedCard != null)
			{
				//discard the current card
				
				//if someone has gone out already, you cannot be considered to be "going out"
				if (someoneHasGoneOut)
				{
					isGoingOut = false;
				}
				
				isValidMove = discardCard(playerId, discardCardIndex, isGoingOut);
				//if you are not allowed to discard, then return false
				if (!isValidMove)
				{
					return false;
				}

				//updates the turn countdown if someone has recently gone out
				if (someoneHasGoneOut)
				{	
					if (turnCountdown == 0) //end of the round
					{
						isEndOfRound();
						Log.i("makeMove", "It is the end of the round.");
						someoneHasGoneOut = false;
						masterGameState.setSomeoneHasGoneOut(false);
					}
					else //turn countdown is not zero
					{	--turnCountdown;}
					Log.i("makeMove", "Decreased turnCountdown to " + turnCountdown);
				}
				Log.i("makeMove", "Processed valid discard action, isGoingOut is "
						+ isGoingOut);
				updateCurrentPlayer();
				masterGameState.setCanDiscard(false); //current player cannot discard again
				
				Log.i("makeMove", "Player " + playerId + 
						" successfully discarded " 
						+ selectedCard.toString());
			}
			else //trying to discard a null card (illegal)
			{
				Log.i("makeMove", "Failed attempt to discard a null card.");
				return false;
			}
		}
		//draw action
		else if (ttma.isDraw() && masterGameState.getCanDraw()
				&& itIsYourTurn) 
		{
			
			Log.i("makeMove", "received a drawAction");
			
			//cast the action to a draw action
			TTDrawAction drawAction = (TTDrawAction) ttma;
			boolean fromDiscard = drawAction.getFromDiscard();
			//fromDiscard is true if the player wants to draw from discard pile
				//false if the player wants to draw from the draw pile
			
			//if you want to draw from the discard pile, it can't be empty
			if (fromDiscard && masterGameState.getDiscardPile().deckIsEmpty())
			{
				Log.i("Local Game - makeMove", "Cannot draw from an empty discard pile");
				return false; //cannot draw from an empty discard pile
				
			}
			
			//draw the card
			drawCard(fromDiscard, playerId);
			
			isValidMove = true;
			
			//player is no longer allowed to draw
			masterGameState.setCanDraw(false);
			
			//player is now allowed to discard/go out
			masterGameState.setCanDiscard(true);
			
			messagesToPlayers.put(playerId, "Choose a card to discard.");
			
			//reshuffle the discard pile if the draw pile is empty
			if (masterGameState.getDrawPile().deckIsEmpty())
			{
				Log.i("makeMove", "about to reset drawPile");
				resetDrawPile();
				Log.i("makeMove", "finished resetting drawPile");
				messagesToPlayers.put(playerId, "Choose a card to discard " +
						"(draw pile was re-shuffled just now)");
			}
		}
		//rearrange action
		else if (ttma.isRearrange()) 
		{
			Log.i("makeMove", "receiving rearrange action");
			//cast the action to a rearrange action
			TTRearrangeAction rearrangeAction = (TTRearrangeAction) ttma;
			int moveFrom = rearrangeAction.getMoveFrom();
			int moveTo = rearrangeAction.getMoveTo();
			boolean doSwap = rearrangeAction.getWantSwap();
			
			Deck playersHand = masterGameState.getPlayersHand(playerId);
			
			//if doSwap is true, swap the two cards
			if(doSwap)
			{
				playersHand.swapCards(moveFrom, moveTo);
			}
			else
			{
				//doSwap is false, so the player wants
				//the card at moveFrom to be inserted in front of the
				//card at moveTo
				playersHand.insertCard(moveFrom, moveTo);
			}
			
			//sets the player's hand
			masterGameState.setPlayersHand(playerId, playersHand);
			
			isValidMove =  true;
		}
		Log.i("makeMove", "currentPlayer is player " + masterGameState.getCurrentPlayer());
		return isValidMove;
	}

	/*
	 * sendUpdatedStateTo
	 * 
	 * send the modified copy of the state to player p
	 * 
	 * @param p
	 */

	public final void sendUpdatedStateTo(GamePlayer p) {
		//make a copy of the master game state
		TTState modifiedState = new TTState(masterGameState);
		
		Log.i("sendUpdatedState", "masterGameState, top discard card: " 
				+ masterGameState.getTopDiscardCard());
		
		//send the modified game state to the player
		Log.i("sendUpdatedState", "modifiedState, top discard card: " 
				+ modifiedState.getTopDiscardCard());
		String playerMessage = (String) messagesToPlayers.get
				(this.getPlayerIdx(p));
		
		//index of current player
		int currentPlayerIdx = masterGameState.getCurrentPlayer();
		//player names array
		String[] playerNames = this.playerNames;
		//current player's name
		String currentPlayerName = playerNames[currentPlayerIdx];
				
		//if it is not the player's turn
		if (currentPlayerIdx != this.getPlayerIdx(p))
		{
			playerMessage = currentPlayerName + " is making a move . . . .";
		}
		else //it is the player's turn
		{
			playerMessage = "Tap a pile to draw a card.";
			
			if (masterGameState.getCanDiscard())
			{
				playerMessage = "Choose a card to discard";
			}
		}
		modifiedState.setMessage(playerMessage);
		
		Log.i("sendUpdatedStateTo", "Draw Pile has " + masterGameState.getDrawPile().getCards().size());
		Log.i("TTLocalGame - sending state", modifiedState.toString());
		
		//send the state
		p.sendInfo(modifiedState);
	}
	
	/*
	 * checkIfGameOver
	 * 
	 * this method is called automatically by the game framework
	 * 
	 * @return a string if the game is over; this string will
	 *   	be the message that pops up
	 * @return null if the game is not over=
	 */
	public final String checkIfGameOver() {
		Log.i("checkIfGameOver", "checkIfGameOver was called");
		
		//need to find the index of the player with the highest score
		int playerWithHighestScore = -1;
		int previousScore = 0;
		//iterate through the players and get the index of the 
			//player with the highest score
		for (int i = 0; i < masterGameState.getNumPlayers(); ++i)
		{
			if (previousScore < masterGameState.getPlayersScore(i))
			{
				playerWithHighestScore = i;
				previousScore = masterGameState.getPlayersScore(i);
			}
			else if (previousScore == masterGameState.getPlayersScore(i))
			{
				playerWithHighestScore = -1;
			}
		}
		
		//round is over if it is round 12
		if (masterGameState.getRoundNumber() > 11)
		{
			if (playerWithHighestScore == -1) //if there is a draw
			{
				return "Game is over. There is a draw.";
			}
			return "Game is over. Player " + playerWithHighestScore +
					" is the winner." ;
		}
		//return null to indicate game not over
		else return null;
	}

	@Override
	/*
	 * canMove
	 * 
	 * A player is always allowed to move since a player is always
	 * allowed to rearrange his/her hand.
	 * 
	 * @param playerIdx
	 */
	protected boolean canMove(int playerIdx) {
		return true;
	}


}
