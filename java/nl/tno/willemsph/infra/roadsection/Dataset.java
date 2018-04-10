package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class Dataset {
	public static final String META_NAMESPACE = "https://w3id.org/meta#";

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
	}

	public enum Format {
		CSV, XLS, PDF, SPFF, XML, JSON, JSON_LD, TTL;

		public static Format translate(URI format) {
			switch (format.toString()) {
			case META_NAMESPACE + "CSV":
				return CSV;
			case META_NAMESPACE + "XLS":
				return XLS;
			case META_NAMESPACE + "PDF":
				return PDF;
			case META_NAMESPACE + "SPFF":
				return SPFF;
			case META_NAMESPACE + "XML":
				return XML;
			case META_NAMESPACE + "JSON":
				return JSON;
			case META_NAMESPACE + "JSON_LD":
				return JSON_LD;
			case META_NAMESPACE + "TTL":
				return TTL;
			}
			return null;
		}

	}

	private String datasetLabel;
	private URI dataReference;
	private String contactLabel;
	private String infraLabel;
	private String road;
	private String way;
	private Double start;
	private Double end;
	private DecimalSymbol decimalSymbol;
	private Separator separator;
	private Format format;
	private String ownerLabel;

	public Dataset() {
	}

	public Dataset(String datasetLabel, URI dataReference, URI decimalSymbol, URI separator, URI format,
			String ownerLabel, String contactLabel, String infraLabel, String road, String way, Double start,
			Double end) {
		this.datasetLabel = datasetLabel;
		this.dataReference = dataReference;
		this.ownerLabel = ownerLabel;
		this.contactLabel = contactLabel;
		this.infraLabel = infraLabel;
		this.road = road;
		this.way = way;
		this.start = start;
		this.end = end;
		this.decimalSymbol = DecimalSymbol.translate(decimalSymbol);
		this.separator = Separator.translate(separator);
		this.format = Format.translate(format);
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

	public String getContactLabel() {
		return contactLabel;
	}

	public void setContactLabel(String contactLabel) {
		this.contactLabel = contactLabel;
	}

	public String getInfraLabel() {
		return infraLabel;
	}

	public void setInfraLabel(String infraLabel) {
		this.infraLabel = infraLabel;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getEnd() {
		return end;
	}

	public void setEnd(Double end) {
		this.end = end;
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

	public String getOwnerLabel() {
		return ownerLabel;
	}

	public void setOwnerLabel(String ownerLabel) {
		this.ownerLabel = ownerLabel;
	}

}
