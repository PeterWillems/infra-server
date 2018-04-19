package nl.tno.willemsph.infra.dataset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
		queryStr.append(
				"SELECT DISTINCT ?dataset ?datasetLabel ?dataReference ?projectLabel ?ownerLabel ?contactLabel ?decimalSymbol ?separator ?format ?infraLabel ?road ?way ?lane ?start ?end ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdf:type meta:DataSet ;");
		queryStr.append("    rdfs:label ?datasetLabel ;");
		queryStr.append("    meta:forProject ?project ;");
		queryStr.append("    meta:hasOwner ?owner ;");
		queryStr.append("    meta:contactPerson ?contact ;");
		queryStr.append("    meta:csvDecimalSymbol ?decimalSymbol ;");
		queryStr.append("    meta:csvSeparator ?separator ;");
		queryStr.append("    meta:hasFormat ?format ;");
		queryStr.append("    meta:dataReference ?dataReference ;");
		queryStr.append("    meta:relatedToInfraObject ?infraObject .");
		queryStr.append("  ?project rdfs:label ?projectLabel .");
		queryStr.append("  ?owner rdfs:label ?ownerLabel .");
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
		for (JsonNode node : responseNodes) {
			String datasetLabel = node.get("datasetLabel").get("value").asText();
			URI dataReference = new URI(node.get("dataReference").get("value").asText());
			// getDataReference(dataReference);
			String projectLabel = node.get("projectLabel").get("value").asText();
			String ownerLabel = node.get("ownerLabel").get("value").asText();
			String contactLabel = node.get("contactLabel").get("value").asText();
			URI decimalSymbol = new URI(node.get("decimalSymbol").get("value").asText());
			URI separator = new URI(node.get("separator").get("value").asText());
			URI format = new URI(node.get("format").get("value").asText());
			String infraLabel = node.get("infraLabel").get("value").asText();
			String road = node.get("road").get("value").asText();
			String way = node.get("way").get("value").asText();
			String lane = node.get("lane").get("value").asText();
			Double start = Double.parseDouble(node.get("start").get("value").textValue().replace(',', '.'));
			Double end = Double.parseDouble(node.get("end").get("value").textValue().replace(',', '.'));
			Dataset dataset = new Dataset(datasetLabel, dataReference, decimalSymbol, separator, format, projectLabel,
					ownerLabel, contactLabel, infraLabel, road, way, lane, start, end);
			List<String> measurementYears = getMeasurementYears(node.get("dataset").get("value").asText());
			dataset.setMeasurementYears(measurementYears);
			datasets.add(dataset);
		}
		return datasets;
	}

	private List<String> getMeasurementYears(String datasetUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setIri("dataset", datasetUri);
		queryStr.setNsPrefix("meta", "https://w3id.org/meta#");
		queryStr.setNsPrefix("metadata", "https://w3id.org/metadata#");
		queryStr.append("SELECT  ?year ");
		queryStr.append("WHERE {");
		queryStr.append("  ?dataset rdf:type meta:DataSet ;");
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

}
