package pkg.Radar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
@Entity
@Schema(name = "Plot", description = "Plot Model")
public class Radar {

    @Id
    @GeneratedValue
    private Long id;
    @Schema(required = true)

    private String status;
    private String lastSend;
    private int radio;
    private Long time;
    private String trackInput;
    private Integer count;
    private String trackMode;
    private int sensor;


    private int environment;
    public float courseRangeMin;
    private float course;
    public float courseRangeMax;
    public float courseIncrement;
    private float latitude;
    private float bearing;
    private int mode1code;
    private int mode2code;

    private String startTime;
    private float speed;
    public float speedRangeMin;
    public float speedRangeMax;
    public float speedIncrement;
    private float longitude;
    private float distance;
    private int mode3code;
    private int mode4code;

    private String endTime;
    public float altitudeRangeMin;
    public float altitudeRangeMax;
    public float altitudeIncrement;
    private float altitude;
    private int mode5code;

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public int getSensor() {
        return sensor;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTrackInput() {
        return trackInput;
    }

    public void setTrackInput(String trackInput) {
        this.trackInput = trackInput;
    }

    public float getCourse() {
        return course;
    }

    public void setCourse(float course) {
        this.course = course;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(String trackMode) {
        this.trackMode = trackMode;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
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

    public int getMode1code() {
        return mode1code;
    }

    public void setMode1code(int mode1code) {
        this.mode1code = mode1code;
    }

    public int getMode2code() {
        return mode2code;
    }

    public void setMode2code(int mode2code) {
        this.mode2code = mode2code;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public int getMode3code() {
        return mode3code;
    }

    public void setMode3code(int mode3code) {
        this.mode3code = mode3code;
    }

    public int getMode4code() {
        return mode4code;
    }

    public void setMode4code(int mode4code) {
        this.mode4code = mode4code;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public int getMode5code() {
        return mode5code;
    }

    public void setMode5code(int mode5code) {
        this.mode5code = mode5code;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
