package nl.tno.willemsph.infra.dataset;

public class InfraObject {
	private String infraLabel;
	private String road;
	private String way;
	private String lane;
	private Double start;
	private Double end;

	public InfraObject() {
	}

	public InfraObject(String infraLabel, String road, String way, String lane, Double start, Double end) {
		this.infraLabel = infraLabel;
		this.road = road;
		this.way = way;
		this.lane = lane;
		this.start = start;
		this.end = end;
	}

	public String getInfraLabel() {
		return infraLabel;
	}

	public void setInfraLabel(String infraLabel) {
		this.infraLabel = infraLabel;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getLane() {
		return lane;
	}

	public void setLane(String lane) {
		this.lane = lane;
	}

	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getEnd() {
		return end;
	}

	public void setEnd(Double end) {
		this.end = end;
	}

}
