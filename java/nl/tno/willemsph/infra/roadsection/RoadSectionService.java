package nl.tno.willemsph.infra.roadsection;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.infra.SparqlService;

@Service
public class RoadSectionService {

	@Autowired
	private SparqlService fuseki;
	private Map<URI, DrivewayPosition> drivewayPositions;
	private Map<URI, DrivewaySubtype> drivewaySubtypes;
	private Map<URI, RelativePosition> relativePositions;
	private Map<URI, RoadAuthorityType> roadAuthorityTypes;

	public List<Long> getAllRoadSectionIds() throws IOException {
		List<Long> roadSectionIds = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.append("SELECT ?roadSectionId ");
		queryStr.append("{");
		queryStr.append("  ?roadsection rdf:type nwb:RoadSection ;");
		queryStr.append("  nwb:roadSectionId ?roadSectionId .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?roadSectionId");

		JsonNode responseNodes = fuseki.query(queryStr);
		for (JsonNode node : responseNodes) {
			roadSectionIds.add(node.get("roadSectionId").get("value").asLong());
		}
		return roadSectionIds;
	}

	public List<Long> getAllRoadSectionIdsForRoad(String roadId, Optional<Boolean> right,
			Optional<Double> beginKilometer, Optional<Double> endKilometer) throws IOException {
		List<Long> roadSectionIds = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setLiteral("roadNumber", roadId);
		queryStr.append("SELECT ?roadSectionId ");
		queryStr.append("{");
		queryStr.append("  ?roadsection rdf:type nwb:RoadSection ;");
		queryStr.append("  nwb:roadSectionId ?roadSectionId ;");
		if (right.isPresent()) {
			if (right.get()) {
				queryStr.append("  nwb:relativePosition nwb:RelativePositionRight ;");
			} else {
				queryStr.append("  nwb:relativePosition nwb:RelativePositionLeft ;");
			}
		}
		if (beginKilometer.isPresent() || endKilometer.isPresent()) {
			queryStr.append("  nwb:roadNumber ?roadNumber ;");
			queryStr.append("  nwb:beginKilometer ?beginKilometer ;");
			queryStr.append("  nwb:endKilometer ?endKilometer ;");
			queryStr.append("FILTER (");
			if (beginKilometer.isPresent()) {
				queryStr.append("((?beginKilometer < " + beginKilometer.get() + ") && (?endKilometer > "
						+ beginKilometer.get() + "))");
			}
			if (beginKilometer.isPresent() && endKilometer.isPresent()) {
				queryStr.append(" || ((?beginKilometer > " + beginKilometer.get() + ") && (?endKilometer < "
						+ endKilometer.get() + ")) || ");
			}
			if (endKilometer.isPresent()) {
				queryStr.append("((?beginKilometer < " + endKilometer.get() + ") && (?endKilometer > "
						+ endKilometer.get() + "))");
			}
			queryStr.append(")");
		} else {
			queryStr.append("  nwb:roadNumber ?roadNumber .");
		}
		queryStr.append("}");
		queryStr.append("ORDER BY ?roadSectionId");

		JsonNode responseNodes = fuseki.query(queryStr);
		for (JsonNode node : responseNodes) {
			roadSectionIds.add(node.get("roadSectionId").get("value").asLong());
		}
		return roadSectionIds;
	}

	public List<RoadSection> getAllRoadSections(Optional<String> roadId, Optional<Boolean> direction,
			Optional<Double> beginKilometer, Optional<Double> endKilometer, Optional<String> drivewaySubtype)
			throws IOException {
		List<RoadSection> roadSections = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		if (roadId.isPresent()) {
			queryStr.setIri("graph", SparqlService.HOOFDWEGENNET_DATA + "/R" + roadId.get());
		}
		queryStr.append("SELECT ?roadSectionId ?nwbProperty ?propertyValue ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("  ?roadsection rdf:type nwb:RoadSection ;");
		queryStr.append("  nwb:roadSectionId ?roadSectionId ;");
		queryStr.append("	?nwbProperty ?propertyValue ");
		if (roadId.isPresent()) {
			queryStr.setLiteral("roadNumber", roadId.get());
			queryStr.append(";  nwb:roadNumber ?roadNumber ");
		}
		if (direction.isPresent()) {
			if (direction.get()) {
				queryStr.append(";  nwb:relativePosition nwb:RelativePositionRight ");
			} else {
				queryStr.append(";  nwb:relativePosition nwb:RelativePositionLeft ");
			}
		}
		if (beginKilometer.isPresent() || endKilometer.isPresent()) {
			queryStr.append(";  nwb:beginKilometer ?beginKilometer ");
			queryStr.append(";  nwb:endKilometer ?endKilometer ");
			queryStr.append("FILTER (");
			if (beginKilometer.isPresent()) {
				queryStr.append("((?beginKilometer < " + beginKilometer.get() + ") && (?endKilometer > "
						+ beginKilometer.get() + "))");
			}
			if (beginKilometer.isPresent() && endKilometer.isPresent()) {
				queryStr.append(" || ((?beginKilometer > " + beginKilometer.get() + ") && (?endKilometer < "
						+ endKilometer.get() + ")) || ");
			}
			if (endKilometer.isPresent()) {
				queryStr.append("((?beginKilometer < " + endKilometer.get() + ") && (?endKilometer > "
						+ endKilometer.get() + "))");
			}
			queryStr.append(")");
		}
		queryStr.append(". ");
		if (drivewaySubtype.isPresent()) {
			queryStr.setLiteral("drivewaySubtypeCode", drivewaySubtype.get());
			queryStr.append("?roadsection  nwb:drivewaySubtype ?drivewaySubtype . ");
		}
		queryStr.append(" }");
		if (drivewaySubtype.isPresent()) {
			queryStr.append("?drivewaySubtype  nwb:drivewaySubtypeCode ?drivewaySubtypeCode . ");
		}
		queryStr.append("} ");
		queryStr.append("ORDER BY ?roadSectionId");

		JsonNode responseNodes = fuseki.query(queryStr);
		Long currentRoadSectionId = null;
		RoadSection currentRoadSection = null;
		for (JsonNode node : responseNodes) {
			Long roadSectionId = node.get("roadSectionId").get("value").asLong();
			if (!roadSectionId.equals(currentRoadSectionId)) {
				currentRoadSectionId = roadSectionId;
				currentRoadSection = new RoadSection(currentRoadSectionId);
				roadSections.add(currentRoadSection);
			}
			getProperty(roadSectionId, roadId, node.get("nwbProperty").get("value").asText(),
					node.get("propertyValue").get("value").asText(), currentRoadSection);
		}

		return roadSections;
	}

	public RoadSection getRoadSection(long id) throws IOException {
		RoadSection roadSection = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setLiteral("id", id);
		queryStr.append("SELECT ?graph ?nwbProperty ?propertyValue ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?roadsection rdf:type nwb:RoadSection ;");
		queryStr.append("    nwb:roadSectionId ?id ;");
		queryStr.append("    ?nwbProperty ?propertyValue .");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = fuseki.query(queryStr);
		if (responseNodes.size() > 0) {
			roadSection = new RoadSection(id);
		}
		for (JsonNode node : responseNodes) {
			getProperty(id, null, node.get("nwbProperty").get("value").asText(),
					node.get("propertyValue").get("value").asText(), roadSection);
		}

		return roadSection;
	}

	public String getRoadSectionPropertyValue(long id, String localName) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setLiteral("id", id);
		queryStr.setIri("nwbProperty", queryStr.getNsPrefixURI("nwb") + localName);
		queryStr.append("SELECT ?propertyValue ");
		queryStr.append("{");
		queryStr.append("  ?roadsection rdf:type nwb:RoadSection ;");
		queryStr.append("  nwb:roadSectionId ?id ;");
		queryStr.append("  ?nwbProperty ?propertyValue .");
		queryStr.append("}");

		JsonNode responseNodes = fuseki.query(queryStr);
		if (responseNodes.size() > 0) {
			for (JsonNode node : responseNodes) {
				return node.get("propertyValue").get("value").toString();
			}
		}
		return null;
	}

	public Geometry getRoadSectionGeometry(long id) throws IOException {
		Geometry geometry = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setLiteral("id", id);
		queryStr.append("SELECT ?graph ?x ?y ?lat ?lng ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?wegvak rdf:type nwb:RoadSection ; ");
		queryStr.append("      nwb:roadSectionId ?id ; ");
		queryStr.append("      ( nwb:geometry | list:hasNext )+ ?geometrie . ");
		queryStr.append("    ?geometrie list:hasContents  ?coordinate . ");
		queryStr.append("    ?coordinate nwb:lat ?lat ; ");
		queryStr.append("      nwb:lng ?lng ; ");
		queryStr.append("      nwb:x ?x ; ");
		queryStr.append("      nwb:y ?y . ");
		queryStr.append("  } ");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);

		geometry = new Geometry();
		List<MultiLineString> multiLineStrings = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			Coordinate coordinate = new Coordinate(node.get("lat").get("value").asDouble(),
					node.get("lng").get("value").asDouble(), node.get("x").get("value").asDouble(),
					node.get("y").get("value").asDouble());
			MultiLineString multiLineString = new MultiLineString(coordinate);
			multiLineStrings.add(multiLineString);
		}
		geometry.setMultiLineString(multiLineStrings);
		return geometry;
	}

	public Geometry getRoadSectionGeometryForRoad(long id, String road) throws IOException {
		Geometry geometry = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.setLiteral("id", id);
		queryStr.append("SELECT DISTINCT ?x ?y ?lat ?lng ");
		queryStr.append("{");
		queryStr.append("  GRAPH <" + SparqlService.HOOFDWEGENNET_DATA + "/R" + road + "> { ");
		queryStr.append("  ?wegvak rdf:type nwb:RoadSection ;");
		queryStr.append("    nwb:roadSectionId ?id ;");
		queryStr.append("    ( nwb:geometry | list:hasNext )+ ?geometrie .");
		queryStr.append("  ?geometrie list:hasContents  ?coordinate .");
		queryStr.append("  ?coordinate nwb:lat ?lat ;");
		queryStr.append("    nwb:lng ?lng ;");
		queryStr.append("    nwb:x ?x ;");
		queryStr.append("    nwb:y ?y .");
		queryStr.append("  } ");
		queryStr.append("}");
		JsonNode responseNodes = fuseki.query(queryStr);

		geometry = new Geometry();
		List<MultiLineString> multiLineStrings = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			Coordinate coordinate = new Coordinate(node.get("lat").get("value").asDouble(),
					node.get("lng").get("value").asDouble(), node.get("x").get("value").asDouble(),
					node.get("y").get("value").asDouble());
			MultiLineString multiLineString = new MultiLineString(coordinate);
			multiLineStrings.add(multiLineString);
		}
		geometry.setMultiLineString(multiLineStrings);
		return geometry;
	}

	private void getProperty(Long roadSectionId, Optional<String> roadId, String propertyUri, String value,
			RoadSection roadSection) throws IOException {
		int indexOfHashMark = propertyUri.indexOf('#');
		switch (propertyUri.substring(indexOfHashMark)) {
		case "#administrativeDirection":
			roadSection.setAdministrativeDirection(Boolean.parseBoolean(value));
			break;
		case "#beginDistance":
			roadSection.setBeginDistance(Integer.parseInt(value));
			break;
		case "#beginJunction":
			roadSection.setBeginJunction(URI.create(value));
			break;
		case "#beginKilometer":
			roadSection.setBeginKilometer(Double.parseDouble(value));
			break;
		case "#drivewayPosition":
			DrivewayPosition drivewayPosition = getDrivewayPositions().get(URI.create(value));
			roadSection.setDrivewayPosition(drivewayPosition);
			break;
		case "#drivewaySubtype":
			DrivewaySubtype drivewaySubtype = getDrivewaySubtypesMap().get(URI.create(value));
			roadSection.setDrivewaySubtype(drivewaySubtype);
			break;
		case "#drivingDirection":
			roadSection.setDrivingDirection(Boolean.parseBoolean(value));
			break;
		case "#endDistance":
			roadSection.setEndDistance(Integer.parseInt(value));
			break;
		case "#endJunction":
			roadSection.setEndJunction(URI.create(value));
			break;
		case "#endKilometer":
			roadSection.setEndKilometer(Double.parseDouble(value));
			break;
		case "#geometry":
			if (roadId != null && roadId.isPresent()) {
				Geometry roadSectionGeometry = getRoadSectionGeometryForRoad(roadSectionId, roadId.get());
				roadSection.setGeometry(roadSectionGeometry);
			} else {
				Geometry roadSectionGeometry = getRoadSectionGeometry(roadSectionId);
				roadSection.setGeometry(roadSectionGeometry);
			}
			break;
		case "#hectoLetter":
			roadSection.setHectoLetter(value);
			break;
		case "#municipalityId":
			roadSection.setMunicipalityId(Integer.parseInt(value));
			break;
		case "#municipalityName":
			roadSection.setMunicipalityName(value);
			break;
		case "#relativePosition":
			RelativePosition relativePosition = getRelativePositions().get(URI.create(value));
			roadSection.setRelativePosition(relativePosition);
			break;
		case "#residence":
			roadSection.setResidence(value);
			break;
		case "#roadAuthorityId":
			roadSection.setRoadAuthorityId(value);
			break;
		case "#roadAuthorityName":
			roadSection.setRoadAuthorityName(value);
			break;
		case "#roadAuthorityType":
			RoadAuthorityType roadAuthorityType = getRoadAuthorityTypes().get(URI.create(value));
			roadSection.setRoadAuthorityType(roadAuthorityType);
			break;
		case "#roadNumber":
			roadSection.setRoadNumber(value);
			break;
		case "#roadPartLetter":
			roadSection.setRoadPartLetter(value);
			break;
		case "#roadSectionId":
			roadSection.setId(Long.parseLong(value));
			roadSection.setRoadSectionId(Long.parseLong(value));
			break;
		case "#startDate":
			LocalDate localDate = LocalDate.parse(value);
			roadSection.setStartDate(new StartDate(localDate));
			break;
		case "#streetName":
			roadSection.setStreetName(value);
			break;
		}
	}

	private Map<URI, DrivewayPosition> getDrivewayPositions() throws IOException {
		if (drivewayPositions == null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			String uri = queryStr.getNsPrefixURI("nwb") + "DrivewayPosition";
			queryStr.setIri("uri", uri);
			queryStr.append("SELECT ?instance ?labelEn ?labelNl ?code ");
			queryStr.append("{");
			queryStr.append("  ?instance rdf:type nwb:DrivewayPosition ;");
			queryStr.append("    nwb:drivewayPositionCode ?code ;");
			queryStr.append("    rdfs:label ?labelEn ;");
			queryStr.append("    rdfs:label ?labelNl .");
			queryStr.append("    FILTER ( lang(?labelEn) = 'en' )");
			queryStr.append("    FILTER ( lang(?labelNl) = 'nl' )");
			queryStr.append("}");
			JsonNode responseNodes = fuseki.query(queryStr);
			drivewayPositions = new HashMap<>();
			for (JsonNode node : responseNodes) {
				URI instance = URI.create(node.get("instance").get("value").asText());
				String labelEn = node.get("labelEn").get("value").asText();
				String labelNl = node.get("labelNl").get("value").asText();
				String code = node.get("code").get("value").asText();
				drivewayPositions.put(instance, new DrivewayPosition(instance, labelEn, labelNl, code));
			}
		}
		return drivewayPositions;
	}

	public List<DrivewaySubtype> getDrivewaySubtypes() throws IOException {
		ArrayList<DrivewaySubtype> drivewaySubtypeList = new ArrayList<>(getDrivewaySubtypesMap().values());
		drivewaySubtypeList.sort(new Comparator<DrivewaySubtype>() {

			@Override
			public int compare(DrivewaySubtype arg0, DrivewaySubtype arg1) {
				return arg0.getDrivewaySubtypeCode().compareToIgnoreCase(arg1.getDrivewaySubtypeCode());
			}
		});
		return drivewaySubtypeList;
	}

	private Map<URI, DrivewaySubtype> getDrivewaySubtypesMap() throws IOException {
		if (drivewaySubtypes == null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			String uri = queryStr.getNsPrefixURI("nwb") + "DrivewaySubtype";
			queryStr.setIri("uri", uri);
			queryStr.append("SELECT ?instance ?labelEn ?labelNl ?code ");
			queryStr.append("{");
			queryStr.append("  ?instance rdf:type nwb:DrivewaySubtype ;");
			queryStr.append("    nwb:drivewaySubtypeCode ?code ;");
			queryStr.append("    rdfs:label ?labelEn ;");
			queryStr.append("    rdfs:label ?labelNl .");
			queryStr.append("    FILTER ( lang(?labelEn) = 'en' )");
			queryStr.append("    FILTER ( lang(?labelNl) = 'nl' )");
			queryStr.append("}");
			queryStr.append("ORDER BY ?code");
			JsonNode responseNodes = fuseki.query(queryStr);
			drivewaySubtypes = new HashMap<>();
			for (JsonNode node : responseNodes) {
				URI instance = URI.create(node.get("instance").get("value").asText());
				String labelEn = node.get("labelEn").get("value").asText();
				String labelNl = node.get("labelNl").get("value").asText();
				String code = node.get("code").get("value").asText();
				drivewaySubtypes.put(instance, new DrivewaySubtype(instance, labelEn, labelNl, code));
			}
		}
		return drivewaySubtypes;
	}

	private Map<URI, RelativePosition> getRelativePositions() throws IOException {
		if (relativePositions == null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			String uri = queryStr.getNsPrefixURI("nwb") + "RelativePosition";
			queryStr.setIri("uri", uri);
			queryStr.append("SELECT ?instance ?labelEn ?labelNl ?code ");
			queryStr.append("{");
			queryStr.append("  ?instance rdf:type nwb:RelativePosition ;");
			queryStr.append("    nwb:relativePositionCode ?code ;");
			queryStr.append("    rdfs:label ?labelEn ;");
			queryStr.append("    rdfs:label ?labelNl .");
			queryStr.append("    FILTER ( lang(?labelEn) = 'en' )");
			queryStr.append("    FILTER ( lang(?labelNl) = 'nl' )");
			queryStr.append("}");
			JsonNode responseNodes = fuseki.query(queryStr);
			relativePositions = new HashMap<>();
			for (JsonNode node : responseNodes) {
				URI instance = URI.create(node.get("instance").get("value").asText());
				String labelEn = node.get("labelEn").get("value").asText();
				String labelNl = node.get("labelNl").get("value").asText();
				String code = node.get("code").get("value").asText();
				relativePositions.put(instance, new RelativePosition(instance, labelEn, labelNl, code));
			}
		}
		return relativePositions;
	}

	private Map<URI, RoadAuthorityType> getRoadAuthorityTypes() throws IOException {
		if (roadAuthorityTypes == null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
			String uri = queryStr.getNsPrefixURI("nwb") + "RoadAuthorityType";
			queryStr.setIri("uri", uri);
			queryStr.append("SELECT ?instance ?labelEn ?labelNl ?code ");
			queryStr.append("{");
			queryStr.append("  ?instance rdf:type nwb:RoadAuthorityType ;");
			queryStr.append("    nwb:roadAuthorityTypeCode ?code ;");
			queryStr.append("    rdfs:label ?labelEn ;");
			queryStr.append("    rdfs:label ?labelNl .");
			queryStr.append("    FILTER ( lang(?labelEn) = 'en' )");
			queryStr.append("    FILTER ( lang(?labelNl) = 'nl' )");
			queryStr.append("}");
			JsonNode responseNodes = fuseki.query(queryStr);
			roadAuthorityTypes = new HashMap<>();
			for (JsonNode node : responseNodes) {
				URI instance = URI.create(node.get("instance").get("value").asText());
				String labelEn = node.get("labelEn").get("value").asText();
				String labelNl = node.get("labelNl").get("value").asText();
				String code = node.get("code").get("value").asText();
				roadAuthorityTypes.put(instance, new RoadAuthorityType(instance, labelEn, labelNl, code));
			}
		}
		return roadAuthorityTypes;
	}

	public RoadSectionProperty getProperty(String localName) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		String uri = queryStr.getNsPrefixURI("nwb") + localName;
		queryStr.setIri("uri", uri);
		queryStr.append("SELECT ?labelEn ?labelNl ?range");
		queryStr.append("{");
		queryStr.append("  ?uri rdfs:domain nwb:RoadSection ;");
		queryStr.append("    rdfs:range ?range ;");
		queryStr.append("    rdfs:label ?labelEn ;");
		queryStr.append("    rdfs:label ?labelNl .");
		queryStr.append("    FILTER ( lang(?labelEn) = 'en' )");
		queryStr.append("    FILTER ( lang(?labelNl) = 'nl' )");
		queryStr.append("}");
		JsonNode node = fuseki.query(queryStr);
		String labelEn = node.get(0).get("labelEn").get("value").asText();
		String labelNl = node.get(0).get("labelNl").get("value").asText();
		String range = node.get(0).get("range").get("value").asText();
		return new RoadSectionProperty(URI.create(uri), labelEn, labelNl, URI.create(range));
	}

	public List<RoadSectionProperty> getProperties() throws IOException {
		List<RoadSectionProperty> properties = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(fuseki.getPrefixMapping());
		queryStr.append("SELECT ?pUri ?labelEn ?labelNl ?range ");
		queryStr.append("{");
		queryStr.append("  ?pUri rdfs:domain nwb:RoadSection ;");
		queryStr.append("    rdfs:range ?range ;");
		queryStr.append("    rdfs:label ?labelEn ;");
		queryStr.append("    rdfs:label ?labelNl .");
		queryStr.append("    FILTER ( lang(?labelEn) = 'en' )");
		queryStr.append("    FILTER ( lang(?labelNl) = 'nl' )");
		queryStr.append("}");
		queryStr.append("ORDER BY ?pUri");
		JsonNode responseNodes = fuseki.query(queryStr);
		for (JsonNode node : responseNodes) {
			String pUri = node.get("pUri").get("value").asText();
			switch (pUri) {
			case "https://w3id.org/nwb/hoofdwegennet#houseNumberStructureLeft":
			case "https://w3id.org/nwb/hoofdwegennet#houseNumberStructureRight":
			case "https://w3id.org/nwb/hoofdwegennet#firstHouseNumberLeft":
			case "https://w3id.org/nwb/hoofdwegennet#firstHouseNumberRight":
			case "https://w3id.org/nwb/hoofdwegennet#lastHouseNumberLeft":
			case "https://w3id.org/nwb/hoofdwegennet#lastHouseNumberRight":
				continue;
			default:
				break;
			}
			String labelEn = node.get("labelEn").get("value").asText();
			String labelNl = node.get("labelNl").get("value").asText();
			String range = node.get("range").get("value").asText();
			System.out.println(pUri);
			RoadSectionProperty roadSectionProperty = new RoadSectionProperty(URI.create(pUri), labelEn, labelNl,
					URI.create(range));
			properties.add(roadSectionProperty);
		}
		return properties;
	}

}
