package nl.tno.willemsph.infra.roadsection;

public class MultiLineString {
	private Coordinate coordinate;

	public MultiLineString() {
	}

	public MultiLineString(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

}
