package nl.tno.willemsph.infra.dataset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.infra.SparqlService;

@Service
public class DatasetService {
	public static final String META = "https://w3id.org/meta#";
	public static final String METADATA = "https://w3id.org/metadata#";
	public static final String DCE = "http://purl.org/dc/elements/1.1/";

	@Autowired
	private SparqlService fuseki;

	public List<Dataset> getAllDatasets() throws IOException, URISyntaxException {
		List<Dataset> datasets = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append(
				"SELECT DISTINCT ?dataset ?datasetLabel ?dataReference ?measurementStartDate ?measurementEndDate ?project ?projectLabel ?organisation ?ownerLabel ?topic ?topicLabel ?contact ?contactLabel ?decimalSymbol ?separator ?format ?infraObject ?infraLabel ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdf:type meta:File .");
		queryStr.append("  ?dataset rdfs:label ?datasetLabel .");
		queryStr.append("  OPTIONAL {    ?dataset meta:csvDecimalSymbol ?decimalSymbol . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:csvSeparatorSymbol ?separator . } ");
		queryStr.append("  OPTIONAL {    ?dataset dce:format ?format . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:dataReference ?dataReference . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:measurementStartDate ?measurementStartDate . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:measurementEndDate ?measurementEndDate . } ");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("    ?dataset meta:forProject ?project . ");
		queryStr.append("    ?project rdfs:label ?projectLabel . ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("    ?dataset meta:hasOwner ?organisation . ");
		queryStr.append("    ?organisation rdfs:label ?ownerLabel . ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("     ?dataset meta:hasTopic ?topic . ");
		queryStr.append("     ?topic rdfs:label ?topicLabel . FILTER (lang(?topicLabel) = 'en') ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("    ?dataset meta:contactPerson ?contact .  ");
		queryStr.append("    ?contact rdfs:label ?contactLabel .  ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("      ?dataset meta:relatedToInfraObject ?infraObject . ");
		queryStr.append("      ?infraObject rdfs:label ?infraLabel .  ");
		queryStr.append("      ?infraObject meta:startRoadNetworkLocation ?startLocation .  ");
		queryStr.append("      ?infraObject meta:endRoadNetworkLocation ?endLocation .  ");
		queryStr.append("      ?startLocation meta:roadReference ?road .  ");
		queryStr.append("      ?startLocation meta:wayReference ?way . ");
		queryStr.append("      ?startLocation meta:laneReference ?lane .  ");
		queryStr.append("      ?startLocation meta:hectometerPostReference ?start .  ");
		queryStr.append("      ?endLocation meta:hectometerPostReference ?end .  ");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?datasetLabel");
		JsonNode responseNodes = fuseki.query(queryStr);
		String lastDatasetId = null;
		Dataset dataset = null;
		List<InfraObject> infraObjects = null;
		for (JsonNode node : responseNodes) {
			String datasetId = node.get("dataset").get("value").asText();
			String datasetLabel = node.get("datasetLabel").get("value").asText();
			JsonNode dataReferenceNode = node.get("dataReference");
			URI dataReference = dataReferenceNode != null ? new URI(dataReferenceNode.get("value").asText()) : null;
			JsonNode measurementStartDateNode = node.get("measurementStartDate");
			Date measurementStartDate = measurementStartDateNode != null
					? DatatypeConverter.parseDateTime(measurementStartDateNode.get("value").asText()).getTime()
					: null;
			JsonNode measurementEndDateNode = node.get("measurementEndDate");
			Date measurementEndDate = measurementEndDateNode != null
					? DatatypeConverter.parseDateTime(measurementEndDateNode.get("value").asText()).getTime()
					: null;
			JsonNode projectNode = node.get("project");
			URI project = projectNode != null ? new URI(projectNode.get("value").asText()) : null;
			JsonNode projectLabelNode = node.get("projectLabel");
			String projectLabel = projectLabelNode != null ? projectLabelNode.get("value").asText() : null;
			JsonNode organisationNode = node.get("organisation");
			URI organisation = organisationNode != null ? new URI(organisationNode.get("value").asText()) : null;
			JsonNode ownerLabelNode = node.get("ownerLabel");
			String ownerLabel = ownerLabelNode != null ? ownerLabelNode.get("value").asText() : null;
			JsonNode topicNode = node.get("topic");
			URI topic = topicNode != null ? new URI(topicNode.get("value").asText()) : null;
			JsonNode topicLabelNode = node.get("topicLabel");
			String topicLabel = topicLabelNode != null ? topicLabelNode.get("value").asText() : null;
			JsonNode contactNode = node.get("contact");
			URI contact = contactNode != null ? new URI(contactNode.get("value").asText()) : null;
			JsonNode contactLabelNode = node.get("contactLabel");
			String contactLabel = contactLabelNode != null ? contactLabelNode.get("value").asText() : null;
			JsonNode decimalSymbolNode = node.get("decimalSymbol");
			URI decimalSymbol = decimalSymbolNode != null ? new URI(decimalSymbolNode.get("value").asText()) : null;
			JsonNode separatorNode = node.get("separator");
			URI separator = separatorNode != null ? new URI(separatorNode.get("value").asText()) : null;
			JsonNode formatNode = node.get("format");
			URI format = formatNode != null ? new URI(formatNode.get("value").asText()) : null;
			JsonNode infraObjectNode = node.get("infraObject");
			URI infraObjectUri = infraObjectNode != null ? new URI(infraObjectNode.get("value").asText()) : null;
			JsonNode infraLabelNode = node.get("infraLabel");
			String infraLabel = infraLabelNode != null ? infraLabelNode.get("value").asText() : null;
			JsonNode roadNode = node.get("road");
			String road = roadNode != null ? roadNode.get("value").asText() : null;
			JsonNode wayNode = node.get("way");
			String way = wayNode != null ? wayNode.get("value").asText() : null;
			JsonNode laneNode = node.get("lane");
			String lane = laneNode != null ? laneNode.get("value").asText() : null;
			JsonNode startNode = node.get("start");
			Double start = startNode != null ? Double.parseDouble(startNode.get("value").textValue().replace(',', '.'))
					: null;
			JsonNode endNode = node.get("end");
			Double end = endNode != null ? Double.parseDouble(endNode.get("value").textValue().replace(',', '.'))
					: null;
			if (!datasetId.equals(lastDatasetId)) {
				dataset = new Dataset(datasetId, datasetLabel, dataReference, measurementStartDate, measurementEndDate,
						decimalSymbol, separator, format, project, projectLabel, organisation, ownerLabel, topic,
						topicLabel, contact, contactLabel);
				datasets.add(dataset);
				infraObjects = new ArrayList<>();
				lastDatasetId = datasetId;
			}
			List<String> measurementYears = getMeasurementYears(node.get("dataset").get("value").asText());
			dataset.setMeasurementYears(measurementYears);
			List<QuantityKindAndUnit> quantityKindAndUnits = getQuantityKindAndUnits(
					node.get("dataset").get("value").asText());
			dataset.setQuantityKindAndUnits(quantityKindAndUnits);
			if (infraObjectUri != null) {
				InfraObject infraObject = new InfraObject(infraObjectUri, infraLabel, road, way, lane, start, end);
				infraObjects.add(infraObject);
			}
			dataset.setInfraObjects(infraObjects);
		}
		return datasets;
	}

	public Dataset getDataset(String localName) throws IOException, URISyntaxException {
		String datasetId = METADATA + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("dataset", datasetId);
		queryStr.append(
				"SELECT DISTINCT ?datasetLabel ?dataReference ?measurementStartDate ?measurementEndDate ?project ?projectLabel ?organisation ?ownerLabel ?topic ?topicLabel ?contact ?contactLabel ?decimalSymbol ?separator ?format ?infraObject ?infraLabel ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdfs:label ?datasetLabel .");
		queryStr.append("  OPTIONAL {    ?dataset meta:csvDecimalSymbol ?decimalSymbol . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:csvSeparatorSymbol ?separator . } ");
		queryStr.append("  OPTIONAL {    ?dataset dce:format ?format . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:dataReference ?dataReference . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:measurementStartDate ?measurementStartDate . } ");
		queryStr.append("  OPTIONAL {    ?dataset meta:measurementEndDate ?measurementEndDate . } ");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("    ?dataset meta:forProject ?project . ");
		queryStr.append("    ?project rdfs:label ?projectLabel . ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("    ?dataset meta:hasOwner ?organisation . ");
		queryStr.append("    ?organisation rdfs:label ?ownerLabel . ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("     ?dataset meta:hasTopic ?topic . ");
		queryStr.append("     ?topic rdfs:label ?topicLabel . FILTER (lang(?topicLabel) = 'en') ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("    ?dataset meta:contactPerson ?contact .  ");
		queryStr.append("    ?contact rdfs:label ?contactLabel .  ");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL {  ");
		queryStr.append("      ?dataset meta:relatedToInfraObject ?infraObject . ");
		queryStr.append("      ?infraObject rdfs:label ?infraLabel .  ");
		queryStr.append("      ?infraObject meta:startRoadNetworkLocation ?startLocation .  ");
		queryStr.append("      ?infraObject meta:endRoadNetworkLocation ?endLocation .  ");
		queryStr.append("      ?startLocation meta:roadReference ?road .  ");
		queryStr.append("      ?startLocation meta:wayReference ?way . ");
		queryStr.append("      ?startLocation meta:laneReference ?lane .  ");
		queryStr.append("      ?startLocation meta:hectometerPostReference ?start .  ");
		queryStr.append("      ?endLocation meta:hectometerPostReference ?end .  ");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?datasetLabel");
		JsonNode responseNodes = fuseki.query(queryStr);
		String lastDatasetId = null;
		Dataset dataset = null;
		List<InfraObject> infraObjects = null;
		for (JsonNode node : responseNodes) {
			String datasetLabel = node.get("datasetLabel").get("value").asText();
			JsonNode dataReferenceNode = node.get("dataReference");
			URI dataReference = dataReferenceNode != null ? new URI(dataReferenceNode.get("value").asText()) : null;
			JsonNode measurementStartDateNode = node.get("measurementStartDate");
			Date measurementStartDate = measurementStartDateNode != null
					? DatatypeConverter.parseDate(measurementStartDateNode.get("value").asText()).getTime()
					: null;
			JsonNode measurementEndDateNode = node.get("measurementEndDate");
			Date measurementEndDate = measurementEndDateNode != null
					? DatatypeConverter.parseDate(measurementEndDateNode.get("value").asText()).getTime()
					: null;
			JsonNode projectNode = node.get("project");
			URI project = projectNode != null ? new URI(projectNode.get("value").asText()) : null;
			JsonNode projectLabelNode = node.get("projectLabel");
			String projectLabel = projectLabelNode != null ? projectLabelNode.get("value").asText() : null;
			JsonNode organisationNode = node.get("organisation");
			URI organisation = organisationNode != null ? new URI(organisationNode.get("value").asText()) : null;
			JsonNode ownerLabelNode = node.get("ownerLabel");
			String ownerLabel = ownerLabelNode != null ? ownerLabelNode.get("value").asText() : null;
			JsonNode topicNode = node.get("topic");
			URI topic = topicNode != null ? new URI(topicNode.get("value").asText()) : null;
			JsonNode topicLabelNode = node.get("topicLabel");
			String topicLabel = topicLabelNode != null ? topicLabelNode.get("value").asText() : null;
			JsonNode contactNode = node.get("contact");
			URI contact = contactNode != null ? new URI(contactNode.get("value").asText()) : null;
			JsonNode contactLabelNode = node.get("contactLabel");
			String contactLabel = contactLabelNode != null ? contactLabelNode.get("value").asText() : null;
			JsonNode decimalSymbolNode = node.get("decimalSymbol");
			URI decimalSymbol = decimalSymbolNode != null ? new URI(decimalSymbolNode.get("value").asText()) : null;
			JsonNode separatorNode = node.get("separator");
			URI separator = separatorNode != null ? new URI(separatorNode.get("value").asText()) : null;
			JsonNode formatNode = node.get("format");
			URI format = formatNode != null ? new URI(formatNode.get("value").asText()) : null;
			JsonNode infraObjectNode = node.get("infraObject");
			URI infraObjectUri = infraObjectNode != null ? new URI(infraObjectNode.get("value").asText()) : null;
			JsonNode infraLabelNode = node.get("infraLabel");
			String infraLabel = infraLabelNode != null ? infraLabelNode.get("value").asText() : null;
			JsonNode roadNode = node.get("road");
			String road = roadNode != null ? roadNode.get("value").asText() : null;
			JsonNode wayNode = node.get("way");
			String way = wayNode != null ? wayNode.get("value").asText() : null;
			JsonNode laneNode = node.get("lane");
			String lane = laneNode != null ? laneNode.get("value").asText() : null;
			JsonNode startNode = node.get("start");
			Double start = startNode != null ? Double.parseDouble(startNode.get("value").textValue().replace(',', '.'))
					: null;
			JsonNode endNode = node.get("end");
			Double end = endNode != null ? Double.parseDouble(endNode.get("value").textValue().replace(',', '.'))
					: null;
			if (!datasetId.equals(lastDatasetId)) {
				dataset = new Dataset(datasetId, datasetLabel, dataReference, measurementStartDate, measurementEndDate,
						decimalSymbol, separator, format, project, projectLabel, organisation, ownerLabel, topic,
						topicLabel, contact, contactLabel);
				infraObjects = new ArrayList<>();
				lastDatasetId = datasetId;
			}
			List<String> measurementYears = getMeasurementYears(datasetId);
			dataset.setMeasurementYears(measurementYears);
			List<QuantityKindAndUnit> quantityKindAndUnits = getQuantityKindAndUnits(datasetId);
			dataset.setQuantityKindAndUnits(quantityKindAndUnits);
			if (infraObjectUri != null) {
				InfraObject infraObject = new InfraObject(infraObjectUri, infraLabel, road, way, lane, start, end);
				infraObjects.add(infraObject);
			}
			dataset.setInfraObjects(infraObjects);
		}
		return dataset;
	}

	public Dataset createDataset() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		String localName = "ID" + UUID.randomUUID().toString();
		URI uri = new URI(METADATA + localName);
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.setLiteral("label", localName.substring(0, 6));
		queryStr.setIri("format", "http://www.sparontologies.net/mediatype/text/csv");
		queryStr.setIri("csvDecimalSymbol", META + "COMMA");
		queryStr.setIri("csvSeparatorSymbol", META + "DOT");
		queryStr.append("INSERT { ");
		queryStr.append("  ?uri rdf:type meta:File . ");
		queryStr.append("  ?uri rdfs:label ?label . ");
		queryStr.append("  ?uri meta:csvDecimalSymbol ?csvDecimalSymbol . ");
		queryStr.append("  ?uri meta:csvSeparatorSymbol ?csvSeparatorSymbol . ");
		queryStr.append("  ?uri dce:format ?format . ");
		queryStr.append("} WHERE {");
		queryStr.append("}");
		fuseki.update(queryStr);

		return getDataset(localName);
	}

	private List<QuantityKindAndUnit> getQuantityKindAndUnits(String datasetUri)
			throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setIri("dataset", datasetUri);
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.append("SELECT  ?quantityUri ?label ?quantity ?unit ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdf:type meta:File ;");
		queryStr.append("    meta:quantityKindAndUnit ?quantityUri .");
		queryStr.append("    ?quantityUri rdfs:label ?label .");
		queryStr.append("    OPTIONAL { ?quantityUri meta:quantityReference ?quantity . }");
		queryStr.append("    OPTIONAL { ?quantityUri meta:unitReference ?unit . }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?quantity");
		JsonNode responseNodes = fuseki.query(queryStr);
		List<QuantityKindAndUnit> quantities = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("quantityUri").get("value").asText());
			String label = node.get("label").get("value").asText();
			JsonNode quantityNode = node.get("quantity");
			String quantity = quantityNode != null ? quantityNode.get("value").asText() : null;
			JsonNode unitNode = node.get("unit");
			String unit = unitNode != null ? unitNode.get("value").asText() : null;
			System.out.println("Quantity: " + label);
			quantities.add(new QuantityKindAndUnit(uri, label, quantity, unit));
		}
		return quantities;
	}

	private List<String> getMeasurementYears(String datasetUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setIri("dataset", datasetUri);
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.append("SELECT  ?year ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdf:type meta:File ;");
		queryStr.append("    meta:measurementYear ?year .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?year");
		JsonNode responseNodes = fuseki.query(queryStr);
		List<String> years = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			String year = node.get("year").get("value").asText();
			System.out.println("Year: " + year);
			years.add(year);
		}
		return years;
	}

	public void getDataReference(URI dataReference) throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = client.execute(new HttpGet(dataReference));
		InputStream content = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		for (CSVRecord csvRecord : parser) {
			System.out.println(csvRecord.toString());
		}
		parser.close();
	}

	public Dataset updateDataset(String localName, Dataset updatedDataset) throws IOException, URISyntaxException {
		Dataset storedDataset = getDataset(localName);
		if (!storedDataset.getDatasetLabel().equals(updatedDataset.getDatasetLabel())) {
			updateLabelOfDataset(updatedDataset);
		}
		updateProjectOfDataset(storedDataset, updatedDataset);
		updateOrganisationOfDataset(storedDataset, updatedDataset);
		updateContactOfDataset(storedDataset, updatedDataset);
		updateTopicOfDataset(storedDataset, updatedDataset);
		if (!storedDataset.getDecimalSymbol().equals(updatedDataset.getDecimalSymbol())) {
			updateDecimalSymbolOfDataset(updatedDataset);
		}
		if (!storedDataset.getSeparator().equals(updatedDataset.getSeparator())) {
			updateSeparatorOfDataset(updatedDataset);
		}
		if (!storedDataset.getFormat().equals(updatedDataset.getFormat())) {
			updateFormatOfDataset(updatedDataset);
		}
		updateDataReferenceOfDataset(storedDataset, updatedDataset);
		updateMeasurementYears(storedDataset, updatedDataset);
		updateQuantities(storedDataset, updatedDataset);
		updateInfraObjects(storedDataset, updatedDataset);
		return getDataset(localName);
	}

	private void updateMeasurementYears(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		boolean doUpdate = false;
		for (String year : storedDataset.getMeasurementYears()) {
			if (!updatedDataset.getMeasurementYears().contains(year)) {
				doUpdate = true;
				break;
			}
		}
		if (!doUpdate) {
			for (String year : updatedDataset.getMeasurementYears()) {
				if (!storedDataset.getMeasurementYears().contains(year)) {
					doUpdate = true;
					break;
				}
			}
		}

		if (doUpdate) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setNsPrefix("metadata", METADATA);
			queryStr.setIri("dataset", storedDataset.getDatasetUri());
			queryStr.append("DELETE { ?dataset meta:measurementYear ?year . } ");
			queryStr.append("WHERE {");
			queryStr.append("  ?dataset rdf:type meta:File ;");
			queryStr.append("  OPTIONAL { meta:measurementYear ?year . } ");
			queryStr.append("}");
			fuseki.update(queryStr);

			for (String year : updatedDataset.getMeasurementYears()) {
				queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
				queryStr.setNsPrefix("meta", META);
				queryStr.setNsPrefix("metadata", METADATA);
				queryStr.setIri("dataset", storedDataset.getDatasetUri());
				queryStr.setLiteral("year", year);
				queryStr.append("INSERT { ?dataset meta:measurementYear ?year . } ");
				queryStr.append("WHERE {");
				queryStr.append("}");
				fuseki.update(queryStr);
			}
		}
	}

	private void updateQuantities(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		boolean doUpdate = false;
		for (QuantityKindAndUnit quantity : storedDataset.getQuantityKindAndUnits()) {
			if (!updatedDataset.getQuantityKindAndUnits().contains(quantity)) {
				doUpdate = true;
				break;
			}
		}
		if (!doUpdate) {
			for (QuantityKindAndUnit quantity : updatedDataset.getQuantityKindAndUnits()) {
				if (!storedDataset.getQuantityKindAndUnits().contains(quantity)) {
					doUpdate = true;
					break;
				}
			}
		}
		if (doUpdate) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
			queryStr.setIri("dataset", storedDataset.getDatasetUri());
			queryStr.append("DELETE { ?dataset meta:quantityKindAndUnit ?quantity . } ");
			queryStr.append("WHERE {");
			queryStr.append("  ?dataset rdf:type meta:File ;");
			queryStr.append("    meta:quantityKindAndUnit ?quantity .");
			queryStr.append("}");
			fuseki.update(queryStr);

			for (QuantityKindAndUnit quantity : updatedDataset.getQuantityKindAndUnits()) {
				queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
				queryStr.setNsPrefix("meta", META);
				queryStr.setNsPrefix("metadata", METADATA);
				queryStr.setIri("dataset", storedDataset.getDatasetUri());
				queryStr.setIri("quantity", quantity.getUri().toString());
				queryStr.append("INSERT { ?dataset meta:quantityKindAndUnit ?quantity . } ");
				queryStr.append("WHERE {");
				queryStr.append("}");
				fuseki.update(queryStr);
			}
		}
	}

	private void updateInfraObjects(Dataset storedDataset, Dataset updatedDataset)
			throws IOException, URISyntaxException {
		boolean doUpdate = false;
		for (InfraObject infraObject : storedDataset.getInfraObjects()) {
			if (!updatedDataset.getInfraObjects().contains(infraObject)) {
				doUpdate = true;
				break;
			}
		}
		if (!doUpdate) {
			for (InfraObject infraObject : updatedDataset.getInfraObjects()) {
				if (!storedDataset.getInfraObjects().contains(infraObject)) {
					doUpdate = true;
					break;
				}
			}
		}
		if (doUpdate) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setNsPrefix("metadata", METADATA);
			queryStr.setIri("dataset", updatedDataset.getDatasetUri());
			queryStr.append("DELETE { ");
			queryStr.append("  ?dataset meta:relatedToInfraObject ?infra_object . ");
			queryStr.append("  ?infra_object ?pred ?value . ");
			queryStr.append("}");
			queryStr.append("WHERE {");
			queryStr.append("  ?dataset rdf:type meta:File ;");
			queryStr.append("    meta:relatedToInfraObject ?infra_object .");
			queryStr.append("}");
			fuseki.update(queryStr);

			for (InfraObject infraObject : updatedDataset.getInfraObjects()) {
				queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
				queryStr.setNsPrefix("meta", META);
				queryStr.setNsPrefix("metadata", METADATA);
				queryStr.setIri("dataset", updatedDataset.getDatasetUri());
				queryStr.setIri("infra_object", infraObject.getUri().toString());
				URI start = new URI(METADATA + "ID" + UUID.randomUUID().toString());
				queryStr.setIri("start", start.toString());
				queryStr.setLiteral("start_label", start.getFragment());
				queryStr.setLiteral("start_hectometer", String.valueOf(infraObject.getStart()));
				queryStr.setLiteral("start_lane", infraObject.getLane());
				queryStr.setLiteral("start_road", infraObject.getRoad());
				queryStr.setLiteral("start_way", infraObject.getWay());
				URI end = new URI(METADATA + "ID" + UUID.randomUUID().toString());
				queryStr.setIri("end", end.toString());
				queryStr.setLiteral("end_label", end.getFragment());
				queryStr.setLiteral("end_hectometer", String.valueOf(infraObject.getEnd()));
				queryStr.setLiteral("end_lane", infraObject.getLane());
				queryStr.setLiteral("end_road", infraObject.getRoad());
				queryStr.setLiteral("end_way", infraObject.getWay());
				queryStr.setLiteral("distance", 0);
				queryStr.append("INSERT { ");
				queryStr.append("  ?dataset meta:relatedToInfraObject ?infra_object . ");
				queryStr.append("  ?infra_object rdf:type meta:RoadPart . ");
				queryStr.append("  ?start rdf:type meta:RoadNetworkLocation ; ");
				queryStr.append("    rdfs:label ?start_label ; ");
				queryStr.append("    meta:distance ?distance ; ");
				queryStr.append("    meta:hectometerPostReference ?start_hectometer ; ");
				queryStr.append("    meta:laneReference ?start_lane ; ");
				queryStr.append("    meta:roadReference ?start_road ; ");
				queryStr.append("    meta:wayReference ?start_way . ");
				queryStr.append("  ?end rdf:type meta:RoadNetworkLocation ; ");
				queryStr.append("    rdfs:label ?end_label ; ");
				queryStr.append("    meta:distance ?distance ; ");
				queryStr.append("    meta:hectometerPostReference ?end_hectometer ; ");
				queryStr.append("    meta:laneReference ?end_lane ; ");
				queryStr.append("    meta:roadReference ?end_road ; ");
				queryStr.append("    meta:wayReference ?end_way . ");
				queryStr.append("  ?infra_object meta:startRoadNetworkLocation ?start . ");
				queryStr.append("  ?infra_object meta:endRoadNetworkLocation ?end . ");
				queryStr.append("}");
				queryStr.append("WHERE {");
				queryStr.append("}");
				fuseki.update(queryStr);
			}
		}
	}

	private void updateLabelOfDataset(Dataset dataset) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setLiteral("new_label", dataset.getDatasetLabel());
		queryStr.append("  DELETE { ?subject rdfs:label ?label } ");
		queryStr.append("  INSERT { ?subject rdfs:label ?new_label } ");
		queryStr.append("WHERE { OPTIONAL { ?subject rdfs:label ?label } ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateProjectOfDataset(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		if ((storedDataset.getProject() != null && !storedDataset.getProject().equals(updatedDataset.getProject()))
				|| (updatedDataset.getProject() != null
						&& !updatedDataset.getProject().equals(storedDataset.getProject()))) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setIri("subject", updatedDataset.getDatasetUri());
			queryStr.setIri("new_project", updatedDataset.getProject().toString());
			queryStr.append("  DELETE { ?subject meta:forProject ?project } ");
			queryStr.append("  INSERT { ?subject meta:forProject ?new_project } ");
			queryStr.append("WHERE { OPTIONAL { ?subject meta:forProject ?project . } ");
			queryStr.append("}");

			fuseki.update(queryStr);
		}
	}

	private void updateDataReferenceOfDataset(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		if ((storedDataset.getDataReference() != null
				&& !storedDataset.getDataReference().equals(updatedDataset.getDataReference()))
				|| (updatedDataset.getDataReference() != null
						&& !updatedDataset.getDataReference().equals(storedDataset.getDataReference()))) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setIri("subject", updatedDataset.getDatasetUri());
			queryStr.setIri("new_datareference", updatedDataset.getDataReference().toString());
			queryStr.append("  DELETE { ?subject meta:dataReference ?datareference } ");
			queryStr.append("  INSERT { ?subject meta:dataReference ?new_datareference } ");
			queryStr.append("WHERE { ?subject meta:dataReference ?datareference . ");
			queryStr.append("}");

			fuseki.update(queryStr);
		}
	}

	private void updateOrganisationOfDataset(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		if ((storedDataset.getOrganisation() != null
				&& !storedDataset.getOrganisation().equals(updatedDataset.getOrganisation()))
				|| (updatedDataset.getOrganisation() != null
						&& !updatedDataset.getOrganisation().equals(storedDataset.getOrganisation()))) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setIri("subject", updatedDataset.getDatasetUri());
			queryStr.setIri("new_organisation", updatedDataset.getOrganisation().toString());
			queryStr.append("  DELETE { ?subject meta:hasOwner ?organisation } ");
			queryStr.append("  INSERT { ?subject meta:hasOwner ?new_organisation } ");
			queryStr.append("WHERE { OPTIONAL { ?subject meta:hasOwner ?organisation . } ");
			queryStr.append("}");

			fuseki.update(queryStr);
		}
	}

	private void updateContactOfDataset(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		if ((storedDataset.getContact() != null && !storedDataset.getContact().equals(updatedDataset.getContact()))
				|| (updatedDataset.getContact() != null
						&& !updatedDataset.getContact().equals(storedDataset.getContact()))) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setIri("subject", updatedDataset.getDatasetUri());
			queryStr.setIri("new_contact", updatedDataset.getContact().toString());
			queryStr.append("  DELETE { ?subject meta:contactPerson ?contact } ");
			queryStr.append("  INSERT { ?subject meta:contactPerson ?new_contact } ");
			queryStr.append("WHERE { OPTIONAL { ?subject meta:contactPerson ?organisation . } ");
			queryStr.append("}");

			fuseki.update(queryStr);
		}
	}

	private void updateTopicOfDataset(Dataset storedDataset, Dataset updatedDataset) throws IOException {
		if ((storedDataset.getTopic() != null && !storedDataset.getTopic().equals(updatedDataset.getTopic()))
				|| (updatedDataset.getTopic() != null && !updatedDataset.getTopic().equals(storedDataset.getTopic()))) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			queryStr.setNsPrefix("meta", META);
			queryStr.setIri("subject", updatedDataset.getDatasetUri());
			queryStr.setIri("new_topic", updatedDataset.getTopic().toString());
			queryStr.append("  DELETE { ?subject meta:hasTopic ?topic } ");
			queryStr.append("  INSERT { ?subject meta:hasTopic ?new_topic } ");
			queryStr.append("WHERE { OPTIONAL { ?subject meta:hasTopic ?topic . } ");
			queryStr.append("}");

			fuseki.update(queryStr);
		}
	}

	private void updateDecimalSymbolOfDataset(Dataset dataset) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_decimal_symbol", dataset.getDecimalSymbol().getUri().toString());
		queryStr.append("  DELETE { ?subject meta:csvDecimalSymbol ?decimal_symbol } ");
		queryStr.append("  INSERT { ?subject meta:csvDecimalSymbol ?new_decimal_symbol } ");
		queryStr.append("WHERE { ?subject meta:csvDecimalSymbol ?decimal_symbol . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateSeparatorOfDataset(Dataset dataset) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_separator_symbol", dataset.getSeparator().getUri().toString());
		queryStr.append("  DELETE { ?subject meta:csvSeparatorSymbol ?separator_symbol } ");
		queryStr.append("  INSERT { ?subject meta:csvSeparatorSymbol ?new_separator_symbol } ");
		queryStr.append("WHERE { ?subject meta:csvSeparatorSymbol ?separator_symbol . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateFormatOfDataset(Dataset dataset) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("dc", DCE);
		queryStr.setNsPrefix("meta", META);
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_format", dataset.getSeparator().getUri().toString());
		queryStr.append("  DELETE { ?subject dc:format ?format } ");
		queryStr.append("  INSERT { ?subject dc:format ?new_format } ");
		queryStr.append("WHERE { ?subject dc:format ?format . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	public List<Project> getAllProjects() throws IOException, URISyntaxException {
		List<Project> projects = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append("SELECT DISTINCT ?uri ?label ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Project ;");
		queryStr.append("    rdfs:label ?label .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		Project project = null;
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			String label = node.get("label").get("value").asText();
			project = new Project(uri, label);
			projects.add(project);
		}
		return projects;
	}

	public Project getProject(String localName) throws IOException, URISyntaxException {
		URI uri = new URI(METADATA + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT DISTINCT ?uri ?label ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Project ;");
		queryStr.append("    rdfs:label ?label .");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		Project project = null;
		for (JsonNode node : responseNodes) {
			String label = node.get("label").get("value").asText();
			project = new Project(uri, label);
		}
		return project;
	}

	public List<Organisation> getAllOrganisations() throws IOException, URISyntaxException {
		List<Organisation> organisations = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append("SELECT DISTINCT ?uri ?label ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Organisation ;");
		queryStr.append("    rdfs:label ?label .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		Organisation organisation = null;
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			String label = node.get("label").get("value").asText();
			organisation = new Organisation(uri, label);
			organisations.add(organisation);
		}
		return organisations;
	}

	public Organisation getOrganisation(String localName) throws IOException, URISyntaxException {
		URI uri = new URI(METADATA + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT DISTINCT ?uri ?label ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Organisation ;");
		queryStr.append("    rdfs:label ?label .");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		Organisation organisation = null;
		for (JsonNode node : responseNodes) {
			String label = node.get("label").get("value").asText();
			organisation = new Organisation(uri, label);
		}
		return organisation;
	}

	public List<Person> getAllPersons() throws IOException, URISyntaxException {
		List<Person> persons = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append("SELECT DISTINCT ?uri ?label ?email_address ?works_for ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Person ;");
		queryStr.append("    meta:eMailAddress ?email_address ;");
		queryStr.append("    meta:worksFor ?works_for ;");
		queryStr.append("    rdfs:label ?label . ");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		Person person = null;
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			String label = node.get("label").get("value").asText();
			String eMailAddress = node.get("email_address").get("value").asText();
			URI worksFor = new URI(node.get("works_for").get("value").asText());
			person = new Person(uri, label, eMailAddress, worksFor);
			persons.add(person);
		}
		return persons;
	}

	public Person getPerson(String localName) throws IOException, URISyntaxException {
		URI uri = new URI(METADATA + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT DISTINCT ?label ?email_address ?works_for ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Organisation ;");
		queryStr.append("    meta:eMailAddress ?email_address ;");
		queryStr.append("    meta:worksFor ?works_for ;");
		queryStr.append("    rdfs:label ?label . ");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		Person person = null;
		for (JsonNode node : responseNodes) {
			String label = node.get("label").get("value").asText();
			String eMailAddress = node.get("email_address").get("value").asText();
			URI worksFor = new URI(node.get("works_for").get("value").asText());
			person = new Person(uri, label, eMailAddress, worksFor);
		}
		return person;
	}

	public List<Topic> getAllTopics() throws IOException, URISyntaxException {
		List<Topic> topics = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append("SELECT DISTINCT ?uri ?label ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Topic ;");
		queryStr.append("    rdfs:label ?label . FILTER(langMatches(lang(?label), \"en\")) ");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		Topic topic = null;
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			String label = node.get("label").get("value").asText();
			topic = new Topic(uri, label);
			topics.add(topic);
		}
		return topics;
	}

	public Topic getTopic(String localName) throws IOException, URISyntaxException {
		URI uri = new URI(META + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT DISTINCT ?uri ?label ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:Topic ;");
		queryStr.append("    rdfs:label ?label . FILTER(langMatches(lang(?label), \"en\"))");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		Topic topic = null;
		for (JsonNode node : responseNodes) {
			String label = node.get("label").get("value").asText();
			topic = new Topic(uri, label);
		}
		return topic;
	}

	public List<InfraObject> getAllInfraObjects() throws IOException, URISyntaxException {
		List<InfraObject> infraObjects = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append("SELECT DISTINCT ?uri ?label ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:RoadPart .");
		queryStr.append("  ?uri  rdfs:label ?label .");
		queryStr.append("  OPTIONAL { ");
		queryStr.append("    ?uri  meta:endRoadNetworkLocation ?endLocation .");
		queryStr.append("    ?endLocation meta:hectometerPostReference ?end ;");
		queryStr.append("      meta:laneReference ?lane ;");
		queryStr.append("      meta:roadReference ?road ;");
		queryStr.append("      meta:wayReference ?way .");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL { ");
		queryStr.append("    ?uri  meta:startRoadNetworkLocation ?startLocation .");
		queryStr.append("    ?startLocation meta:hectometerPostReference ?start .");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		InfraObject infraObject = null;
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode roadNode = node.get("road");
			String road = roadNode != null ? roadNode.get("value").asText() : null;
			JsonNode wayNode = node.get("way");
			String way = wayNode != null ? wayNode.get("value").asText() : null;
			JsonNode laneNode = node.get("lane");
			String lane = laneNode != null ? laneNode.get("value").asText() : null;
			JsonNode startNode = node.get("start");
			Double start = startNode != null ? Double.parseDouble(startNode.get("value").textValue().replace(',', '.'))
					: null;
			JsonNode endNode = node.get("end");
			Double end = endNode != null ? Double.parseDouble(endNode.get("value").textValue().replace(',', '.'))
					: null;
			infraObject = new InfraObject(uri, label, road, way, lane, start, end);
			infraObjects.add(infraObject);
		}
		return infraObjects;
	}

	public InfraObject getInfraObject(String localName) throws IOException, URISyntaxException {
		URI uri = new URI(METADATA + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT DISTINCT ?label ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:RoadPart .");
		queryStr.append("  ?uri rdfs:label ?label .");
		queryStr.append("  OPTIONAL { ");
		queryStr.append("    ?uri  meta:endRoadNetworkLocation ?endLocation .");
		queryStr.append("    ?endLocation meta:hectometerPostReference ?end ;");
		queryStr.append("      meta:laneReference ?lane ;");
		queryStr.append("      meta:roadReference ?road ;");
		queryStr.append("      meta:wayReference ?way .");
		queryStr.append("  }");
		queryStr.append("  OPTIONAL { ");
		queryStr.append("    ?uri  meta:startRoadNetworkLocation ?startLocation .");
		queryStr.append("    ?startLocation meta:hectometerPostReference ?start .");
		queryStr.append("  }");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		InfraObject infraObject = null;
		for (JsonNode node : responseNodes) {
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode roadNode = node.get("road");
			String road = roadNode != null ? roadNode.get("value").asText() : null;
			JsonNode wayNode = node.get("way");
			String way = wayNode != null ? wayNode.get("value").asText() : null;
			JsonNode laneNode = node.get("lane");
			String lane = laneNode != null ? laneNode.get("value").asText() : null;
			JsonNode startNode = node.get("start");
			Double start = startNode != null ? Double.parseDouble(startNode.get("value").textValue().replace(',', '.'))
					: null;
			JsonNode endNode = node.get("end");
			Double end = endNode != null ? Double.parseDouble(endNode.get("value").textValue().replace(',', '.'))
					: null;
			infraObject = new InfraObject(uri, label, road, way, lane, start, end);
		}
		return infraObject;
	}

	public InfraObject createInfraObject() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		String localName = "ID" + UUID.randomUUID().toString();
		URI uri = new URI(METADATA + localName);
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.setLiteral("label", localName.substring(0, 6));
		queryStr.append("INSERT { ");
		queryStr.append("  ?uri rdf:type meta:RoadPart . ");
		queryStr.append("  ?uri rdfs:label ?label . ");
		queryStr.append("} WHERE {");
		queryStr.append("}");
		fuseki.update(queryStr);

		return getInfraObject(localName);
	}

	public List<QuantityKindAndUnit> getAllQuantities() throws IOException, URISyntaxException {
		List<QuantityKindAndUnit> quantities = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.append("SELECT  ?uri ?label ?quantity ?unit ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:QuantityKindAndUnit ;");
		queryStr.append("    rdfs:label ?label .");
		queryStr.append("  OPTIONAL { ?uri meta:quantityReference ?quantity . }");
		queryStr.append("  OPTIONAL { ?uri meta:unitReference ?unit . }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			String label = node.get("label").get("value").asText();
			JsonNode quantityNode = node.get("quantity");
			String quantity = quantityNode != null ? quantityNode.get("value").asText() : null;
			JsonNode unitNode = node.get("unit");
			String unit = unitNode != null ? unitNode.get("value").asText() : null;
			System.out.println("Quantity: " + label);
			quantities.add(new QuantityKindAndUnit(uri, label, quantity, unit));
		}
		return quantities;
	}

	public QuantityKindAndUnit getQuantity(String localName) throws IOException, URISyntaxException {
		URI uri = new URI(METADATA + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", META);
		queryStr.setNsPrefix("metadata", METADATA);
		queryStr.setNsPrefix("dce", DCE);
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT  ?label ?quantity ?unit ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:QuantityKindAndUnit ;");
		queryStr.append("    rdfs:label ?label .");
		queryStr.append("    OPTIONAL { ?uri meta:quantityReference ?quantity . }");
		queryStr.append("    OPTIONAL { ?uri meta:unitReference ?unit . }");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		QuantityKindAndUnit quantityKindAndUnit = null;
		for (JsonNode node : responseNodes) {
			String label = node.get("label").get("value").asText();
			JsonNode quantityNode = node.get("quantity");
			String quantity = quantityNode != null ? quantityNode.get("value").asText() : null;
			JsonNode unitNode = node.get("unit");
			String unit = unitNode != null ? unitNode.get("value").asText() : null;
			quantityKindAndUnit = new QuantityKindAndUnit(uri, label, quantity, unit);
		}
		return quantityKindAndUnit;
	}

	public List<Dataset.DecimalSymbol> getAllDecimalSymbols() throws IOException, URISyntaxException {
		return Arrays.asList(Dataset.DecimalSymbol.values());
	}

	public List<Dataset.Separator> getAllSeparators() throws IOException, URISyntaxException {
		return Arrays.asList(Dataset.Separator.values());
	}

	public List<Dataset.Format> getAllFormats() throws IOException, URISyntaxException {
		return Arrays.asList(Dataset.Format.values());
	}

}
