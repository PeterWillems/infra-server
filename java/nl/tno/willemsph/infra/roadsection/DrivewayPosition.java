package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class DrivewayPosition implements RoadSectionPropertyValue {

	private URI uri;
	private String labelEn;
	private String labelNl;
	private String drivewayPositionCode;

	public DrivewayPosition() {
	}

	public DrivewayPosition(URI uri, String labelEn, String labelNl, String drivewayPositionCode) {
		this.setUri(uri);
		this.setLabelEn(labelEn);
		this.setLabelNl(labelNl);
		this.setDrivewayPositionCode(drivewayPositionCode);
	}

	@Override
	public String getValue() {
		return getDrivewayPositionCode();
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

	public String getDrivewayPositionCode() {
		return drivewayPositionCode;
	}

	public void setDrivewayPositionCode(String drivewayPositionCode) {
		this.drivewayPositionCode = drivewayPositionCode;
	}

}
