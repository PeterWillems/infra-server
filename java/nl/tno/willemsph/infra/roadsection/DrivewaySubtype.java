package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class DrivewaySubtype implements RoadSectionPropertyValue {

	private URI uri;
	private String labelEn;
	private String labelNl;
	private String drivewaySubtypeCode;

	public DrivewaySubtype() {
	}

	public DrivewaySubtype(URI uri, String labelEn, String labelNl, String drivewaySubtypeCode) {
		this.setUri(uri);
		this.setLabelEn(labelEn);
		this.setLabelNl(labelNl);
		this.drivewaySubtypeCode = drivewaySubtypeCode;
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

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getDrivewaySubtypeCode() {
		return drivewaySubtypeCode;
	}

	public void setDrivewaySubtypeCode(String drivewaySubtypeCode) {
		this.drivewaySubtypeCode = drivewaySubtypeCode;
	}

	@Override
	public String getValue() {
		return getDrivewaySubtypeCode();
	}

}
