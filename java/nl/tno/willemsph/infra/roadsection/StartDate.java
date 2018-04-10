package nl.tno.willemsph.infra.roadsection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StartDate implements RoadSectionPropertyValue {
	private LocalDate localDate;

	public StartDate() {
	}

	public StartDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	@Override
	public String getValue() {
		return localDate.format(DateTimeFormatter.ISO_DATE);
	}

}
