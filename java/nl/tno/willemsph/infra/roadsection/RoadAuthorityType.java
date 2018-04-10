package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class RoadAuthorityType implements RoadSectionPropertyValue {
	private URI uri;
	private String labelEn;
	private String labelNl;
	private String roadAuthorityTypeCode;

	public RoadAuthorityType() {
	}

	public RoadAuthorityType(URI uri, String labelEn, String labelNl, String roadAuthorityTypeCode) {
		this.uri = uri;
		this.labelEn = labelEn;
		this.labelNl = labelNl;
		this.roadAuthorityTypeCode = roadAuthorityTypeCode;
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

	public String getRoadAuthorityTypeCode() {
		return roadAuthorityTypeCode;
	}

	public void setRoadAuthorityTypeCode(String roadAuthorityTypeCode) {
		this.roadAuthorityTypeCode = roadAuthorityTypeCode;
	}

	@Override
	public String getValue() {
		return getRoadAuthorityTypeCode();
	}

}
