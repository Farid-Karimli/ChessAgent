package hw2.agents.moveorder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import hw2.chess.game.Board;
import hw2.chess.game.history.History;
import hw2.chess.game.move.CaptureMove;
import hw2.chess.game.move.Move;
import hw2.chess.game.piece.Piece;
import hw2.chess.search.DFSTreeNode;
import hw2.chess.utils.Coordinate;


public class CustomMoveOrderer
{
	
	/**
	 * By default, I claim that we want to see attacking moves before anything else. However,
	 * this is not a good rule in general, and we may want to make it move-specific OR start incorporating some custom heuristics
	 * @param nodes. The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
	 * @return The ordered nodes.
	 */
	
	static public class CaptureMoveNode {
		private DFSTreeNode node;
		private int targetValue;

		CaptureMoveNode(DFSTreeNode node, int targetValue) {
			this.node = node;
			this.targetValue = targetValue;
		}
		
		public DFSTreeNode getNode() {
			return this.node;
	}
	}
	
	static class CaptureMoveNodeComparator implements Comparator<CaptureMoveNode> {
		
		public int compare(CaptureMoveNode s1, CaptureMoveNode s2) {
			if (s1.targetValue < s2.targetValue)
				return 1;
			else if (s1.targetValue > s2.targetValue)
				return -1;
							return 0;
			}
	}
	
	public static boolean hasMoveOccurred(Move move) {
		return false;
	}
	
	
	public static List<DFSTreeNode> order(List<DFSTreeNode> nodes)
	{
		// by default get the CaptureMoves first
		List<DFSTreeNode> captureNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> castleNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> otherNodes = new LinkedList<DFSTreeNode>();
		
		PriorityQueue<CaptureMoveNode> pq = new PriorityQueue<CaptureMoveNode>(10, new CaptureMoveNodeComparator());
		
		
		// ArrayList history = new ArrayList(History.getStack());
		
		
		for(DFSTreeNode node : nodes)
		{
			if(node.getMove() != null)
			{
				switch(node.getMove().getType())
				{
				case CAPTUREMOVE:
					
					CaptureMove move = (CaptureMove) node.getMove();
					Board parentBoard = node.getParent().getGame().getBoard();
					
					Piece actorPiece = node.getGame().getBoard().getPiece(move.getAttackingPlayer(), move.getAttackingPieceID());
					Coordinate actorPiecePosition = actorPiece.getCurrentPosition(node.getGame().getBoard());
					//System.out.println("Actor" + actorPiece);

					Piece attackedPiece = parentBoard.getPieceAtPosition(actorPiecePosition);
					//System.out.println("Attacked" + attackedPiece);

					int targetPieceValue = Piece.getPointValue(attackedPiece.getType());
					
					pq.add(new CaptureMoveNode(node, targetPieceValue));
					
					
					//captureNodes.add(node);
					break;
				case CASTLEMOVE:
					castleNodes.add(node);
					break;
				default:
					otherNodes.add(node);
					break;
				}
			} else
			{
				otherNodes.add(node);
			}
		}
		
		
		while (!pq.isEmpty()) {
			captureNodes.add(pq.poll().getNode());
		}
		
		captureNodes.addAll(castleNodes);
		captureNodes.addAll(otherNodes);
		
		return captureNodes;
	}

}
