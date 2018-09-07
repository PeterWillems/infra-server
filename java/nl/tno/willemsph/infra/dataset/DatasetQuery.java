package nl.tno.willemsph.infra.dataset;

import java.util.List;

public class DatasetQuery {
	private String roadNumber;
	private List<Long> roadSectionIds;
	private Long startDate;
	private Long endDate;
	private List<String> topics;

	public DatasetQuery() {
	}

	public String getRoadNumber() {
		return roadNumber;
	}

	public void setRoadNumber(String roadNumber) {
		this.roadNumber = roadNumber;
	}

	public List<Long> getRoadSectionIds() {
		return roadSectionIds;
	}

	public void setRoadSectionIds(List<Long> roadSectionIds) {
		this.roadSectionIds = roadSectionIds;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

}
