package nl.tno.willemsph.infra.dataset;

import java.net.URI;

public class InfraObject {
	private URI uri;
	private String label;
	private String road;
	private String way;
	private String lane;
	private Double start;
	private Double end;

	public InfraObject() {
	}

	public InfraObject(URI uri, String label, String road, String way, String lane, Double start, Double end) {
		this.uri = uri;
		this.label = label;
		this.road = road;
		this.way = way;
		this.lane = lane;
		this.start = start;
		this.end = end;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
