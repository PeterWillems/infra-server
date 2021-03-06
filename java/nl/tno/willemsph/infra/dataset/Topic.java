package nl.tno.willemsph.infra.dataset;

import java.net.URI;

public class Topic {
	private URI uri;
	private String label;

	public Topic() {
	}

	public Topic(URI uri, String label) {
		this.uri = uri;
		this.label = label;
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

}
