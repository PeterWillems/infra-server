package nl.tno.willemsph.infra.roadsection;

import java.net.URI;

public class RoadSection {
	private long id;
	private Geometry geometry;
	private Boolean administrativeDirection;
	private Integer beginDistance;
	private URI beginJunction;
	private Double beginKilometer;
	private DrivewayPosition drivewayPosition;
	private DrivewaySubtype drivewaySubtype;
	private Boolean drivingDirection;
	private Integer endDistance;
	private URI endJunction;
	private Double endKilometer;
	private String hectoLetter;
	private Integer municipalityId;
	private String municipalityName;
	private RelativePosition relativePosition;
	private String residence;
	private String roadAuthorityId;
	private String roadAuthorityName;
	private RoadAuthorityType roadAuthorityType;
	private String roadNumber;
	private String roadPartLetter;
	private Long roadSectionId;
	private StartDate startDate;
	private String streetName;

	public RoadSection() {
	}

	public RoadSection(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public Boolean getAdministrativeDirection() {
		return administrativeDirection;
	}

	public void setAdministrativeDirection(Boolean administrativeDirection) {
		this.administrativeDirection = administrativeDirection;
	}

	public Integer getBeginDistance() {
		return beginDistance;
	}

	public void setBeginDistance(Integer beginDistance) {
		this.beginDistance = beginDistance;
	}

	public URI getBeginJunction() {
		return beginJunction;
	}

	public void setBeginJunction(URI beginJunction) {
		this.beginJunction = beginJunction;
	}

	public Double getBeginKilometer() {
		return beginKilometer;
	}

	public void setBeginKilometer(Double beginKilometer) {
		this.beginKilometer = beginKilometer;
	}

	public DrivewaySubtype getDrivewaySubtype() {
		return drivewaySubtype;
	}

	public void setDrivewaySubtype(DrivewaySubtype drivewaySubtype) {
		this.drivewaySubtype = drivewaySubtype;
	}

	public Boolean getDrivingDirection() {
		return drivingDirection;
	}

	public void setDrivingDirection(Boolean drivingDirection) {
		this.drivingDirection = drivingDirection;
	}

	public Integer getEndDistance() {
		return endDistance;
	}

	public void setEndDistance(Integer endDistance) {
		this.endDistance = endDistance;
	}

	public URI getEndJunction() {
		return endJunction;
	}

	public void setEndJunction(URI endJunction) {
		this.endJunction = endJunction;
	}

	public Double getEndKilometer() {
		return endKilometer;
	}

	public void setEndKilometer(Double endKilometer) {
		this.endKilometer = endKilometer;
	}

	public String getHectoLetter() {
		return hectoLetter;
	}

	public void setHectoLetter(String hectoLetter) {
		this.hectoLetter = hectoLetter;
	}

	public Integer getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(Integer municipalityId) {
		this.municipalityId = municipalityId;
	}

	public String getMunicipalityName() {
		return municipalityName;
	}

	public void setMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
	}

	public RelativePosition getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(RelativePosition relativePosition) {
		this.relativePosition = relativePosition;
	}

	public String getResidence() {
		return residence;
	}

	public void setResidence(String residence) {
		this.residence = residence;
	}

	public String getRoadAuthorityId() {
		return roadAuthorityId;
	}

	public void setRoadAuthorityId(String roadAuthorityId) {
		this.roadAuthorityId = roadAuthorityId;
	}

	public String getRoadAuthorityName() {
		return roadAuthorityName;
	}

	public void setRoadAuthorityName(String roadAuthorityName) {
		this.roadAuthorityName = roadAuthorityName;
	}

	public RoadAuthorityType getRoadAuthorityType() {
		return roadAuthorityType;
	}

	public void setRoadAuthorityType(RoadAuthorityType roadAuthorityType) {
		this.roadAuthorityType = roadAuthorityType;
	}

	public String getRoadNumber() {
		return roadNumber;
	}

	public void setRoadNumber(String roadNumber) {
		this.roadNumber = roadNumber;
	}

	public String getRoadPartLetter() {
		return roadPartLetter;
	}

	public void setRoadPartLetter(String roadPartLetter) {
		this.roadPartLetter = roadPartLetter;
	}

	public StartDate getStartDate() {
		return startDate;
	}

	public void setStartDate(StartDate startDate) {
		this.startDate = startDate;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public Long getRoadSectionId() {
		return roadSectionId;
	}

	public void setRoadSectionId(Long roadSectionId) {
		this.roadSectionId = roadSectionId;
	}

	public DrivewayPosition getDrivewayPosition() {
		return drivewayPosition;
	}

	public void setDrivewayPosition(DrivewayPosition drivewayPosition) {
		this.drivewayPosition = drivewayPosition;
	}

}
