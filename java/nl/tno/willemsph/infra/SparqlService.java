package nl.tno.willemsph.infra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SparqlService {
	public static final String QUERY_URL = "http://localhost:3330/rdf/query";
	public static final String UPDATE_URL = "http://localhost:3330/rdf/update";
	public static final String LIST_NS = "https://w3id.org/list#";
	public static final String HOOFDWEGENNET_NS = "https://w3id.org/nwb/hoofdwegennet#";
	public static final String HOOFDWEGENNET_DATA = "https://w3id.org/nwb/hoofdwegennet/data";
	private static final String NWB_RESOURCE = "hoofdwegennet2.ttl";
	private static final String META_RESOURCE = "meta.ttl";
	private static final String METADATA_RESOURCE = "metadata.ttl";
	private static final String[] ROAD = { //
			"001", "002", "003", "004", "005", "006", "007", "008", "009", "010", //
			"011", "012", "013", "014", "015", "016", "017", "020", "022", "027", "028", //
			"030", "031", "032", "033", "035", "036", "037", "038", "044", "046", "048", //
			"050", "057", "058", "059", "061", "065", "067", "073", "074", "076", "077", "079", //
			"099", "200", "205", "708", "783", "835", "838", "915"
	};

	private FusekiServer fuseki;
	private Dataset ds;
	private PrefixMapping prefixMapping;

	public SparqlService() throws IOException {
		Model defaultModel = ModelFactory.createDefaultModel();

		Resource nwb = new ClassPathResource(NWB_RESOURCE);
		Resource meta = new ClassPathResource(META_RESOURCE);
		Resource metadata = new ClassPathResource(METADATA_RESOURCE);

		defaultModel.read(nwb.getInputStream(), null, "TURTLE");
		defaultModel.read(meta.getInputStream(), null, "TURTLE");
		defaultModel.read(metadata.getInputStream(), null, "TURTLE");

		ds = DatasetFactory.create(defaultModel);
		for (String road : ROAD) {
			Model namedModel = ModelFactory.createDefaultModel();
			ClassPathResource roadResource = new ClassPathResource("RW" + road + ".zip");
			readModelResource(roadResource, namedModel);
			ds.addNamedModel(HOOFDWEGENNET_DATA + "/R" + road, namedModel);
		}

		fuseki = FusekiServer.create().add("/rdf", ds).build();
		fuseki.start();
	}

	private void readModelResource(Resource rw001, Model model) throws IOException {
		ZipInputStream zipIn = new ZipInputStream(rw001.getInputStream());
		ZipEntry zipEntry = zipIn.getNextEntry();
		System.out.println("Loading : " + zipEntry.getName());
		model.read(zipIn, null, "TURTLE");
		zipIn.close();
	}

	public void stopServer() {
		if (fuseki != null)
			fuseki.stop();
	}

	public JsonNode query(ParameterizedSparqlString queryStr) throws IOException {
		String query = queryStr.toString();
		HttpURLConnection con = getQueryConnection();
		JsonNode bindings = send(con, query);
		return bindings;
	}
	
	public void update(ParameterizedSparqlString queryStr) throws IOException {
		String query = queryStr.toString();
		HttpURLConnection con = getUpdateConnection();
		sendUpdate(con, query);
	}

	public PrefixMapping getPrefixMapping() {
		if (prefixMapping == null) {
			prefixMapping = PrefixMapping.Factory.create();
			prefixMapping.setNsPrefix("rdf", RDF.uri);
			prefixMapping.setNsPrefix("rdfs", RDFS.uri);
			prefixMapping.setNsPrefix("owl", OWL.NS);
			prefixMapping.setNsPrefix("xml", "http://www.w3.org/XML/1998/namespace/");
			prefixMapping.setNsPrefix("nwb", HOOFDWEGENNET_NS);
			prefixMapping.setNsPrefix("list", LIST_NS);
		}
		return prefixMapping;
	}

	private HttpURLConnection getQueryConnection() throws IOException {
		URL obj = new URL(QUERY_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/sparql-query");
		return con;
	}
	
	private HttpURLConnection getUpdateConnection() throws IOException {
		URL obj = new URL(UPDATE_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/sparql-update");
		return con;
	}
	
	private void sendUpdate(HttpURLConnection con, String query) throws IOException {
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + SparqlService.class);
		System.out.println("Post parameters : " + query);
		System.out.println("Response Code : " + responseCode);
	}

	private JsonNode send(HttpURLConnection con, String query) throws IOException {
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + SparqlService.class);
		System.out.println("Post parameters : " + query);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jTree = mapper.readTree(in);
		JsonNode bindings = jTree.findValue("bindings");
		return bindings;
	}

	public List<String> getRoadNumbers() {
		return Arrays.asList(ROAD);
	}
}
