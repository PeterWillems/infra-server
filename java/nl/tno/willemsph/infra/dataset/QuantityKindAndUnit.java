package nl.tno.willemsph.infra.dataset;

import java.net.URI;

public class QuantityKindAndUnit {
	private URI uri;
	private String label;
	private String quantityReference;
	private String unitReference;

	public QuantityKindAndUnit() {
	}

	public QuantityKindAndUnit(URI uri, String label, String quantityReference, String unitReference) {
		this.uri = uri;
		this.label = label;
		this.quantityReference = quantityReference;
		this.unitReference = unitReference;
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

	public String getQuantityReference() {
		return quantityReference;
	}

	public void setQuantityReference(String quantityReference) {
		this.quantityReference = quantityReference;
	}

	public String getUnitReference() {
		return unitReference;
	}

	public void setUnitReference(String unitReference) {
		this.unitReference = unitReference;
	}

}
