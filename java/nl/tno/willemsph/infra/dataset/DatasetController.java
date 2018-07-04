package nl.tno.willemsph.infra.dataset;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatasetController {

	@Autowired
	private DatasetService datasetService;

	@CrossOrigin
	@RequestMapping("/datasets")
	public List<Dataset> getAllDatasets() throws IOException, URISyntaxException {
		return datasetService.getAllDatasets();
	}

	@CrossOrigin
	@RequestMapping("/datasets/{localName}")
	public Dataset getDataset(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getDataset(localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/datasets/{localName}")
	public Dataset updateDataset(@PathVariable String localName, @RequestBody Dataset dataset)
			throws URISyntaxException, IOException {
		return datasetService.updateDataset(localName, dataset);
	}

	@CrossOrigin
	@RequestMapping("/projects")
	public List<Project> getAllProjects() throws IOException, URISyntaxException {
		return datasetService.getAllProjects();
	}

	@CrossOrigin
	@RequestMapping("/projects/{localName}")
	public Project getProject(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getProject(localName);
	}

	@CrossOrigin
	@RequestMapping("/organisations")
	public List<Organisation> getAllOrganisations() throws IOException, URISyntaxException {
		return datasetService.getAllOrganisations();
	}

	@CrossOrigin
	@RequestMapping("/organisations/{localName}")
	public Organisation getOrganisations(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getOrganisation(localName);
	}

	@CrossOrigin
	@RequestMapping("/topics")
	public List<Topic> getAllTopics() throws IOException, URISyntaxException {
		return datasetService.getAllTopics();
	}

	@CrossOrigin
	@RequestMapping("/topics/{localName}")
	public Topic getTopic(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getTopic(localName);
	}
	
	@CrossOrigin
	@RequestMapping("/infra-objects")
	public List<InfraObject> getAllInfraObjects() throws IOException, URISyntaxException {
		return datasetService.getAllInfraObjects();
	}

	@CrossOrigin
	@RequestMapping("/infra-objects/{localName}")
	public InfraObject getInfraObject(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getInfraObject(localName);
	}

	@CrossOrigin
	@RequestMapping("/quantities")
	public List<QuantityKindAndUnit> getAllQuantities() throws IOException, URISyntaxException {
		return datasetService.getAllQuantities();
	}

	@CrossOrigin
	@RequestMapping("/quantities/{localName}")
	public QuantityKindAndUnit getQuantity(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getQuantity(localName);
	}
	
	@CrossOrigin
	@RequestMapping("/persons")
	public List<Person> getAllPersons() throws IOException, URISyntaxException {
		return datasetService.getAllPersons();
	}

	@CrossOrigin
	@RequestMapping("/persons/{localName}")
	public Person getPerson(@PathVariable String localName) throws IOException, URISyntaxException {
		return datasetService.getPerson(localName);
	}

	@CrossOrigin
	@RequestMapping("/decimal-symbols")
	public List<Dataset.DecimalSymbol> getAllDecimalSymbols() throws IOException, URISyntaxException {
		return datasetService.getAllDecimalSymbols();
	}

	@CrossOrigin
	@RequestMapping("/separators")
	public List<Dataset.Separator> getAllSeparators() throws IOException, URISyntaxException {
		return datasetService.getAllSeparators();
	}

	@CrossOrigin
	@RequestMapping("/formats")
	public List<Dataset.Format> getAllFormats() throws IOException, URISyntaxException {
		return datasetService.getAllFormats();
	}

}
