package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class RelativePosition implements RoadSectionPropertyValue {
	private URI uri;
	private String labelEn;
	private String labelNl;
	private String relativePositionCode;

	public RelativePosition() {
	}

	public RelativePosition(URI uri, String labelEn, String labelNl, String relativePositionCode) {
		this.uri = uri;
		this.labelEn = labelEn;
		this.labelNl = labelNl;
		this.relativePositionCode = relativePositionCode;
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

	public String getRelativePositionCode() {
		return relativePositionCode;
	}

	public void setRelativePositionCode(String relativePositionCode) {
		this.relativePositionCode = relativePositionCode;
	}

	@Override
	public String getValue() {
		return getRelativePositionCode();
	}

}
