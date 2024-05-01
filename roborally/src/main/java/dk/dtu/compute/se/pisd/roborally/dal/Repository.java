/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Repository implements IRepository {
	
	private static final String GAME_GAMEID = "gameID";

	private static final String GAME_NAME = "name";
	
	private static final String GAME_CURRENTPLAYER = "currentPlayer";

	private static final String GAME_PHASE = "phase";

	private static final String GAME_STEP = "step";
	
	private static final String PLAYER_PLAYERID = "playerID";
	
	private static final String PLAYER_NAME = "name";

	private static final String PLAYER_COLOUR = "colour";
	
	private static final String PLAYER_GAMEID = "gameID";
	
	private static final String PLAYER_POSITION_X = "positionX";

	private static final String PLAYER_POSITION_Y = "positionY";

	private static final String PLAYER_HEADING = "heading";

	private static final String PLAYER_CHECKP = "checkpoints";

    private static final String CARDNAME = "CardName";

	private static final String PLAYER_HEALTH = "health";


	private Connector connector;

	public Repository(Connector connector){
		this.connector = connector;
	}

	@Override
	public boolean createGameInDB(Board game) {
		if (game.getGameId() == null) {
			Connection connection = connector.getConnection();
			try {
				connection.setAutoCommit(false);

				PreparedStatement ps = getInsertGameStatementRGK();
				// TODO: the name should eventually be set by the user
				//       for the game and should be then obtained by
				//       game.getName();
				ps.setString(1, "Date: " +  new Date()); // instead of name
				ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
				ps.setInt(3, game.getPhase().ordinal());
				ps.setInt(4, game.getStep());
				ps.setString(5, game.getBoardName());
				// If you have a foreign key constraint for current players,
				// the check would need to be temporarily disabled, since
				// MySQL does not have a per transaction validation, but
				// validates on a per row basis.
				// Statement statement = connection.createStatement();
				// statement.execute("SET foreign_key_checks = 0");
				
				int affectedRows = ps.executeUpdate();
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (affectedRows == 1 && generatedKeys.next()) {
					game.setGameId(generatedKeys.getInt(1));
				}
				generatedKeys.close();
				
				// Enable foreign key constraint check again:
				// statement.execute("SET foreign_key_checks = 1");
				// statement.close();

				createPlayersInDB(game);

				// TODO V4a: this method needs to be implemented first
				createCardFieldsInDB(game);



				// since current player is a foreign key, it can only be
				// inserted after the players are created, since MySQL does
				// not have a per transaction validation, but validates on
				// a per row basis.
				ps = getSelectGameStatementU();
				ps.setInt(1, game.getGameId());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
					rs.updateRow();
				} else {
					// TODO error handling
					throw new SQLException("Game rows not affected.");
				}
				rs.close();

				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
				System.err.println("Some DB error");
				
				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO error handling
					e1.printStackTrace();
				}
			}
		} else {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
		}
		return false;
	}
		
	@Override
	public boolean updateGameInDB(Board game) {
		assert game.getGameId() != null;
		
		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameId());
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
				rs.updateInt(GAME_STEP, game.getStep());
				rs.updateRow();
			} else {
				// TODO error handling
				throw new SQLException("Game rows not affected.");
			}
			rs.close();

			updatePlayersInDB(game);
			/* TODO V4a: this method needs to be implemented first*/
			updateCardFieldsInDB(game);


            connection.commit();
            connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");
			
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO error handling
				e1.printStackTrace();
			}
		}

		return false;
	}
	
	@Override
	public Board loadGameFromDB(int id) {
		Board game;
		try {
			// XXX here, we could actually use a simpler statement
			//     which is not updatable, but we reuse the one from
			//     above for simplicity
			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {
				// TODO V4b: and we should also store the name of the used game board
				//      in the database, and load the corresponding board from the
				//      JSON file. For now, we use the default game board.

				String boardName = rs.getString("board_name");
				game = LoadBoard.loadBoard(boardName); // Load the specific board
				game.setBoardName(boardName);

				if (game == null) {
					return null;
				}
				playerNo = rs.getInt(GAME_CURRENTPLAYER);
				// TODO currently we do not set the games name (needs to be added)
				game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
				game.setStep(rs.getInt(GAME_STEP));
			} else {
				// TODO error handling
				return null;
			}
			rs.close();

			game.setGameId(id);			
			loadPlayersFromDB(game);

			if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				// TODO  error handling
				return null;
			}

			/* TODO V4a: this method needs to be implemented first*/
			loadCardFieldsFromDB(game);


			return game;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");
		}
		return null;
	}
	
	@Override
	public List<GameInDB> getGames() {
		// TODO when there are many games in the DB, fetching all available games
		//      from the DB is a bit extreme; eventually there should a
		//      method that can filter the returned games in order to
		//      reduce the number of the returned games.
		List<GameInDB> result = new ArrayList<>();
		try {
			PreparedStatement ps = getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(GAME_GAMEID);
				String name = rs.getString(GAME_NAME);
				result.add(new GameInDB(id,name));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO proper error handling
			e.printStackTrace();
		}
		return result;		
	}

	/**
	 * Added the checkpoints point score, in order to save that as well to the player
	 * @param game the initialized gameboard
	 * @throws SQLException
	 */
	private void createPlayersInDB(Board game) throws SQLException {
		// TODO code should be more defensive
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());
		
		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(PLAYER_GAMEID, game.getGameId());
			rs.updateInt(PLAYER_PLAYERID, i);
			rs.updateString(PLAYER_NAME, player.getName());
			rs.updateString(PLAYER_COLOUR, player.getColor());
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateInt(PLAYER_CHECKP , player.getCurrentCheckpoint());
			rs.updateInt(PLAYER_HEALTH, player.getHealth());
			rs.insertRow();
		}
		rs.close();
	}


	/**
	 * @author s235459
	 * @param game the initialized gameboard
	 * Creates cardfields tables in the database, to store cards
	 * @throws SQLException
	 */

	private void createCardFieldsInDB(Board game) throws SQLException{
		PreparedStatement ps = getSelectCards();
		ps.setInt(1, game.getGameId());
		ResultSet rs = ps.executeQuery();

		CommandCardField cardfield;
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);

			for (int pos = 0 ; pos < player.NO_CARDS ; pos++){
				rs.moveToInsertRow();

				cardfield = player.getCardField(pos);
				CommandCard card = cardfield.getCard();

				rs.updateInt(PLAYER_GAMEID, game.getGameId());
				rs.updateInt(PLAYER_PLAYERID, i);
				rs.updateInt("cardType" , 0);
				rs.updateInt("position", pos );
				if (card != null) {
					rs.updateString(CARDNAME, card.getName());
				} else {
					rs.updateNull(CARDNAME);
				}
				rs.insertRow();
			}

			for (int pos = 0 ; pos < player.NO_REGISTERS ; pos++){
				rs.moveToInsertRow();

				// int type = rs.getInt("cardType");
				cardfield = player.getProgramField(pos);

				CommandCard card = cardfield.getCard();

				rs.updateInt(PLAYER_GAMEID, game.getGameId());
				rs.updateInt(PLAYER_PLAYERID, i);
				rs.updateInt("cardType" , 1);
				rs.updateInt("position", pos );
				if (card != null) {
					rs.updateString(CARDNAME, card.getName());
				} else {
					rs.updateNull(CARDNAME);
				}
				rs.insertRow();
			}
		}
		rs.close();
	}


	private void loadPlayersFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersASCStatement();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId) {
				// TODO this should be more defensive
				String name = rs.getString(PLAYER_NAME);
				String colour = rs.getString(PLAYER_COLOUR);
				Player player = new Player(game, colour ,name , 30);
				game.addPlayer(player);

				int x = rs.getInt(PLAYER_POSITION_X);
				int y = rs.getInt(PLAYER_POSITION_Y);
				player.setSpace(game.getSpace(x,y));
				int heading = rs.getInt(PLAYER_HEADING);
				player.setHeading(Heading.values()[heading]);
				int checkpoint = rs.getInt(PLAYER_CHECKP);
				player.setCurrentCheckpoint(checkpoint);
				int health = rs.getInt(PLAYER_HEALTH);
				player.setHealth(health);
			} else {
				// TODO error handling

				System.err.println("Game in DB does not have a player with id " + i +"!");
			}
		}

		rs.close();
	}

	private void updatePlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			// TODO should be more defensive
			Player player = game.getPlayer(playerId);
			// rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateInt(PLAYER_CHECKP , player.getCurrentCheckpoint());
			rs.updateInt(PLAYER_HEALTH, player.getHealth());
			// TODO error handling
			// TODO take care of case when number of players changes, etc
			rs.updateRow();
		}
		rs.close();

		// TODO error handling/consistency check: check whether all players were updated
	}



	/**
	 * @author s235459
	 * Makes it possible to load the saved cards from the database repository
	 * @param game
	 * @throws SQLException
	 */
	private void loadCardFieldsFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectCard();
		ps.setInt(1, game.getGameId());
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			Player player = game.getPlayer(playerId);

			int type = rs.getInt("cardType");
			int pos = rs.getInt("position");
			CommandCardField cardfield;

			if (type == 1) {
				cardfield = player.getProgramField(pos);
			} else {
				cardfield = player.getCardField(pos);
			}

			String cardName = rs.getString(CARDNAME);
			Command command = giveCommandValue(cardName);

			if (command != null) {
				CommandCard card = new CommandCard(command);
				cardfield.setCard(card);
			} else {
				cardfield.setCard(null);
			}
		}
		rs.close();
	}


	/**
	 * @author s235459
	 * Updates the cardfields after being saved in the database repository
	 * @param game
	 * @throws SQLException
	 */
	private void updateCardFieldsInDB(Board game) throws SQLException{
		PreparedStatement ps = getSelectCards();
		ps.setInt(1, game.getGameId());
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			Player player = game.getPlayer(playerId);
			 int type = rs.getInt("cardType");
			 int pos = rs.getInt("position");

			 CommandCardField cardField;
			 if (type == 1) {
				 cardField = player.getProgramField(pos);
			 } else  {
				 cardField = player.getCardField(pos);
			 }
			 CommandCard card = cardField.getCard();

			 if (card != null) {
				 rs.updateString("CardName", card.getName());
			 } else {
				 rs.updateString("CardName", null);
			 }
		}
		rs.close();
	}

	/**
	 * @author s235459
	 * @param cardName The cardname retrieved from the database, in a String form
	 * @return returns the corresponding Command value
	 */
	private Command giveCommandValue(String cardName){

		if (cardName != null) {
			switch (cardName) {
				case "Fwd":
					return Command.FORWARD;
				case "Turn Right":
					return Command.RIGHT;
				case "Turn Left":
					return Command.LEFT;
				case "Fast Fwd":
					return Command.FORWARD;
				case "Diagonal Right":
					return Command.FRONT_RIGHT;
				case "Diagonal Left":
					return Command.FRONT_LEFT;
				case "Left OR Right":
					return Command.OPTION_LEFT_RIGHT;
				case "U Turn":
					return Command.U_TURN;
				case "SHOOT LASER":
					return Command.SHOOT_LASER;
				case "Health Potion":
					return Command.HEALTH_POTION;
				case "Move Back":
					return Command.MOVE_BACK;
				default:
					return null;
			}
		}
		return null;
	}


	private final String SQL_SELECT_CARD =
			"SELECT * FROM CardFields WHERE gameID = ?";

	private PreparedStatement select_cards= null;

	private PreparedStatement getSelectCard() {
		if (select_cards == null) {
			Connection connection = connector.getConnection();
			try {
				select_cards = connection.prepareStatement(
						SQL_SELECT_CARD,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_cards;
	}

	private static final String SQL_INSERT_GAME =
			"INSERT INTO Game(name, currentPlayer, phase, step, board_name) VALUES (?, ?, ?, ?, ?)";

	private PreparedStatement insert_game_stmt = null;

	private PreparedStatement getInsertGameStatementRGK() {
		if (insert_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_game_stmt = connection.prepareStatement(
						SQL_INSERT_GAME,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_game_stmt;
	}

	private static final String SQL_SELECT_GAME =
			"SELECT * FROM Game WHERE gameID = ?";

	private PreparedStatement select_game_stmt = null;

	private PreparedStatement getSelectGameStatementU() {
		if (select_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_game_stmt = connection.prepareStatement(
						SQL_SELECT_GAME,
						ResultSet.TYPE_FORWARD_ONLY,
					    ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_game_stmt;
	}

	private static final String SQL_SELECT_PLAYERS =
			"SELECT * FROM Player WHERE gameID = ?";

	private PreparedStatement select_players_stmt = null;

	private PreparedStatement getSelectPlayersStatementU() {
		if (select_players_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_players_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_stmt;
	}



	private static final String SQL_SELECT_PLAYERS_ASC =
			"SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";

	private PreparedStatement select_players_asc_stmt = null;

	private PreparedStatement getSelectPlayersASCStatement() {
		if (select_players_asc_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				// This statement does not need to be updatable
				select_players_asc_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS_ASC);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_asc_stmt;
	}

	private static final String SQL_SELECT_GAMES =
			"SELECT gameID, name FROM Game";

	private PreparedStatement select_games_stmt = null;


	private PreparedStatement getSelectGameIdsStatement() {
		if (select_games_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_games_stmt = connection.prepareStatement(
						SQL_SELECT_GAMES);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_games_stmt;
	}

	private static final String SELECT_CARDS_GAMEID =
			"SELECT* FROM CardFields WHERE gameID = ?";
	private PreparedStatement selecting_cards= null;

	private PreparedStatement getSelectCards(){
		if (selecting_cards == null) {
			Connection connection = connector.getConnection();
			try {
				selecting_cards = connection.prepareStatement(
						SELECT_CARDS_GAMEID,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return selecting_cards;
	}



}
