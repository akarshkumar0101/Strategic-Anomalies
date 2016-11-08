package game.board;

import java.util.ArrayList;
import java.util.List;

public class Path {
	private final Coordinate startCoor;
	private final List<Coordinate> pathCoordinates;

	public Path(Coordinate startCoor) {
		this(10, startCoor);
	}

	public Path(int len, Coordinate startCoor) {
		pathCoordinates = new ArrayList<>(len);
		this.startCoor = startCoor;
	}

	public Path(Path another) {
		pathCoordinates = new ArrayList<>(another.pathCoordinates);
		startCoor = another.startCoor;
	}

	public void addCoordinate(Coordinate coor) {
		pathCoordinates.add(coor);
	}

	public void addPath(Path another) {
		for (Coordinate coor : another.pathCoordinates) {
			pathCoordinates.add(coor);
		}
	}

	public Coordinate getStartCoor() {
		return startCoor;
	}

	public Coordinate getEndCoor() {
		return pathCoordinates.get(pathCoordinates.size()-1);
	}

	public List<Coordinate> getPathCoordinates() {
		return pathCoordinates;
	}
	@Override
	public String toString(){
		String str ="Path: {";
		
		for(Coordinate coor:pathCoordinates){
			str+= coor+" ";
		}
		
		return str+"}";
	}
}
