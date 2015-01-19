
package edu.up.cs301.threethirteen;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * TTHumanPlayer
 * 
 * represents a human player interacting with a GUI display to play a game
 * of Three Thirteen
 * 
 * @author Christine Chen
 * @version 11/13/14
 * @version 11/17/14 2:51 pm
 * @version 11/29/14
 * @version 12/5/14
 * 
 * Acknowledgments: the methods receiveInfo and setAsGui were based completely
 * on the identically named methods in Dr.Vegdahl's SJHumanPlayer.java.
 *
 *
 */
public class TTHumanPlayer extends GameHumanPlayer implements Animator, OnClickListener
{
	//constants
	
	//since the exact width and height of the AnimationSurface are not known
	//at this point, the various dimensions are expressed as percentages
	public static final float DECK_WIDTH = 0.10f;
	
	public static final float DECK_HT = 0.24f;
	
	//the space between the discard pile and stock pile
	public static final float DECK_SPC = 0.05f;
	
	//helps to draw the highlight rectangle
	public static final float STRETCH_BY = 2f;
	
	//instance variables
	
	//the Activity
	private Activity myActivity;
	
	//the AnimationSurface that the game will be drawn on 
	private AnimationSurface animSurface;
	
	//the buttons
	private Button discardButton;
	private Button goOutButton;
	private Button swapButton;
	private Button insertButton;
	
	//the messageBoard textView
	private TextView messageBoard;
	
	//the score textViews for the players
	private TextView scorePlayer1;
	private TextView scorePlayer2;
	private TextView scorePlayer3;
	private TextView scorePlayer4;
	
	//an array to hold the score TextViews
	private TextView [] myScoreTextViews;
	
	//array that holds players' numeric scores
	private int [] playersScores;
	
	//String that holds the messageBoard's message
	private String message;
	
	//TextView that displays 
	//pertinent information if a player goes out
	//(the "winning" hand, the new round number...)
	private TextView goOutInfo;
	
	//the state instance variable for the human player
	private TTState myState;
	
	//the player's index
	int myId;
	
	//background color
	private int bkColor;
	
	//the stock pile and discard pile
	private RectF stockPile;
	private RectF discardPile;
	
	//brush for the discard pile
	private Paint deckBrush;
	
	//brush for stock pile
	private Paint stockPileBrush;
	
	//brush for cards in the player's hand
	private Paint handCardsBrush;
	
	//brush that highlights card
	private Paint highlightBrush;
	
	//double array to hold the RectFs that will be used
	//to help draw the cards in the player's hand onto the GUI
	private RectF[][] recs;
	
	//x and y coordinates of the DOWN touch
	private int xCoordDown;
	private int yCoordDown;
	
	//boolean that indicates whether a player
	//actually touched a card on the screen
	private boolean tappedACard;
	
	//instance variables that hold the location of the 
	//the RectF the player tapped (in the recs array)
	private int rowOfTappedRect;
	private int colOfTappedRect;
	
	//instance variable that holds the index of the Card that was tapped
	private int indexOfCardDown;
	
	//boolean that indicates whether or not there is a valid Card index in 
	//indexOfCardDown
	private boolean haveCardDownIndex;
	
	private boolean swapButtonHit;
	private boolean insertHit;
	
	
	//if a player wishes to move a card to a different spot,
	//indexMoveTo holds the index of the destination spot in the player's hand
	private int indexMoveTo;
	
	
	//Card instance variable to hold the top card of the 
	//discard pile
	private Card topDiscardCard;
	
	//Deck instance variable to hold player's hand
	private Deck playersHand;
	
	//create a handler instance variable 
	private Handler myHandler;
	
	/**
	 * TTHumanPlayer
	 * 
	 * constructor 
	 * 
	 * @param name: name of player
	 * @param backColor: background color
	 */
	public TTHumanPlayer(String name, int backColor) 
	{
		super(name);
		
		//set the background color 
		bkColor = backColor;
		
		//initialize discardPile and stockPile
		discardPile = new RectF();
		stockPile = new RectF();
		
		//initialize deckBrush instance variable (set the actual instance variable, 
		//don't create a new local variable)
		deckBrush = new Paint();
		
		//set the style of the brush so that it creates an outline
		deckBrush.setStyle(Paint.Style.STROKE);
		deckBrush.setStrokeWidth(2.5f);
		deckBrush.setColor(Color.BLACK);
		
		//initialize stockPileBrush instance variable
		stockPileBrush = new Paint();
		stockPileBrush.setColor(Color.BLUE);
		
		//initialize highlightBrush
		highlightBrush = new Paint();
		
		//set the style of the brush so that it creates an outline
		highlightBrush.setStyle(Paint.Style.STROKE);
		highlightBrush.setStrokeWidth(5f);
		highlightBrush.setColor(Color.YELLOW);
		
		//initialize the handCardsBrush instance variable
		handCardsBrush = new Paint();
		
		//set the style of the brush so that it creates an outline
		handCardsBrush.setStyle(Paint.Style.STROKE);
		handCardsBrush.setStrokeWidth(2.5f);
		handCardsBrush.setColor(Color.BLUE);
		
		
		//initialize recs to hold two rows of RectFs, with 
		//each row containing nine RectFs 
		recs = new RectF[2][9];
		
		//call setRects() to fill recs with 
		//RectF objects
		this.setRects();
		
		//initialize tappedACard to false;
		tappedACard = false;
		
		haveCardDownIndex = false;
		
		//initialize message to "No Message!"
		message = "No Message!";
		
		//initialize myScoreTextViews
		myScoreTextViews = new TextView[4];
		
		//initialize swapButtonHit and insertHit
		swapButtonHit = false;
		insertHit = false;
		
		
	}
	
	/**
	 * setRects()
	 * a helper method that initializes the recs array
	 * with RectF objects
	 */
	private void setRects()
	{
		for(int r = 0; r<recs.length; r++)
		{
			for(int k = 0; k<recs[r].length; k++)
			{
				recs[r][k] = new RectF();
			}
		}
	}
	
	
	/**
	 * setAsGui 
	 * 
	 * this method is called whenever the GUI has changed
	 * 
	 * @param activity: the current activity
	 */
	public void setAsGui(GameMainActivity activity) 
	{
		//set myActivity instance variable
		myActivity = activity;
		
		//load the layout resource for the configuration
		activity.setContentView(R.layout.activity_tt);
		
		//link the AnimationSurface to an Animator
		animSurface = (AnimationSurface) myActivity.findViewById(R.id.TTAnimSurface);
		animSurface.setAnimator(this);
		
		//get the discard button
		discardButton = (Button) myActivity.findViewById(R.id.discardButton);
		discardButton.setOnClickListener(this);
		
		
		//get the go out button
		goOutButton = (Button) myActivity.findViewById(R.id.goOutButton);
		goOutButton.setOnClickListener(this);
		

		//get the swap button
		swapButton = (Button) myActivity.findViewById(R.id.swapButton);
		swapButton.setOnClickListener(this);
		
		//get the insert button
		insertButton = (Button) myActivity.findViewById(R.id.insertButton);
		insertButton.setOnClickListener(this);
		
		//get the TextView messageBoard
		messageBoard = (TextView) myActivity.findViewById(R.id.messageBoard);
		
		//get the scores TextViews
		scorePlayer1 = (TextView) myActivity.findViewById(R.id.scorePlayer1);
		scorePlayer2 = (TextView) myActivity.findViewById(R.id.scorePlayer2);
		scorePlayer3 = (TextView) myActivity.findViewById(R.id.scorePlayer3);
		scorePlayer4 = (TextView) myActivity.findViewById(R.id.scorePlayer4);
		
		myScoreTextViews[0] = scorePlayer1;
		myScoreTextViews[1] = scorePlayer2;
		myScoreTextViews[2] = scorePlayer3;
		myScoreTextViews[3] = scorePlayer4;
		
		//get the goOutInfo TextView
		goOutInfo = (TextView) myActivity.findViewById(R.id.roundGoOutInfo);
		
		//read in the Card images
		Card.initImages(activity);
		
		//initialize the handler
		myHandler = new Handler();
		
		
		//process the state (if it's not null)
		if (myState != null)
		{
			receiveInfo(myState);
		}

	}
	
	@Override
	/**
	 * receiveInfo
	 * 
	 * called whenever the player receives a GameInfo object
	 * 
	 * @param info: the GameInfo object
	 */
	public void receiveInfo(GameInfo info)
	{
		Log.i("TTHumanPlayer - receiving state", info.toString());
		
		if (!(info instanceof TTState))
		{
			//flash the screen
			animSurface.flash(Color.RED, 50);
			
		}
		else
			
		{
			
			Log.i("receiveInfo", "receiving state");
			//set the player's inherited state instance variable to info
			myState = (TTState) info;
			
			//set topDiscardCard and playersHand
			topDiscardCard = myState.getTopDiscardCard();
			
			if(topDiscardCard != null)
			{
				Log.i("receiveInfo HumanPlayer", "topDiscardCard: " + topDiscardCard.toString());
			}
			else
			{
				Log.i("receiveInfo HumanPlayer", "topDiscardCard is null");
			}
			
			playersHand = myState.getPlayersHand(this.playerNum);
			
				
			//post a Runnable object to the Handler so that when the main thread 
			//goes to sleep, the run() method of the Runnable object will be run on the main thread
			myHandler.post(new Runnable()
			{
				public void run()
				
				{
					//get the player's message from myState
					message = myState.getMessage();
					
					//get the players' scores from myState
					playersScores = myState.getScoresOfPlayers();
					
					//get the names of the players
					String [] myPlayersNames = myState.getNamesOfPlayers();
					
					if (playersScores != null)
					{
						Log.i("receiveInfo", "about to print out players scores");
						
						//loop through myScoreTextViews
						for(int i = 0; i<myState.getNumPlayers(); i++)
						{
								if (myPlayersNames[i] != null)
								{
									//make sure the name is not ridiculously long (max limit of 15 characters)
									if (myPlayersNames[i].length() < 16)
									{
										//print out the player's scores
										myScoreTextViews[i].setText(myPlayersNames[i] + ": " + playersScores[i]);
										Log.i("receiveInfo", myPlayersNames[i] + ": " + playersScores[i]);
									}
									else
									{
										//only display a substring of the player's name with 15 characters
										String tempName = myPlayersNames[i].substring(0,15);
										
										//print out the player's scores
										myScoreTextViews[i].setText(tempName + ": " + playersScores[i]);
										Log.i("receiveInfo", tempName + ": " + playersScores[i]);
									}
									
								}
								else
								{
									//print out the player's scores
									myScoreTextViews[i].setText("P" + (i+1) + ": " + playersScores[i]);
									Log.i("receiveInfo", "P" + (i+1) + ": " + playersScores[i]);
								}
								
							
						}
						
					}
					
					if(message != null)
					{
						//display the message 
						messageBoard.setText("MESSAGE BOARD:\n" + message);
					}
					
					if(myState.getGoOutMessage() != null)
					{
						//display the appropriate message in the goOutInfo TextView
						goOutInfo.setText(myState.getGoOutMessage());
					}
					
					
				}
			});
			
			//if myState.getSomeoneHasGoneOut returns true, then have the screen flash green
			if (myState.getSomeoneHasGoneOut())
			{
				animSurface.flash(Color.GREEN, 1000);
			}
		}
		

	}


	

	
	@Override
	/**
	 * getTopView
	 * 
	 * returns the top GUI view
	 */
	public View getTopView() 
	{
		return myActivity.findViewById(R.id.topGuiView);
	}
	
	/**
	 * interval
	 * 
	 * returns the time, in ms, between each "tick" 
	 */
	public int interval() 
	{
		
		return 50;
	}

	/**
	 * backGroundColor
	 * 
	 * returns the background color of the AnimationSurface
	 */
	public int backgroundColor() 
	{
		
		return bkColor;
	}
	
	/**
	 * doPause
	 * 
	 * returns true if the animation should be stopped; false otherwise
	 */
	public boolean doPause() 
	{
		return false;
	}

	/**
	 * doQuit
	 * 
	 * returns true if the animation should be terminated; false otherwise
	 */
	public boolean doQuit() 
	{
		
		return false;
	}

	
	/**
	 * tick
	 * 
	 * this method gets called on a set, regular basis to redraw the player's screen 
	 */
	public void tick(Canvas g) 
	{
		
		//get the height and width of the canvas
		int sW = g.getWidth();
		int sH = g.getHeight();
		
		//draw the two decks
		this.drawCardPiles(g, sW, sH, topDiscardCard);
		
		//draw the player's hand
		this.drawHand(g, sW, sH, playersHand);
		
		//NOTE: the author understands that it is actually quite redundant to pass
		//the topDiscardCard and playersHand along as arguments when the 
		//methods could access the instance variables themselves
		

	}
	
	/**
	 * drawCardPiles
	 * 
	 * helper method that draws the discard pile and stock pile
	 * @param c canvas
	 * @param canvasW canvas width
	 * @param canvasH canvas height
	 * @param topDiscardCard the top card on the discard pile
	 */
	private void drawCardPiles(Canvas c, int canvasW, int canvasH, Card topDiscardCard)
	{
		//corresponds to half of sW
		float xMid = canvasW/(2.0f);
		
		//the y-value for the top left corner of the stock/discard piles
		float yStart = canvasH/(4.5f);
		
		float deckHt = DECK_HT * canvasH;
		
		float deckWth = DECK_WIDTH * canvasW;
		
		//space between the two decks
		float deckSpacer = DECK_SPC * canvasW;
	
		
		//x coordinates for stock pile
		float xTopLeftS = xMid-deckSpacer-deckWth;
		float xBotRightS = xMid-deckSpacer;
		
		//x coordinates for discard pile
		float xTopLeftD = xMid+deckSpacer;
		float xBotRightD = xMid+deckSpacer+deckWth;
		
		//y coordinates for both piles
		float yTopLeft = yStart;
		float yBotRight = yStart+deckHt;    
		
		
		
		//draw the StockPile
		stockPile.set(xTopLeftS, yTopLeft, xBotRightS, yBotRight);
		c.drawRect(stockPile, stockPileBrush);
		
		
		
		//draw the DiscardPile
		//if topDiscardCard != null, draw that Card
		
		if(topDiscardCard != null)
		{
			discardPile.set(xTopLeftD, yTopLeft, xBotRightD, yBotRight);
			
			//draw the card
			topDiscardCard.drawOn(c, discardPile);
			
		
		}
		else
		{	
			//just draw the outline of a rect
			discardPile.set(xTopLeftD, yTopLeft, xBotRightD, yBotRight);
			c.drawRect(discardPile, deckBrush);
		}
		
		
	}

	
	/**
	 * drawHand
	 * 
	 * helper method that draws the player's hand on the bottom
	 * of the screen
	 * @param c canvas
	 * @param cW canvas width
	 * @param cH canvas height
	 * @param playersHand a Deck object containing the player's cards
	 */
	 private final void drawHand(Canvas c, int cW, int cH, Deck playersHand)
	 
	{
		//the y-value that indicates the top of the area 
		//containing the player's hand
		 float yTopHandArea = cH/(1.8f);
		
		//the card width 
		//safe to use recs[0].length because there must always
		//be at least one row of cards
		float cardWidth = cW/(recs[0].length+3);
				
		//the space between each card in a row
		float paddingW = (cardWidth)/(recs[0].length);
				
		//the card height (recs.length represents the numbers of rows
		//of cards in the player's hand)
		float cardHeight = (cH-yTopHandArea)/(recs.length);
		
		//the space between each card in a column 
		float paddingH = (cardHeight/8);
		
		//to ensure that cards are not drawn partially off-screen,
		//the cardHeight value must be slightly decreased 
		float toSubtractFromHt = paddingH/2;
		
		
		//iterate through the recs 2D array, setting and drawing each
		//rectangle
		
		//define the variables that will control the outer loop
		int r;
		
		//the left top corner y-coordinate
		float leftTY;
		
		//the right bottom corner y-coordinate
		float rightBY;
		
		//create an index to help iterate through the player's hand
		int index = 0;
		
		
		//for each iteration through this outer loop, make sure to update 
		//leftTY and rightBY since the rectangles are moving down a level
		for(leftTY = yTopHandArea, rightBY = (yTopHandArea + cardHeight-toSubtractFromHt), 
			r = 0; r<recs.length; r++, leftTY = (rightBY+paddingH), 
			rightBY = (rightBY + paddingH + cardHeight - toSubtractFromHt))
		{	
			//the left top corner x-coordinate
			float leftTX = paddingW;
			
			//the right bottom corner x-coordinate
			float rightBX = leftTX + cardWidth;
			
			for(int k = 0; k < recs[r].length; k++)
			{	
				//make sure that playersHand != null
				//make sure that index is less than the size 
				//of the playersHand Deck
				//make sure that the Card at index is not null
				if(playersHand != null &&
					index < playersHand.size() &&
					playersHand.getCardAtIndex(index) != null)
				{
					//there is a card to draw on the screen
					
					//get the card
					Card card = playersHand.getCardAtIndex(index);
					
					//set recs[r][k] to the proper values
					recs[r][k].set(leftTX, leftTY, rightBX, rightBY);
					
					//draw the card
					card.drawOn(c, recs[r][k]);
					
					
					if(haveCardDownIndex && 
					   index == indexOfCardDown)
					{
						//highlight the card
						RectF highlight = new RectF((leftTX - STRETCH_BY), (leftTY - STRETCH_BY),
											(rightBX + STRETCH_BY), (rightBY + STRETCH_BY));
						
						c.drawRect(highlight, highlightBrush);
					}
				}
				else
				//otherwise, just draw the outline of a rectangle
				{
					//see if the blank was tapped. If so, draw a yellow rect.
					if(haveCardDownIndex && 
					   index == indexOfCardDown)
					{
						RectF highlight = new RectF((leftTX - STRETCH_BY), (leftTY - STRETCH_BY),
											(rightBX + STRETCH_BY), (rightBY + STRETCH_BY));
						
						c.drawRect(highlight, highlightBrush);
					}
					//If not, just draw a blue rect.
					else
					{	
						//set the rectangle to the proper coordinates
						recs[r][k].set(leftTX, leftTY, rightBX, rightBY);
						c.drawRect(recs[r][k], handCardsBrush);
					
					}
				}
				
				
	
				//update leftTX and rightBX so that the next rectangle
				//is drawn further down the row
				leftTX = leftTX + cardWidth + paddingW;
				rightBX = rightBX + paddingW + cardWidth;
				
				//update the index
				index++;
			}
				
			
		}   
    }

	 
	 /**
	  * onTouch
	  * responds to any events fired when the user touches the screen
	  */
	public void onTouch(MotionEvent event) 
	{
		if (myState == null)
		{
			return;
		}
		
		//get the coordinates of the MotionEvent
		int eX = (int)event.getX();
		int eY = (int)event.getY();
		
		//get the action message
		int actionMessage = event.getAction();
		
		if(actionMessage == MotionEvent.ACTION_DOWN)
		{	//set the x and y coordinates for the down move
			xCoordDown = eX;
			yCoordDown = eY;
			
			//see if the user is trying to draw a card
			if(stockPile.contains(xCoordDown, yCoordDown))
			{
				
					//send off a TTDrawAction to TTLocalGame
					//the false indicates that the player wishes to draw
					//from the StockPile and not from the DiscardPile
					game.sendAction(new TTDrawAction(this, false));
					
					Log.i("onTouch", "sent a draw action (stock pile)");
					
					
			}
			else if(discardPile.contains(xCoordDown, yCoordDown))
			{
			
				//send off a TTDrawAction to TTLocalGame
				//the true indicates that the player wishes to draw
				//from the DiscardPile
				game.sendAction(new TTDrawAction(this, true));
				
				Log.i("onTouch", "sent draw action (discard pile)");
				
			}
			
			
			//otherwise see if the player is trying to select a card in his/her hand
			//make sure recs is not null
			else if(recs != null)
			{
				//make sure tappedACard is false
				tappedACard = false;
				
			
				//iterate through recs to see if the player 
				//tapped one of the "card" Rects in his/her hand
				for (int r = 0; r<recs.length; r++)
				{
					for(int k = 0; k<recs[r].length; k++)
					{
						if(recs[r][k].contains((float)xCoordDown, (float)yCoordDown))
						{
							//set tappedACard to true (this could just be a blank)
							tappedACard = true;
							
							//set rowOfTappedRect and colOfTappedRect
							rowOfTappedRect = r;
							colOfTappedRect = k;
							
							
							
							//if the swap button or insert button was hit, it is time to send off
							//a rearrange action (make sure that haveCardDownIndex == true, meaning
							// indexOfCardDown contains the index of the card the player wants
							//to move)
							if (haveCardDownIndex && (swapButtonHit || insertHit))
							{
								indexMoveTo = this.getCardIndex(r,k);
								
								if(swapButtonHit)
								{	
									//the "true" argument indicates this is a swap and not an insert
									game.sendAction(new TTRearrangeAction(this, indexOfCardDown, indexMoveTo, true));
									
									swapButtonHit = false;
									
									//the move has been handled, so set haveCardDownIndex to false
									haveCardDownIndex = false;
									Log.i("onTouch", "sending swap action from: " + indexOfCardDown + " to " + indexMoveTo);
								}
								else
								{
									//insertHit is true
									//send a TTRearrangeAction with a false to indicate that
									//the rearrange should be performed as an insert
									game.sendAction(new TTRearrangeAction(this, indexOfCardDown, indexMoveTo, false));
									
									Log.i("onTouch", "sending insert action from: " + indexOfCardDown + " to " + indexMoveTo);
									
									//set insertHit to false
									insertHit = false;
									haveCardDownIndex = false;
								}
								
								Log.i("onTouch", "Rearrange action sent.");
								Log.i("onTouch", "swap1index = " + indexOfCardDown 
										+ ", swap2index = " + indexMoveTo);
								
							}
							else
							{	//get the index of the Card (could be a blank) in
								//the player's hand on which the DOWN move occurred
								indexOfCardDown = this.getCardIndex(r,k);
								
								//set haveCardDownIndex to true
								haveCardDownIndex = true;
							}
							
							
							
							
							Log.i("onTouch", "xCoordDown: " + eX
								  + " yCoordDown: " + eY + " tappedACard: "
								  + tappedACard + " rowOfTappedRect: "+ rowOfTappedRect
								  + " colOfTappedRect: " + colOfTappedRect);
								  
							
							
						}
						
						else if(!tappedACard && !swapButtonHit && !insertHit)
						{
							//the user maybe just tapped a blank area on the screen
							
							//set haveCardDownIndex to false so that 
							//none of the cards are highlighted.
							haveCardDownIndex = false;
						}
					}
				}
			}
			
			if (myState.getCurrentPlayer() == this.myId)
			{
				Log.i("onTouch", "It is my turn.");
			}
			else
			{
				Log.i("onTouch", "It is NOT my turn.");
			}
		}
		
	}
	
	/**
	 * getCardIndex
	 * a helper method that returns the index of the card in the player's hand
	 * that corresponds to recs[row][col]
	 * @param row
	 * @param col
	 * 
	 */
	private int getCardIndex(int row, int col)
	{	
		//the card index to return (cardIndex will always be modified
		//and will never return -1).
		int cardIndex = -1;
		
		if(row<1 && row >=0)
		{
			//the index of the card is simply equal to col
			cardIndex = col;
		}
		if(row >= 1)
		{
			//the index of the card now 
			//equals col + (the row number * 
			// the length of the row). This assumes that all
			//rows have the same number of columns
			cardIndex = col + (row*(recs[row].length));
		}
		
		return cardIndex;
	}
	
	

	/**
	 * getPlayerNum
	 * @Override
	 * returns the player's ID
	 * 
	 */
	public int getPlayerNum()
	{
		return playerNum;
	}
	
	
	
	/**
	 * getPlayerName
	 * returns the player's name
	 * @Override
	 * 
	 */
	public String getPlayerName()
	{
		return name;
	}
	
	/**
	 * responds to events fired by any of the buttons
	 */
	public void onClick(View v) 
	{
		if(v==discardButton)
		{
			//check that tappedACard and haveCardDownIndex == true
			if(tappedACard && haveCardDownIndex)
			{
				//send a TTDiscardAction to TTLocalGame with indexOfCardDown
				//and a false for the third parameter to indicate that the player is
				//not going out as well
				game.sendAction(new TTDiscardAction(this, indexOfCardDown, false));
				
				
				
				//set tappedACard and haveCardDownIndex to false to indicate
				//that the player's move has been handled
				tappedACard = false;
				haveCardDownIndex = false;
				Log.i("onClick", "sent discard action");
			}
		}
		if(v==goOutButton)
		{
			//check that tappedACard and haveCardDownIndex == true
			if(tappedACard && haveCardDownIndex)
			{
				//send a TTDiscardAction to TTLocalGame with indexOfCardDown
				//and a true for the third parameter to indicate that the player is
				//going out as well
				game.sendAction(new TTDiscardAction(this, indexOfCardDown, true));
				
				//set tappedACard and haveCardDownIndex to false to indicate
				//that the player's move has been handled
				tappedACard = false;
				haveCardDownIndex = false;
				
				Log.i("OnClick", "sent discard action (canGoOut is true)");
			}
		}
		if (v==swapButton)
		{
			Log.i("onClick", "Swap button hit.");
			swapButtonHit = true;
		}
		if(v==insertButton)
		{
			Log.i("onClick", "insert button hit");
			insertHit = true;
		}
	}

		
	
		
	}
	

   