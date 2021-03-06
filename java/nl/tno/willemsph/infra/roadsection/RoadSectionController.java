package nl.tno.willemsph.infra.roadsection;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.tno.willemsph.infra.SparqlService;

@RestController
public class RoadSectionController {

	@Autowired
	private RoadSectionService roadSectionService;
	@Autowired
	private SparqlService fuseki;

	@CrossOrigin
	@RequestMapping("/roadsection/properties")
	public List<RoadSectionProperty> getRoadSectionProperty() throws IOException {
		return roadSectionService.getProperties();
	}

	@CrossOrigin
	@RequestMapping("/roadsection/properties/{localName}")
	public RoadSectionProperty getRoadSectionProperty(@PathVariable() String localName) throws IOException {
		return roadSectionService.getProperty(localName);
	}

	@CrossOrigin
	@RequestMapping("/roadsections")
	public List<RoadSection> getAllRoadSections(@RequestParam("road") Optional<String> roadId,
			@RequestParam("direction") Optional<Boolean> direction,
			@RequestParam("beginKilometer") Optional<Double> beginKilometer,
			@RequestParam("endKilometer") Optional<Double> endKilometer,
			@RequestParam("drivewaySubtype") Optional<String> drivewaySubtype) throws IOException {
		return roadSectionService.getAllRoadSections(roadId, direction, beginKilometer, endKilometer, drivewaySubtype);
	}

	@CrossOrigin
	@RequestMapping("/roadsections/{id}")
	public RoadSection getRoadSection(@PathVariable long id) throws IOException {
		return roadSectionService.getRoadSection(id);
	}

	@CrossOrigin
	@RequestMapping("/roadsections/{id}/geometry")
	public Geometry getRoadSectionGeometry(@PathVariable long id) throws IOException {
		return roadSectionService.getRoadSectionGeometry(id);
	}

	@CrossOrigin
	@RequestMapping("/roadsections/{id}/properties/{localName}")
	public String getRoadSectionPropertyValue(@PathVariable long id, @PathVariable() String localName)
			throws IOException {
		return roadSectionService.getRoadSectionPropertyValue(id, localName);
	}

	@CrossOrigin
	@RequestMapping("/roads")
	public List<String> getRoadNumbers() {
		return fuseki.getRoadNumbers();
	}

	@CrossOrigin
	@RequestMapping("/roadsections/drivewaysubtypes")
	public List<DrivewaySubtype> getDrivewaySubtypes() throws IOException {
		return roadSectionService.getDrivewaySubtypes();
	}

	@CrossOrigin
	@RequestMapping("/civilstructures")
	public List<CivilStructure> getAllCivilStructures(@RequestParam("road") Optional<String> roadId,
			@RequestParam("direction") Optional<Boolean> direction,
			@RequestParam("beginKilometer") Optional<Double> beginKilometer,
			@RequestParam("endKilometer") Optional<Double> endKilometer) throws IOException {
		return roadSectionService.getAllCivilStructures(roadId, direction, beginKilometer, endKilometer);
	}

	@CrossOrigin
	@RequestMapping("/civilstructures/{localName}/")
	public CivilStructure getCivilStructure(@PathVariable() String localName) throws IOException {
		return roadSectionService.getCivilStructure(localName);
	}

	@CrossOrigin
	@RequestMapping("/civilstructures/{localName}/geometry")
	public Geometry getCivilStructureGeometry(@PathVariable() String localName) throws IOException {
		return roadSectionService.getCivilStructureGeometry(SparqlService.HOOFDWEGENNET_DATA + "#" + localName);
	}

}
