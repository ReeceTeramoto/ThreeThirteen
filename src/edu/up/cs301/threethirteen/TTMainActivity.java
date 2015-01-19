package edu.up.cs301.threethirteen;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import android.graphics.Color;

/**
 * this is the primary activity for Three-Thirteen game
 * 
 * @author Steven R. Vegdahl
 *
 * @author Christine Chen
 * 
 * @version 11/13/2014
 * @version 12/5/14
 *
 */
public class TTMainActivity extends GameMainActivity {
	
	public static final int PORT_NUMBER = 4752;

	/** a ThreeThirteen game for four players. The default is human vs. computer */
	@Override
	public GameConfig createDefaultConfig() {

		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
		
		playerTypes.add(new GamePlayerType("human player (white)") {
			public GamePlayer createPlayer(String name) {
				return new TTHumanPlayer(name, Color.WHITE);
			}});
		
		
		playerTypes.add(new GamePlayerType("human player (light wood)") {
			public GamePlayer createPlayer(String name) {
				return new TTHumanPlayer(name, android.graphics.Color.argb(255, 193,154,107));
			}
		});
		playerTypes.add(new GamePlayerType("computer player (dumb)") {
			public GamePlayer createPlayer(String name) {
				return new TTComputerPlayerDumb(name);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (smart)") {
			public GamePlayer createPlayer(String name) {
				return new TTComputerPlayerSmart(name);
			}
		});


		// Create a game configuration class for ThreeThirteen
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 4, "Three-Thirteen", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		
		defaultConfig.addPlayer("Dumb AI", 2);
	
		
		//done!
		return defaultConfig;
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame() {
		return new TTLocalGame();
	}
}
