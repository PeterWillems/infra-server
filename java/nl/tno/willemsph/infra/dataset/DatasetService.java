package nl.tno.willemsph.infra.dataset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	@Autowired
	private SparqlService fuseki;

	public List<Dataset> getAllDatasets() throws IOException, URISyntaxException {
		List<Dataset> datasets = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
		queryStr.append(
				"SELECT DISTINCT ?dataset ?datasetLabel ?dataReference ?project ?projectLabel ?organisation ?ownerLabel ?topic ?topicLabel ?contact ?contactLabel ?decimalSymbol ?separator ?format ?infraObject ?infraLabel ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdf:type meta:File ;");
		queryStr.append("    rdfs:label ?datasetLabel ;");
		queryStr.append("    meta:forProject ?project ;");
		queryStr.append("    meta:hasOwner ?organisation ;");
		queryStr.append("    meta:hasTopic ?topic ;");
		queryStr.append("    meta:contactPerson ?contact ;");
		queryStr.append("    meta:csvDecimalSymbol ?decimalSymbol ;");
		queryStr.append("    meta:csvSeparatorSymbol ?separator ;");
		queryStr.append("    dce:format ?format ;");
		queryStr.append("    meta:dataReference ?dataReference ;");
		queryStr.append("    meta:relatedToInfraObject ?infraObject .");
		queryStr.append("  ?project rdfs:label ?projectLabel .");
		queryStr.append("  ?organisation rdfs:label ?ownerLabel .");
		queryStr.append("  ?topic rdfs:label ?topicLabel . FILTER (lang(?topicLabel) = 'en') ");
		queryStr.append("  ?contact rdfs:label ?contactLabel .");
		queryStr.append("  ?infraObject rdfs:label ?infraLabel ;");
		queryStr.append("    meta:startRoadNetworkLocation ?startLocation ;");
		queryStr.append("    meta:endRoadNetworkLocation ?endLocation .");
		queryStr.append("  ?startLocation meta:roadReference ?road ;");
		queryStr.append("    meta:wayReference ?way ;");
		queryStr.append("    meta:laneReference ?lane ;");
		queryStr.append("    meta:hectometerPostReference ?start .");
		queryStr.append("  ?endLocation meta:hectometerPostReference ?end .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?datasetLabel");
		JsonNode responseNodes = fuseki.query(queryStr);
		String lastDatasetId = null;
		Dataset dataset = null;
		List<InfraObject> infraObjects = null;
		for (JsonNode node : responseNodes) {
			String datasetId = node.get("dataset").get("value").asText();
			String datasetLabel = node.get("datasetLabel").get("value").asText();
			URI dataReference = new URI(node.get("dataReference").get("value").asText());
			URI project = new URI(node.get("project").get("value").asText());
			String projectLabel = node.get("projectLabel").get("value").asText();
			URI organisation = new URI(node.get("organisation").get("value").asText());
			String ownerLabel = node.get("ownerLabel").get("value").asText();
			URI topic = new URI(node.get("topic").get("value").asText());
			String topicLabel = node.get("topicLabel").get("value").asText();
			URI contact = new URI(node.get("contact").get("value").asText());
			String contactLabel = node.get("contactLabel").get("value").asText();
			URI decimalSymbol = new URI(node.get("decimalSymbol").get("value").asText());
			URI separator = new URI(node.get("separator").get("value").asText());
			URI format = new URI(node.get("format").get("value").asText());
			URI infraObjectUri = new URI(node.get("infraObject").get("value").asText());
			String infraLabel = node.get("infraLabel").get("value").asText();
			String road = node.get("road").get("value").asText();
			String way = node.get("way").get("value").asText();
			String lane = node.get("lane").get("value").asText();
			Double start = Double.parseDouble(node.get("start").get("value").textValue().replace(',', '.'));
			Double end = Double.parseDouble(node.get("end").get("value").textValue().replace(',', '.'));
			if (!datasetId.equals(lastDatasetId)) {
				dataset = new Dataset(datasetId, datasetLabel, dataReference, decimalSymbol, separator, format, project,
						projectLabel, organisation, ownerLabel, topic, topicLabel, contact, contactLabel);
				datasets.add(dataset);
				infraObjects = new ArrayList<>();
				lastDatasetId = datasetId;
			}
			List<String> measurementYears = getMeasurementYears(node.get("dataset").get("value").asText());
			dataset.setMeasurementYears(measurementYears);
			List<QuantityKindAndUnit> quantityKindAndUnits = getQuantityKindAndUnits(
					node.get("dataset").get("value").asText());
			dataset.setQuantityKindAndUnits(quantityKindAndUnits);
			InfraObject infraObject = new InfraObject(infraObjectUri, infraLabel, road, way, lane, start, end);
			infraObjects.add(infraObject);
			dataset.setInfraObjects(infraObjects);
		}
		return datasets;
	}

	public Dataset getDataset(String localName) throws IOException, URISyntaxException {
		String datasetId = "https://w3id.org/metadata#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
		queryStr.setIri("dataset", datasetId);
		queryStr.append(
				"SELECT DISTINCT ?datasetLabel ?dataReference ?project ?projectLabel ?organisation ?ownerLabel ?topic ?topicLabel ?contact ?contactLabel ?decimalSymbol ?separator ?format ?infraObject ?infraLabel ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdfs:label ?datasetLabel ;");
		queryStr.append("    meta:forProject ?project ;");
		queryStr.append("    meta:hasOwner ?organisation ;");
		queryStr.append("    meta:hasTopic ?topic ;");
		queryStr.append("    meta:contactPerson ?contact ;");
		queryStr.append("    meta:csvDecimalSymbol ?decimalSymbol ;");
		queryStr.append("    meta:csvSeparatorSymbol ?separator ;");
		queryStr.append("    dce:format ?format ;");
		queryStr.append("    meta:dataReference ?dataReference ;");
		queryStr.append("    meta:relatedToInfraObject ?infraObject .");
		queryStr.append("  ?project rdfs:label ?projectLabel .");
		queryStr.append("  ?organisation rdfs:label ?ownerLabel .");
		queryStr.append("  ?topic rdfs:label ?topicLabel . FILTER (lang(?topicLabel) = 'en') ");
		queryStr.append("  ?contact rdfs:label ?contactLabel .");
		queryStr.append("  ?infraObject rdfs:label ?infraLabel ;");
		queryStr.append("    meta:startRoadNetworkLocation ?startLocation ;");
		queryStr.append("    meta:endRoadNetworkLocation ?endLocation .");
		queryStr.append("  ?startLocation meta:roadReference ?road ;");
		queryStr.append("    meta:wayReference ?way ;");
		queryStr.append("    meta:laneReference ?lane ;");
		queryStr.append("    meta:hectometerPostReference ?start .");
		queryStr.append("  ?endLocation meta:hectometerPostReference ?end .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?datasetLabel");
		JsonNode responseNodes = fuseki.query(queryStr);
		String lastDatasetId = null;
		Dataset dataset = null;
		List<InfraObject> infraObjects = null;
		for (JsonNode node : responseNodes) {
			String datasetLabel = node.get("datasetLabel").get("value").asText();
			URI dataReference = new URI(node.get("dataReference").get("value").asText());
			URI project = new URI(node.get("project").get("value").asText());
			String projectLabel = node.get("projectLabel").get("value").asText();
			URI organisation = new URI(node.get("organisation").get("value").asText());
			String ownerLabel = node.get("ownerLabel").get("value").asText();
			URI topic = new URI(node.get("topic").get("value").asText());
			String topicLabel = node.get("topicLabel").get("value").asText();
			URI contact = new URI(node.get("contact").get("value").asText());
			String contactLabel = node.get("contactLabel").get("value").asText();
			URI decimalSymbol = new URI(node.get("decimalSymbol").get("value").asText());
			URI separator = new URI(node.get("separator").get("value").asText());
			URI format = new URI(node.get("format").get("value").asText());
			URI infraObjectUri = new URI(node.get("infraObject").get("value").asText());
			String infraLabel = node.get("infraLabel").get("value").asText();
			String road = node.get("road").get("value").asText();
			String way = node.get("way").get("value").asText();
			String lane = node.get("lane").get("value").asText();
			Double start = Double.parseDouble(node.get("start").get("value").textValue().replace(',', '.'));
			Double end = Double.parseDouble(node.get("end").get("value").textValue().replace(',', '.'));
			if (!datasetId.equals(lastDatasetId)) {
				dataset = new Dataset(datasetId, datasetLabel, dataReference, decimalSymbol, separator, format, project,
						projectLabel, organisation, ownerLabel, topic, topicLabel, contact, contactLabel);
				infraObjects = new ArrayList<>();
				lastDatasetId = datasetId;
			}
			List<String> measurementYears = getMeasurementYears(datasetId);
			dataset.setMeasurementYears(measurementYears);
			List<QuantityKindAndUnit> quantityKindAndUnits = getQuantityKindAndUnits(datasetId);
			dataset.setQuantityKindAndUnits(quantityKindAndUnits);
			InfraObject infraObject = new InfraObject(infraObjectUri, infraLabel, road, way, lane, start, end);
			infraObjects.add(infraObject);
			dataset.setInfraObjects(infraObjects);
		}
		return dataset;
	}

	private List<QuantityKindAndUnit> getQuantityKindAndUnits(String datasetUri)
			throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setIri("dataset", datasetUri);
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
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
		if (!storedDataset.getProject().equals(updatedDataset.getProject())) {
			updateProjectOfDataset(updatedDataset);
		}
		if (!storedDataset.getOrganisation().equals(updatedDataset.getOrganisation())) {
			updateOrganisationOfDataset(updatedDataset);
		}
		if (!storedDataset.getContact().equals(updatedDataset.getContact())) {
			updateContactOfDataset(updatedDataset);
		}
		if (!storedDataset.getTopic().equals(updatedDataset.getTopic())) {
			updateTopicOfDataset(updatedDataset);
		}
		if (!storedDataset.getDecimalSymbol().equals(updatedDataset.getDecimalSymbol())) {
			updateDecimalSymbolOfDataset(updatedDataset);
		}
		if (!storedDataset.getSeparator().equals(updatedDataset.getSeparator())) {
			updateSeparatorOfDataset(updatedDataset);
		}
		if (!storedDataset.getFormat().equals(updatedDataset.getFormat())) {
			updateFormatOfDataset(updatedDataset);
		}
		if (!storedDataset.getDataReference().equals(updatedDataset.getDataReference())) {
			updateDataReferenceOfDataset(updatedDataset);
		}
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
			queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
			queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
			queryStr.setIri("dataset", storedDataset.getDatasetUri());
			queryStr.append("DELETE { ?dataset meta:measurementYear ?year . } ");
			queryStr.append("WHERE {");
			queryStr.append("  ?dataset rdf:type meta:File ;");
			queryStr.append("    meta:measurementYear ?year .");
			queryStr.append("}");
			fuseki.update(queryStr);

			for (String year : updatedDataset.getMeasurementYears()) {
				queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
				queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
				queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
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
			queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
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
				queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
				queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
				queryStr.setIri("dataset", storedDataset.getDatasetUri());
				queryStr.setIri("quantity", quantity.getUri().toString());
				queryStr.append("INSERT { ?dataset meta:quantityKindAndUnit ?quantity . } ");
				queryStr.append("WHERE {");
				queryStr.append("}");
				fuseki.update(queryStr);
			}
		}
	}
	
	private void updateInfraObjects(Dataset storedDataset, Dataset updatedDataset) throws IOException {
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
			queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
			queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
			queryStr.setIri("dataset", storedDataset.getDatasetUri());
			queryStr.append("DELETE { ?dataset meta:relatedToInfraObject ?infra_object . } ");
			queryStr.append("WHERE {");
			queryStr.append("  ?dataset rdf:type meta:File ;");
			queryStr.append("    meta:relatedToInfraObject ?infra_object .");
			queryStr.append("}");
			fuseki.update(queryStr);

			for (InfraObject infraObject : updatedDataset.getInfraObjects()) {
				queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
				queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
				queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
				queryStr.setIri("dataset", storedDataset.getDatasetUri());
				queryStr.setIri("infra_object", infraObject.getUri().toString());
				queryStr.append("INSERT { ?dataset meta:relatedToInfraObject ?infra_object . } ");
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

	private void updateProjectOfDataset(Dataset dataset) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_project", dataset.getProject().toString());
		queryStr.append("  DELETE { ?subject meta:forProject ?project } ");
		queryStr.append("  INSERT { ?subject meta:forProject ?new_project } ");
		queryStr.append("WHERE { ?subject meta:forProject ?project . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateDataReferenceOfDataset(Dataset dataset) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_datareference", dataset.getDataReference().toString());
		queryStr.append("  DELETE { ?subject meta:dataReference ?datareference } ");
		queryStr.append("  INSERT { ?subject meta:dataReference ?new_datareference } ");
		queryStr.append("WHERE { ?subject meta:dataReference ?datareference . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateOrganisationOfDataset(Dataset dataset) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_organisation", dataset.getOrganisation().toString());
		queryStr.append("  DELETE { ?subject meta:hasOwner ?organisation } ");
		queryStr.append("  INSERT { ?subject meta:hasOwner ?new_organisation } ");
		queryStr.append("WHERE { ?subject meta:hasOwner ?organisation . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateContactOfDataset(Dataset dataset) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_contact", dataset.getContact().toString());
		queryStr.append("  DELETE { ?subject meta:contactPerson ?contact } ");
		queryStr.append("  INSERT { ?subject meta:contactPerson ?new_contact } ");
		queryStr.append("WHERE { ?subject meta:contactPerson ?organisation . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateTopicOfDataset(Dataset dataset) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setIri("subject", dataset.getDatasetUri());
		queryStr.setIri("new_topic", dataset.getTopic().toString());
		queryStr.append("  DELETE { ?subject meta:hasTopic ?topic } ");
		queryStr.append("  INSERT { ?subject meta:hasTopic ?new_topic } ");
		queryStr.append("WHERE { ?subject meta:hasTopic ?topic . ");
		queryStr.append("}");

		fuseki.update(queryStr);
	}

	private void updateDecimalSymbolOfDataset(Dataset dataset) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
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
		queryStr.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		URI uri = new URI("https://w3id.org/metadata#" + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		URI uri = new URI("https://w3id.org/metadata#" + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		URI uri = new URI("https://w3id.org/metadata#" + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		URI uri = new URI("https://w3id.org/meta#" + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
		queryStr.append("SELECT DISTINCT ?uri ?label ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:RoadPart ;");
		queryStr.append("    rdfs:label ?label ;");
		queryStr.append("    meta:endRoadNetworkLocation ?endLocation ;");
		queryStr.append("    meta:startRoadNetworkLocation ?startLocation .");
		queryStr.append("  ?endLocation meta:hectometerPostReference ?end ;");
		queryStr.append("    meta:laneReference ?lane ;");
		queryStr.append("    meta:roadReference ?road ;");
		queryStr.append("    meta:wayReference ?way .");
		queryStr.append("  ?startLocation meta:hectometerPostReference ?start .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");
		JsonNode responseNodes = fuseki.query(queryStr);
		InfraObject infraObject = null;
		for (JsonNode node : responseNodes) {
			URI uri = new URI(node.get("uri").get("value").asText());
			String label = node.get("label").get("value").asText();
			String road = node.get("road").get("value").asText();
			String way = node.get("way").get("value").asText();
			String lane = node.get("lane").get("value").asText();
			Double start = Double.parseDouble(node.get("start").get("value").textValue().replace(',', '.'));
			Double end = Double.parseDouble(node.get("end").get("value").textValue().replace(',', '.'));
			infraObject = new InfraObject(uri, label, road, way, lane, start, end);
			infraObjects.add(infraObject);
		}
		return infraObjects;
	}

	public InfraObject getInfraObject(String localName) throws IOException, URISyntaxException {
		URI uri = new URI("https://w3id.org/meta#" + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
		queryStr.setIri("uri", uri.toString());
		queryStr.append("SELECT DISTINCT ?label ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?uri rdf:type meta:RoadPart ;");
		queryStr.append("    rdfs:label ?label ;");
		queryStr.append("    meta:endRoadNetworkLocation ?endLocation ;");
		queryStr.append("    meta:startRoadNetworkLocation ?startLocation .");
		queryStr.append("  ?endLocation meta:hectometerPostReference ?end ;");
		queryStr.append("    meta:laneReference ?lane ;");
		queryStr.append("    meta:roadReference ?road ;");
		queryStr.append("    meta:wayReference ?way .");
		queryStr.append("  ?startLocation meta:hectometerPostReference ?start .");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);
		InfraObject infraObject = null;
		for (JsonNode node : responseNodes) {
			String label = node.get("label").get("value").asText();
			String road = node.get("road").get("value").asText();
			String way = node.get("way").get("value").asText();
			String lane = node.get("lane").get("value").asText();
			Double start = Double.parseDouble(node.get("start").get("value").textValue().replace(',', '.'));
			Double end = Double.parseDouble(node.get("end").get("value").textValue().replace(',', '.'));
			infraObject = new InfraObject(uri, label, road, way, lane, start, end);
		}
		return infraObject;
	}

	public List<QuantityKindAndUnit> getAllQuantities() throws IOException, URISyntaxException {
		List<QuantityKindAndUnit> quantities = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
		URI uri = new URI("https://w3id.org/metadata#" + localName);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.setNsPrefix("dce", "http://purl.org/dc/elements/1.1/");
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
