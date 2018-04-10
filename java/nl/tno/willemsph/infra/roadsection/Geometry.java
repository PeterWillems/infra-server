package nl.tno.willemsph.infra.roadsection;

import java.util.List;

public class Geometry {
	private List<MultiLineString> multiLineStrings;

	public Geometry() {
	}

	public List<MultiLineString> getMultiLineString() {
		return multiLineStrings;
	}

	public void setMultiLineString(List<MultiLineString> multiLineStrings) {
		this.multiLineStrings = multiLineStrings;
	}

}
