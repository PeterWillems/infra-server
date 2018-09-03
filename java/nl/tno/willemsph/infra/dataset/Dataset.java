package nl.tno.willemsph.infra.dataset;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

public class Dataset {
	public static final String META_NAMESPACE = "https://w3id.org/meta#";
	public static final String SPARONTOLOGIES_NAMESPACE = "http://www.sparontologies.net/mediatype/text/";

	public enum DecimalSymbol {
		COMMA, DOT;

		public static DecimalSymbol translate(URI decimalSymbol) {
			switch (decimalSymbol.toString()) {
			case META_NAMESPACE + "COMMA":
				return COMMA;
			case META_NAMESPACE + "DOT":
				return DOT;
			}
			return null;
		}

		public URI getUri() throws URISyntaxException {
			return new URI(META_NAMESPACE + name());
		}
	}

	public enum Separator {
		COMMA, SEMICOLON, DOT, SPACE;

		public static Separator translate(URI separator) {
			switch (separator.toString()) {
			case META_NAMESPACE + "COMMA":
				return COMMA;
			case META_NAMESPACE + "SEMICOLON":
				return SEMICOLON;
			case META_NAMESPACE + "DOT":
				return DOT;
			case META_NAMESPACE + "SPACE":
				return SPACE;
			}
			return null;
		}

		public URI getUri() throws URISyntaxException {
			return new URI(META_NAMESPACE + name());
		}
	}

	public enum Format {
		CSV, XLS, PDF, SPFF, XML, JSON, JSON_LD, TTL;

		public static Format translate(URI format) {
			switch (format.toString()) {
			case SPARONTOLOGIES_NAMESPACE + "csv":
				return CSV;
			case SPARONTOLOGIES_NAMESPACE + "xls":
				return XLS;
			case SPARONTOLOGIES_NAMESPACE + "pdf":
				return PDF;
			case SPARONTOLOGIES_NAMESPACE + "spff":
				return SPFF;
			case SPARONTOLOGIES_NAMESPACE + "xml":
				return XML;
			case SPARONTOLOGIES_NAMESPACE + "json":
				return JSON;
			case SPARONTOLOGIES_NAMESPACE + "json_ld":
				return JSON_LD;
			case SPARONTOLOGIES_NAMESPACE + "ttl":
				return TTL;
			}
			return null;
		}

		public URI getUri() throws URISyntaxException {
			return new URI(SPARONTOLOGIES_NAMESPACE + name());
		}

	}

	private String datasetUri;
	private String datasetLabel;
	private URI dataReference;
	private URI project;
	private String projectLabel;
	private URI contact;
	private String contactLabel;
	private List<InfraObject> infraObjects;
	private DecimalSymbol decimalSymbol;
	private Separator separator;
	private Format format;
	private URI organisation;
	private String ownerLabel;
	private Date measurementEndDate;
	private Date measurementStartDate;
	private List<String> measurementYears;
	private List<QuantityKindAndUnit> quantityKindAndUnits;
	private URI topic;
	private String topicLabel;

	public Dataset() {
	}

	public Dataset(String datasetUri, String datasetLabel, URI dataReference, Date measurementStartDate,
			Date measurementEndDate, URI decimalSymbol, URI separator, URI format, URI project, String projectLabel,
			URI organisation, String ownerLabel, URI topic, String topicLabel, URI contact, String contactLabel) {
		this.datasetUri = datasetUri;
		this.datasetLabel = datasetLabel;
		this.dataReference = dataReference;
		this.measurementStartDate = measurementStartDate;
		this.measurementEndDate = measurementEndDate;
		this.project = project;
		this.projectLabel = projectLabel;
		this.organisation = organisation;
		this.ownerLabel = ownerLabel;
		this.topic = topic;
		this.topicLabel = topicLabel;
		this.contact = contact;
		this.contactLabel = contactLabel;
		this.decimalSymbol = DecimalSymbol.translate(decimalSymbol);
		this.separator = Separator.translate(separator);
		this.format = Format.translate(format);
	}

	public String getDatasetUri() {
		return datasetUri;
	}

	public void setDatasetUri(String datasetUri) {
		this.datasetUri = datasetUri;
	}

	public String getDatasetLabel() {
		return datasetLabel;
	}

	public void setDatasetLabel(String datasetLabel) {
		this.datasetLabel = datasetLabel;
	}

	public URI getDataReference() {
		return dataReference;
	}

	public void setDataReference(URI dataReference) {
		this.dataReference = dataReference;
	}

	public URI getProject() {
		return project;
	}

	public void setProject(URI project) {
		this.project = project;
	}

	public String getProjectLabel() {
		return projectLabel;
	}

	public void setProjectLabel(String projectLabel) {
		this.projectLabel = projectLabel;
	}

	public URI getContact() {
		return contact;
	}

	public void setContact(URI contact) {
		this.contact = contact;
	}

	public String getContactLabel() {
		return contactLabel;
	}

	public void setContactLabel(String contactLabel) {
		this.contactLabel = contactLabel;
	}

	public DecimalSymbol getDecimalSymbol() {
		return decimalSymbol;
	}

	public void setDecimalSymbol(DecimalSymbol decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	public Separator getSeparator() {
		return separator;
	}

	public void setSeparator(Separator separator) {
		this.separator = separator;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public URI getOrganisation() {
		return organisation;
	}

	public void setOrganisation(URI organisation) {
		this.organisation = organisation;
	}

	public String getOwnerLabel() {
		return ownerLabel;
	}

	public void setOwnerLabel(String ownerLabel) {
		this.ownerLabel = ownerLabel;
	}

	public Date getMeasurementEndDate() {
		return measurementEndDate;
	}

	public void setMeasurementEndDate(Date measurementEndDate) {
		this.measurementEndDate = measurementEndDate;
	}

	public Date getMeasurementStartDate() {
		return measurementStartDate;
	}

	public void setMeasurementStartDate(Date measurementStartDate) {
		this.measurementStartDate = measurementStartDate;
	}

	public List<String> getMeasurementYears() {
		return measurementYears;
	}

	public void setMeasurementYears(List<String> measurementYears) {
		this.measurementYears = measurementYears;
	}

	public URI getTopic() {
		return topic;
	}

	public void setTopic(URI topic) {
		this.topic = topic;
	}

	public String getTopicLabel() {
		return topicLabel;
	}

	public void setTopicLabel(String topicLabel) {
		this.topicLabel = topicLabel;
	}

	public List<QuantityKindAndUnit> getQuantityKindAndUnits() {
		return quantityKindAndUnits;
	}

	public void setQuantityKindAndUnits(List<QuantityKindAndUnit> quantityKindAndUnits) {
		this.quantityKindAndUnits = quantityKindAndUnits;
	}

	public List<InfraObject> getInfraObjects() {
		return infraObjects;
	}

	public void setInfraObjects(List<InfraObject> infraObjects) {
		this.infraObjects = infraObjects;
	}

}
