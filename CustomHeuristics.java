package hw2.agents.heuristics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.cwru.sepia.util.Direction;
import hw2.agents.heuristics.DefaultHeuristics.DefensiveHeuristics;
import hw2.agents.heuristics.DefaultHeuristics.OffensiveHeuristics;
import hw2.chess.game.move.CaptureMove;
import hw2.chess.game.move.Move;
import hw2.chess.game.move.PromotePawnMove;
import hw2.chess.game.piece.Piece;
import hw2.chess.game.piece.PieceType;
import hw2.chess.game.player.Player;
import hw2.chess.search.DFSTreeNode;
import hw2.chess.utils.Coordinate;

public class CustomHeuristics
{

	
	/**
	 * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
	 * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
	 * in DefaultHeuristics.java (which is in the same directory as this file)
	 */
	
	public static Player getMaxPlayer(DFSTreeNode node)
	{
		return node.getMaxPlayer();
	}
	public static Player getMinPlayer(DFSTreeNode node)
	{
		return getMaxPlayer(node).equals(node.getGame().getCurrentPlayer()) ? node.getGame().getOtherPlayer() : node.getGame().getCurrentPlayer();
	}
	
	// encourage pawns to advance
		private static final int[][] PAWN_TABLE = new int[][] {
				new int[]{0,   0,   0,   0,   0,   0,   0,   0},
				new int[]{5,   10,  10,  -20, -20, 10,  10,  5},
				new int[]{5,   -5,  -10, 0,   0,   -10, -5,  5},
				new int[]{0,   0,   0,   20,  20,  0,   0,   0},
				new int[]{5,   5,   10,  25,  25,  10,  5,   5},
				new int[]{10,  10,  20,  30,  30,  20,  10,  10},
				new int[]{50,  50,  50,  50,  50,  50,  50,  50},
				new int[]{0,   0,   0,   0,   0,   0,   0,   0} };

		// encourage knights to go towards the center & avoid the corners
		private static final int[][] KNIGHT_TABLE = new int[][] {
				new int[]{-50,  -40,  -30,  -30,  -30,  -30,  -40,  -50},
				new int[]{-40,  -20,  0,    5,    5,    0,    -20,  -40},
				new int[]{-30,  5,    10,   15,   15,   10,   5,    -30},
				new int[]{-30,  0,    15,   20,   20,   15,   0,    -30},
				new int[]{-30,  5,    15,   20,   20,   15,   5,    -30},
				new int[]{-30,  0,    10,   15,   15,   10,   0,    -30},
				new int[]{-40,  -20,  0,    0,    0,    0,    -20,  -40},
				new int[]{-50,  -40,  -30,  -30,  -30,  -30,  -40,  -50} };

		// encourage bishops to avoid the corners and borders
		private static final int[][] BISHOP_TABLE = new int[][] {
				new int[]{-20,  -10,  -10,  -10,  -10,  -10,  -10,  -20},
				new int[]{-10,  5,    0,    0,    0,    0,    5,    -10},
				new int[]{-10,  10,   10,   10,   10,   10,   10,   -10},
				new int[]{-10,  0,    10,   10,   10,   10,   0,    -10},
				new int[]{-10,  5,    5,    10,   10,   5,    5,    -10},
				new int[]{-10,  0,    5,    10,   10,   5,    0,    -10},
				new int[]{-10,  0,    0,    0,    0,    0,    0,    -10},
				new int[]{-20,  -10,  -10,  -10,  -10,  -10,  -10,  -20 } };

		// encourage rook to centralize and occupy 7th rank
		// encourage rook to castle
		private static final int[][] ROOK_TABLE = new int[][] {
				new int[]{0,    0,    0,    0,    0,    0,    0,    0},
				new int[]{-5,   0,    0,    0,    0,    0,    0,    -5},
				new int[]{-5,   0,    0,    0,    0,    0,    0,    -5},
				new int[]{-5,   0,    0,    0,    0,    0,    0,    -5},
				new int[]{-5,   0,    0,    0,    0,    0,    0,    -5},
				new int[]{-5,   0,    0,    0,    0,    0,    0,    -5},
				new int[]{5,    10,   10,   10,   10,   10,   10,   5},
				new int[]{0,    0,    0,    5,    5,    0,    0,    0} };

		// mark central squares to keep the queen in the center
		private static final int[][] QUEEN_TABLE = new int[][] {
				new int[]{-20,  -10,  -10,  -5,   -5,   -10,  -10,  -20},
				new int[]{-10,  0,    5,    0,    0,    0,    0,    -10},
				new int[]{-10,  5,    5,    5,    5,    5,    0,    -10},
				new int[]{0,    0,    5,    5,    5,    5,    0,    -5},
				new int[]{-5,   0,    5,    5,    5,    5,    0,    -5},
				new int[]{-10,  0,    5,    5,    5,    5,    0,    -10},
				new int[]{-10,  0,    0,    0,    0,    0,    0,    -10},
				new int[]{-20,  -10,  -10,  -5,   -5,   -10,  -10,  -20} };

		// make the king stand behind the pawn shelter
		private static final int[][] KING_TABLE_START = new int[][] {
				new int[]{20,   30,   10,   0,   0,   10,   30,   20},
				new int[]{20,   20,   0,    0,   0,   0,    20,   20},
				new int[]{-10,  -20,  -20,  -20, -20, -20,  -20,  -10},
				new int[]{-20,  -30,  -30,  -40, -40, -30,  -30,  -20},
				new int[]{-30,  -40,  -40,  -50, -50, -40,  -40,  -30},
				new int[]{-30,  -40,  -40,  -50, -50, -40,  -40,  -30},
				new int[]{-30,  -40,  -40,  -50, -50, -40,  -40,  -30},
				new int[]{-30,  -40,  -40,  -50, -50, -40,  -40,  -30} };

		// king table for the end game
		private static final int[][] KING_TABLE_END = new int[][]{
				new int[]{-50,  -30,  -30,  -30,  -30,  -30,  -30,  -50},
				new int[]{-30,  -30,  0,    0,    0,    0,    -30,  -30},
				new int[]{-30,  -10,  20,   30,   30,   20,   -10,  -30},
				new int[]{-30,  -10,  30,   40,   40,   30,   -10,  -30},
				new int[]{-30,  -10,  30,   40,   40,   30,   -10,  -30},
				new int[]{-30,  -10,  20,   30,   30,   20,   -10,  -30},
				new int[]{-30,  -20,  -10,  0,    0,    -10,  -20,  -30},
				new int[]{-50,  -40,  -30,  -20,  -20,  -30,  -40,  -50} };


		// function to determine position for each piece in center control
		public static int pieceTablePosition(DFSTreeNode node, Piece piece) {
			// obtain the current position of the piece
			Coordinate position = piece.getCurrentPosition(node.getGame().getBoard());

			// obtain the type of the piece
			PieceType type = piece.getType();

			// if the piece is a pawn
			if (type == PieceType.PAWN) {
				return PAWN_TABLE[position.getYPosition() - 1][position.getXPosition() - 1];
			}
			// if the piece is a knight
			else if (type == PieceType.KNIGHT) {
				return KNIGHT_TABLE[position.getYPosition() - 1][position.getXPosition() - 1];
			}
			// if the piece is a bishop
			else if (type == PieceType.BISHOP) {
				return BISHOP_TABLE[position.getYPosition() - 1][position.getXPosition() - 1];
			}
			// if the piece is a rook
			else if (type == PieceType.ROOK) {
				return ROOK_TABLE[position.getYPosition() - 1][position.getXPosition() - 1];
			}
			// if the piece is a queen
			else if (type == PieceType.QUEEN) {
				return QUEEN_TABLE[position.getYPosition() - 1][position.getXPosition() - 1];
			}
			// if the piece is a king
			else // (type == PieceType.KING) {
				return KING_TABLE_START[position.getYPosition() - 1][position.getXPosition() - 1];
		}


		// function to determine the center control for specific pieces
		public static int pieceCenterControl(DFSTreeNode node, Set<Piece> pieces) {
			// initialize the center control
			int centerControl = 0;

			// iterate over the pieces in the set
			for (Piece piece : pieces) {
				// obtain each piece position in the table
				centerControl += pieceTablePosition(node, piece);
			}
			return centerControl;
		}
		
		
	// function to determine pawn formation -- avoid weaknesses
	public static int pawnStructure(DFSTreeNode node) {
		int pawnFormation = 0; // initialize pawn formation counter

		for (Piece piece : node.getGame().getBoard().getPieces(node.getGame().getCurrentPlayer())) {
			// iterate over each own piece on the board
			if (piece.getType() == PieceType.PAWN) {
				// if the piece is a pawn, check its position
				Coordinate pawnPosition = node.getGame().getCurrentPosition(piece);


				// doubled pawn structure
				// check if multiple own pawns are in the same column
				for (Piece otherPiece : node.getGame().getBoard().getPieces(node.getGame().getCurrentPlayer())) {
					if (otherPiece.getType() == PieceType.PAWN) {
						// if the other piece is a pawn, check its position
						Coordinate otherPawnPosition = node.getGame().getCurrentPosition(otherPiece);

						// if different own pawns are NOT in the same column, add to the pawn formation counter
						if (!pawnPosition.equals(otherPawnPosition) && pawnPosition.getXPosition() != otherPawnPosition.getXPosition()) {
							pawnFormation++;
						}
					}
				}


				// passed pawns
				// pawns which have advanced so far that they can no longer be attacked by enemy pawns
				for (Piece enemyPiece : node.getGame().getBoard().getPieces(node.getGame().getOtherPlayer())) {
					if(enemyPiece.getType() == PieceType.PAWN) {
						// if the enemy peice is a pawn, check its position
						Coordinate enemyPawnPosition = node.getGame().getCurrentPosition(enemyPiece);

						// no enemy pawns are in front of or on adjacent files (columns) that can stop own pawn from being promoted
						if (enemyPawnPosition.getXPosition() == pawnPosition.getXPosition() + 1 || enemyPawnPosition.getXPosition() == pawnPosition.getXPosition() - 1) {
							if (enemyPawnPosition.getYPosition() < pawnPosition.getYPosition()) {
								pawnFormation++;
							}
						}
					}
				}
			}
		}
		return pawnFormation;
	}
	public static boolean isControlled(DFSTreeNode node, Coordinate position, Set<Piece> pieces) {
		
		for(Piece piece : pieces) {
			
			List<Move> captureMoves = piece.getAllCaptureMoves(node.getGame());
			
			for (Move move : captureMoves) {
				CaptureMove move_c = (CaptureMove) move;
				
				int pieceID = move_c.getTargetPieceID();
				Piece enemyPiece = node.getGame().getBoard().getPiece(node.getGame().getOtherPlayer(), pieceID);
				Coordinate enemyPiecePosition = enemyPiece.getCurrentPosition(node.getGame().getBoard());
				
				if (enemyPiecePosition.equals(position)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static int oppMaterialValue(DFSTreeNode node, Set<Piece>pieces) {
		int material = 0;
		
		for(Piece piece : pieces)
		{
			if (piece.getType() != PieceType.KING) {
				material += Piece.getPointValue(piece.getType());
			}
			
		}
		return material;
	}
	
	public static int ourMaterialValue(DFSTreeNode node, Set<Piece>pieces) {
		int material = 0;
		
		for(Piece piece : pieces)
		{
			if (piece.getType() != PieceType.KING) {
				material += Piece.getPointValue(piece.getType());
			}
			
			
		}
		return material;
	}
	
	public static int levelOfDevelopment(DFSTreeNode node, Set<Piece>pieces) {
		int level = 0;
		for(Piece piece : pieces)
		{
			
			PieceType pieceType = piece.getType();
			Coordinate currentPosition = piece.getCurrentPosition(node.getGame().getBoard());

			switch(pieceType) {
			
			case BISHOP:
				Coordinate startingPosition1 = new Coordinate(3,1);
				Coordinate startingPosition2 = new Coordinate(6,1);

				if (!currentPosition.equals(startingPosition2) || !currentPosition.equals(startingPosition1)) {
					level += 3;
				}
				
				break;
			case KNIGHT:
				Coordinate NstartingPosition1 = new Coordinate(2,1);
				Coordinate NstartingPosition2 = new Coordinate(7,1);

				if (!currentPosition.equals(NstartingPosition1) || !currentPosition.equals(NstartingPosition2)) {
					level += 3;
				}
				
				break;
			case ROOK:
				Coordinate RstartingPosition1 = new Coordinate(1,1);
				Coordinate RstartingPosition2 = new Coordinate(8,1);

				if (!currentPosition.equals(RstartingPosition1) || !currentPosition.equals(RstartingPosition2)) {
					level += 3;
				}
				break;
			default:
				break;
			}
			
		}
		return level;
	}
	
	// function to determine control of the center of the board
		public static int centerControl(DFSTreeNode node, Set<Piece>pieces) {
			int centerControl = 0; // initialize center control counter

			// check if own piece is in the center of the board
			for (Piece piece : pieces) {
				// iterate over each own piece on the board
				Coordinate piecePosition = node.getGame().getCurrentPosition(piece);

				// check if own piece is in the center of the board
				if (piecePosition.getXPosition() >= 3 && piecePosition.getXPosition() <= 6) {
					// outermost perimeter of the center
					if (piecePosition.getYPosition() == 3 || piecePosition.getYPosition() == 6) {
						centerControl++;
					}
					// middle perimeter of the center
					if (piecePosition.getYPosition() == 4 || piecePosition.getYPosition() == 5) {
						centerControl = centerControl + 2;
					}
					// innermost perimeter of the center
					if ((piecePosition.getXPosition() == 4 || piecePosition.getXPosition() == 5) && (piecePosition.getYPosition() == 4 || piecePosition.getYPosition() == 5)) {
						centerControl = centerControl + 3;
					}
				}
			}
			return centerControl;
		}

	
	public static class OffensiveHeuristics extends Object
	{

		public static int materialDifference(DFSTreeNode node, Set<Piece> ourPieces, Set<Piece> opponentPieces) {
			return ourMaterialValue(node, ourPieces) - oppMaterialValue(node, opponentPieces);
		}
		
		
		/*
		 * public static int getOurControllingSquares(DFSTreeNode node) { int
		 * numControllingSquares = 0; for(Piece piece :
		 * node.getGame().getBoard().getPieces(node.getGame().getCurrentPlayer()))
		 * 
		 * {
		 * 
		 * numControllingSquares += piece.getControllingSquares(node.getGame()); }
		 * 
		 * return numControllingSquares;
		 * 
		 * }
		 */
		
		public static int getNumberOfPiecesWeAreThreatening(DFSTreeNode node, Set<Piece>pieces)
		{
			// Instead of just adding up the number of pieces we are threatening, we should calculate the value of 
			// the pieces we are threatening. This way, a board where we are a threatening one queen can get picked
			// over a board where we are threatening just one pawn 		
			
			List<Move> captureMoves = new ArrayList<Move>();
			
			int numPiecesWeAreThreatening = 0;
			for(Piece piece : pieces)
				
			{
				 		
				 captureMoves.addAll(piece.getAllCaptureMoves(node.getGame()));	
			}
			
			for (Move move : captureMoves) {
				CaptureMove move_c = (CaptureMove) move;
				
				int pieceID = move_c.getTargetPieceID();
				
				Piece enemyPiece = node.getGame().getBoard().getPiece(node.getGame().getOtherPlayer(), pieceID);
				
				numPiecesWeAreThreatening += Piece.getPointValue(enemyPiece.getType());
			}
			
			
			
			
			return numPiecesWeAreThreatening;
		}

	}
	
	public static double getOffensiveHeuristicValue(DFSTreeNode node, Set<Piece>pieces)
	{
		// remember the action has already taken affect at this point, so capture moves have already resolved
		// and the targeted piece will not exist inside the game anymore.
		// however this value was recorded in the amount of points that the player has earned in this node
		double damageDealtInThisNode = node.getGame().getBoard().getPointsEarned(node.getGame().getCurrentPlayer());

		switch(node.getMove().getType())
		{
		case PROMOTEPAWNMOVE:
			PromotePawnMove promoteMove = (PromotePawnMove)node.getMove();
			damageDealtInThisNode += Piece.getPointValue(promoteMove.getPromotedPieceType());
			break;
		default:
			break;
		}
		// offense can typically include the number of pieces that our pieces are currently threatening
		int numPiecesWeAreThreatening = OffensiveHeuristics.getNumberOfPiecesWeAreThreatening(node, pieces);

		return damageDealtInThisNode + numPiecesWeAreThreatening;
	}
	
	public static class DefensiveHeuristics extends Object
	{

		public static int getNumberOfAlivePieces(DFSTreeNode node)
		{
			int numPiecesAlive = 0;
			for(PieceType pieceType : PieceType.values())
			{
				numPiecesAlive += node.getGame().getNumberOfAlivePieces(node.getGame().getCurrentPlayer(), pieceType);
			}
			return numPiecesAlive;
		}

		public static int getClampedPieceValueTotalSurroundingKing(DFSTreeNode node)
		{
			// what is the state of the pieces next to the king? add up the values of the neighboring pieces
			// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
			int kingSurroundingPiecesValueTotal = 0;

			Piece kingPiece = node.getGame().getBoard().getPieces(node.getGame().getCurrentPlayer(), PieceType.KING).iterator().next();
			Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
			for(Direction direction : Direction.values())
			{
				Coordinate neightborPosition = kingPosition.getNeighbor(direction);
				if(node.getGame().getBoard().isInbounds(neightborPosition) && node.getGame().getBoard().isPositionOccupied(neightborPosition))
				{
					Piece piece = node.getGame().getBoard().getPieceAtPosition(neightborPosition);
					int pieceValue = Piece.getPointValue(piece.getType());
					if(piece != null && kingPiece.isEnemyPiece(piece))
					{
						kingSurroundingPiecesValueTotal -= pieceValue;
					} else if(piece != null && !kingPiece.isEnemyPiece(piece))
					{
						kingSurroundingPiecesValueTotal += pieceValue;
					}
				}
			}
			// kingSurroundingPiecesValueTotal cannot be < 0 b/c the utility of losing a game is 0, so all of our utility values should be at least 0
			kingSurroundingPiecesValueTotal = Math.max(kingSurroundingPiecesValueTotal, 0);
			return kingSurroundingPiecesValueTotal;
		}

		public static int getNumberOfPiecesThreateningUs(DFSTreeNode node, Set<Piece>pieces)
		{
			// how many pieces are threatening us?
			
			List<Move> captureMoves = new ArrayList<Move>();

			int numPiecesThreateningUs = 0;
			for(Piece piece : pieces)
			{
				// Same as numPiecesWeAreThreatening
				captureMoves.addAll(piece.getAllCaptureMoves(node.getGame()));
		
			}
			

			for (Move move : captureMoves) {
				CaptureMove move_c = (CaptureMove) move;
				
				int pieceID = move_c.getTargetPieceID();
				
				Piece enemyPiece = node.getGame().getBoard().getPiece(node.getGame().getCurrentPlayer(), pieceID);
				
				numPiecesThreateningUs += Piece.getPointValue(enemyPiece.getType());
			}
			
			
			return numPiecesThreateningUs;
		}
		
	}
	public static double getDefensiveHeuristicValue(DFSTreeNode node, Set<Piece>pieces)
	{
		// how many pieces exist on our team?
		int numPiecesAlive = DefensiveHeuristics.getNumberOfAlivePieces(node);

		// what is the state of the pieces next to the king? add up the values of the neighboring pieces
		// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
		int kingSurroundingPiecesValueTotal = DefensiveHeuristics.getClampedPieceValueTotalSurroundingKing(node);

		// how many pieces are threatening us?
		int numPiecesThreateningUs = DefensiveHeuristics.getNumberOfPiecesThreateningUs(node, pieces);

		return (numPiecesAlive + kingSurroundingPiecesValueTotal) - numPiecesThreateningUs;
	}
	
	public static double getNonlinearPieceCombinationHeuristicValue(DFSTreeNode node)
	{
		// both bishops are worth more together than a single bishop alone
		// same with knights...we want to encourage keeping pairs of elements
		double multiPieceValueTotal = 0.0;

		double exponent = 1.5; // f(numberOfKnights) = (numberOfKnights)^exponent

		// go over all the piece types that have more than one copy in the game (including pawn promotion)
		for(PieceType pieceType : new PieceType[] {PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN})
		{
			multiPieceValueTotal += Math.pow(node.getGame().getNumberOfAlivePieces(node.getGame().getCurrentPlayer(), pieceType), exponent);
		}

		return multiPieceValueTotal;
	}
	
	/*
	 * // moves between the maximizing player and minimizing player go back and
	 * forth // incentivize the maximizing player to choose the second best move
	 * public static double incentivizeSecondBestMove(DFSTreeNode node) { // look at
	 * the parent of the parent DFSTreeNode minimizePlayer = node.getParent();
	 * DFSTreeNode maximizePlayerParent = minimizePlayer.getParent();
	 * 
	 * // obtain all moves from the parent L<Move> moves =
	 * maximizePlayerParent.getGame().getAllMoves(getMaxPlayer(node));
	 * 
	 * // obtain the next best move from the parent Move nextBestMove = null;
	 * 
	 * for (Move move : moves) { if (move.equals(maximizePlayerParent.getMove())) {
	 * continue; } nextBestMove = move; break; }
	 * 
	 * // get the heuristic value of the next best move double heuristicValue =
	 * getHeuristicValue(maximizePlayerParent);
	 * 
	 * // return the heuristic value of the next best move return heuristicValue; }
	 */
	
	public static double getHeuristicValue(DFSTreeNode node)
	{
		
		Set<Piece> opponentPieces = node.getGame().getBoard().getPieces(getMinPlayer(node));
		Set<Piece> myPieces = node.getGame().getBoard().getPieces(getMaxPlayer(node));
		
		int centerControl = pieceCenterControl(node, myPieces);

		int materialDifference = CustomHeuristics.OffensiveHeuristics.materialDifference(node, myPieces, opponentPieces);
		int development = levelOfDevelopment(node, myPieces);
		double offenseHeuristicValue = CustomHeuristics.getOffensiveHeuristicValue(node, myPieces);
		double defenseHeuristicValue = CustomHeuristics.getDefensiveHeuristicValue(node, opponentPieces);
		double nonlinearHeuristicValue = CustomHeuristics.getNonlinearPieceCombinationHeuristicValue(node);
		int pawnStructure = pawnStructure(node);
		
		//int secondBest = (int) CustomHeuristics.incentivizeSecondBestMove(node);
		
		return development + offenseHeuristicValue + defenseHeuristicValue + 2*materialDifference + centerControl + pawnStructure; //+ secondBest;
	}

	
	
}
