package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class RoadSectionProperty {
	private URI uri;
	private String labelEn;
	private String labelNl;
	private URI range;

	public RoadSectionProperty() {
	}

	public RoadSectionProperty(URI uri, String labelEn, String labelNl, URI range) {
		this.setUri(uri);
		this.labelEn = labelEn;
		this.labelNl = labelNl;
		this.setRange(range);
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getLabelEn() {
		return labelEn;
	}

	public void setLabelEn(String labelEn) {
		this.labelEn = labelEn;
	}

	public String getLabelNl() {
		return labelNl;
	}

	public void setLabelNl(String labelNl) {
		this.labelNl = labelNl;
	}

	public URI getRange() {
		return range;
	}

	public void setRange(URI range) {
		this.range = range;
	}

}
