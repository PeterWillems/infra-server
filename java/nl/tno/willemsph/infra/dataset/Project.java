package nl.tno.willemsph.infra.dataset;

import java.net.URI;

public class Project {
	private URI uri;
	private String label;

	public Project() {
	}

	public Project(URI uri, String label) {
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
