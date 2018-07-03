package nl.tno.willemsph.infra.dataset;

import java.net.URI;

public class Person {
	private URI uri;
	private String label;
	private String eMailAddress;
	private URI worksFor;

	public Person() {
	}

	public Person(URI uri, String label, String eMailAddress, URI worksFor) {
		this.uri = uri;
		this.label = label;
		this.eMailAddress = eMailAddress;
		this.worksFor = worksFor;
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

	public String geteMailAddress() {
		return eMailAddress;
	}

	public void seteMailAddress(String eMailAddress) {
		this.eMailAddress = eMailAddress;
	}

	public URI getWorksFor() {
		return worksFor;
	}

	public void setWorksFor(URI worksFor) {
		this.worksFor = worksFor;
	}

}
