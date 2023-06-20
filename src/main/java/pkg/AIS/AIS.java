package pkg.AIS;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Schema(name = "AIS", description = "AIS Model")
public class AIS {
    @Id
    @GeneratedValue
    private long id;
    @Schema(required = true)

    private String status;
    private String lastSend;
    private Long time;
    private String trackInput;
    private String startTime;
    private float course;
    private float courseRangeMin;
    private float courseRangeMax;
    private float courseIncrement;
    private float latitude;
    private float bearing;
    private int mmsiNumber;
    private String aisName;
    private String aisType;
    private String shipCallSign;

    private String trackMode;
    private String endTime;
    private float speed;
    private float speedRangeMin;
    private float speedRangeMax;
    private float speedIncrement;
    private float longitude;
    private float distance;
    private int imoNumber;
    private String eta;
    private int navigationStatus;
    private float rateOfTurn;

    private int dimensionsA;
    private int dimensionsB;
    private int dimensionsC;
    private int dimensionsD;
    private String vendorId;
    private String destination;
    private int count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastSend() {
        return lastSend;
    }

    public void setLastSend(String lastSend) {
        this.lastSend = lastSend;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTrackInput() {
        return trackInput;
    }

    public void setTrackInput(String trackInput) {
        this.trackInput = trackInput;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public float getCourse() {
        return course;
    }

    public void setCourse(float course) {
        this.course = course;
    }

    public float getCourseRangeMin() {
        return courseRangeMin;
    }

    public void setCourseRangeMin(float courseRangeMin) {
        this.courseRangeMin = courseRangeMin;
    }

    public float getCourseRangeMax() {
        return courseRangeMax;
    }

    public void setCourseRangeMax(float courseRangeMax) {
        this.courseRangeMax = courseRangeMax;
    }

    public float getCourseIncrement() {
        return courseIncrement;
    }

    public void setCourseIncrement(float courseIncrement) {
        this.courseIncrement = courseIncrement;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public int getMmsiNumber() {
        return mmsiNumber;
    }

    public void setMmsiNumber(int mmsiNumber) {
        this.mmsiNumber = mmsiNumber;
    }

    public String getAisName() {
        return aisName;
    }

    public void setAisName(String aisName) {
        this.aisName = aisName;
    }

    public String getAisType() {
        return aisType;
    }

    public void setAisType(String aisType) {
        this.aisType = aisType;
    }

    public String getShipCallSign() {
        return shipCallSign;
    }

    public void setShipCallSign(String shipCallSign) {
        this.shipCallSign = shipCallSign;
    }

    public String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(String trackMode) {
        this.trackMode = trackMode;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeedRangeMin() {
        return speedRangeMin;
    }

    public void setSpeedRangeMin(float speedRangeMin) {
        this.speedRangeMin = speedRangeMin;
    }

    public float getSpeedRangeMax() {
        return speedRangeMax;
    }

    public void setSpeedRangeMax(float speedRangeMax) {
        this.speedRangeMax = speedRangeMax;
    }

    public float getSpeedIncrement() {
        return speedIncrement;
    }

    public void setSpeedIncrement(float speedIncrement) {
        this.speedIncrement = speedIncrement;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getImoNumber() {
        return imoNumber;
    }

    public void setImoNumber(int imoNumber) {
        this.imoNumber = imoNumber;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public int getNavigationStatus() {
        return navigationStatus;
    }

    public void setNavigationStatus(int navigationStatus) {
        this.navigationStatus = navigationStatus;
    }

    public float getRateOfTurn() {
        return rateOfTurn;
    }

    public void setRateOfTurn(float rateOfTurn) {
        this.rateOfTurn = rateOfTurn;
    }

    public int getDimensionsA() {
        return dimensionsA;
    }

    public void setDimensionsA(int dimensionsA) {
        this.dimensionsA = dimensionsA;
    }

    public int getDimensionsB() {
        return dimensionsB;
    }

    public void setDimensionsB(int dimensionsB) {
        this.dimensionsB = dimensionsB;
    }

    public int getDimensionsC() {
        return dimensionsC;
    }

    public void setDimensionsC(int dimensionsC) {
        this.dimensionsC = dimensionsC;
    }

    public int getDimensionsD() {
        return dimensionsD;
    }

    public void setDimensionsD(int dimensionsD) {
        this.dimensionsD = dimensionsD;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
