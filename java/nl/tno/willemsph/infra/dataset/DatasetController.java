package nl.tno.willemsph.infra.dataset;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
