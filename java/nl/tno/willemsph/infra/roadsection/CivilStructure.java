package nl.tno.willemsph.infra.roadsection;

public class CivilStructure {
	private String id;
	private Geometry geometry;
	private Double beginKm;
	private String beginWdl;
	private Double doorrijhgt;
	private Double eindKm;
	private String eindWdl;
	private String fkVeld4;
	private String ibn;
	private String inventOms;
	private String iziSide;
	private String kantCode;
	private Integer objectId;
	private String omschr;
	private String wegnummer;

	public CivilStructure() {
	}

	public CivilStructure(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public Double getBeginKm() {
		return beginKm;
	}

	public void setBeginKm(Double beginKm) {
		this.beginKm = beginKm;
	}

	public String getBeginWdl() {
		return beginWdl;
	}

	public void setBeginWdl(String beginWdl) {
		this.beginWdl = beginWdl;
	}

	public Double getDoorrijhgt() {
		return doorrijhgt;
	}

	public void setDoorrijhgt(Double doorrijhgt) {
		this.doorrijhgt = doorrijhgt;
	}

	public Double getEindKm() {
		return eindKm;
	}

	public void setEindKm(Double eindKm) {
		this.eindKm = eindKm;
	}

	public String getEindWdl() {
		return eindWdl;
	}

	public void setEindWdl(String eindWdl) {
		this.eindWdl = eindWdl;
	}

	public String getFkVeld4() {
		return fkVeld4;
	}

	public void setFkVeld4(String fkVeld4) {
		this.fkVeld4 = fkVeld4;
	}

	public String getIbn() {
		return ibn;
	}

	public void setIbn(String ibn) {
		this.ibn = ibn;
	}

	public String getInventOms() {
		return inventOms;
	}

	public void setInventOms(String inventOms) {
		this.inventOms = inventOms;
	}

	public String getIziSide() {
		return iziSide;
	}

	public void setIziSide(String iziSide) {
		this.iziSide = iziSide;
	}

	public String getKantCode() {
		return kantCode;
	}

	public void setKantCode(String kantCode) {
		this.kantCode = kantCode;
	}

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public String getOmschr() {
		return omschr;
	}

	public void setOmschr(String omschr) {
		this.omschr = omschr;
	}

	public String getWegnummer() {
		return wegnummer;
	}

	public void setWegnummer(String wegnummer) {
		this.wegnummer = wegnummer;
	}

}
