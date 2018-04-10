package nl.tno.willemsph.infra.roadsection;

public class Coordinate {
	double lat;
	double lng;
	double x;
	double y;

	public Coordinate() {
	}

	public Coordinate(double lat, double lng, double x, double y) {
		this.lat = lat;
		this.lng = lng;
		this.x = x;
		this.y = y;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
