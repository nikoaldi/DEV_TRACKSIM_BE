package pkg.ADSB;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Schema(name = "ADSB", description = "ADSB Model")
public class ADSB {
    @Id
    @GeneratedValue
    private long id;
    @Schema(required = true)

    private String status;
    private String lastSend;
    private Long time;
    private String trackInput;
    private String trackMode;
    private int sensor;
    private int count;
    private String startTime;
    private String endTime;
    private float speed;
    private float speedRangeMin;
    private float speedRangeMax;
    private float speedIncrement;
    private float course;
    public float courseRangeMin;
    public float courseRangeMax;
    public float courseIncrement;
    private float altitude;
    public float altitudeRangeMin;
    public float altitudeRangeMax;
    public float altitudeIncrement;
    private float longitude;
    private float latitude;
    private float bearing;
    private float distance;
    private String country;
    private int icao;
    private String callSign;
    private int position;
    private Float heading;
    private Float verticalRate;

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

    public int getSensor() {
        return sensor;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
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

    public String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(String trackMode) {
        this.trackMode = trackMode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getAltitudeRangeMin() {
        return altitudeRangeMin;
    }

    public void setAltitudeRangeMin(float altitudeRangeMin) {
        this.altitudeRangeMin = altitudeRangeMin;
    }

    public float getAltitudeRangeMax() {
        return altitudeRangeMax;
    }

    public void setAltitudeRangeMax(float altitudeRangeMax) {
        this.altitudeRangeMax = altitudeRangeMax;
    }

    public float getAltitudeIncrement() {
        return altitudeIncrement;
    }

    public void setAltitudeIncrement(float altitudeIncrement) {
        this.altitudeIncrement = altitudeIncrement;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getIcao() {
        return icao;
    }

    public void setIcao(int icao) {
        this.icao = icao;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Float getVerticalRate() {
        return verticalRate;
    }

    public void setVerticalRate(Float verticalRate) {
        this.verticalRate = verticalRate;
    }
}
