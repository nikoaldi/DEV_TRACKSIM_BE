package pkg.OwnShip;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Schema(name = "OwnShip", description = "OwnShip Model")
public class OwnShip {

    @Id
    @GeneratedValue
    private Long id;
    @Schema(required = true)

    private String trackMode;
    private String startTime;
    private String endTime;
    private String status;
    private String lastSend;
    private Long time;
    private int sensor;
    private Integer environment;
    private Float latitude;
    private Float longitude;

    private Float course;
    private Float courseRangeMin;
    private Float courseRangeMax;
    private Float courseIncrement;
    private Float heading;
    private Float humidity;
    private Float windSpeed;
    private Float WindDirection;

    private Float speed;
    private Float speedRangeMin;
    private Float speedRangeMax;
    private Float speedIncrement;
    private Float pitch;
    private Float roll;
    private Float yaw;
    private Float airTemperature;

    private Float altitude;
    private Float altitudeRangeMin;
    private Float altitudeRangeMax;
    private Float altitudeIncrement;
    private Long accelerationX;
    private Long accelerationY;
    private Long accelerationZ;
    private Float barometricPressure;

    private Float velocityX;
    private Float velocityY;
    private Float velocityZ;

    public int getSensor() {
        return sensor;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public Float getCourseIncrement() {
        return courseIncrement;
    }

    public void setCourseIncrement(Float courseIncrement) {
        this.courseIncrement = courseIncrement;
    }

    public Float getSpeedIncrement() {
        return speedIncrement;
    }

    public void setSpeedIncrement(Float speedIncrement) {
        this.speedIncrement = speedIncrement;
    }

    public Float getAltitudeIncrement() {
        return altitudeIncrement;
    }

    public void setAltitudeIncrement(Float altitudeIncrement) {
        this.altitudeIncrement = altitudeIncrement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(String trackMode) {
        this.trackMode = trackMode;
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

    public Integer getEnvironment() {
        return environment;
    }

    public void setEnvironment(Integer environment) {
        this.environment = environment;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getCourse() {
        return course;
    }

    public void setCourse(Float course) {
        this.course = course;
    }

    public Float getCourseRangeMin() {
        return courseRangeMin;
    }

    public void setCourseRangeMin(Float courseRangeMin) {
        this.courseRangeMin = courseRangeMin;
    }

    public Float getCourseRangeMax() {
        return courseRangeMax;
    }

    public void setCourseRangeMax(Float courseRangeMax) {
        this.courseRangeMax = courseRangeMax;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Float getWindDirection() {
        return WindDirection;
    }

    public void setWindDirection(Float windDirection) {
        WindDirection = windDirection;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getSpeedRangeMin() {
        return speedRangeMin;
    }

    public void setSpeedRangeMin(Float speedRangeMin) {
        this.speedRangeMin = speedRangeMin;
    }

    public Float getSpeedRangeMax() {
        return speedRangeMax;
    }

    public void setSpeedRangeMax(Float speedRangeMax) {
        this.speedRangeMax = speedRangeMax;
    }

    public Float getPitch() {
        return pitch;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    public Float getRoll() {
        return roll;
    }

    public void setRoll(Float roll) {
        this.roll = roll;
    }

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
    }

    public Float getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(Float airTemperature) {
        this.airTemperature = airTemperature;
    }

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    public Float getAltitudeRangeMin() {
        return altitudeRangeMin;
    }

    public void setAltitudeRangeMin(Float altitudeRangeMin) {
        this.altitudeRangeMin = altitudeRangeMin;
    }

    public Float getAltitudeRangeMax() {
        return altitudeRangeMax;
    }

    public void setAltitudeRangeMax(Float altitudeRangeMax) {
        this.altitudeRangeMax = altitudeRangeMax;
    }

    public Long getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(Long accelerationX) {
        this.accelerationX = accelerationX;
    }

    public Long getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(Long accelerationY) {
        this.accelerationY = accelerationY;
    }

    public Long getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(Long accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    public Float getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(Float barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    public Float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(Float velocityX) {
        this.velocityX = velocityX;
    }

    public Float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(Float velocityY) {
        this.velocityY = velocityY;
    }

    public Float getVelocityZ() {
        return velocityZ;
    }

    public void setVelocityZ(Float velocityZ) {
        this.velocityZ = velocityZ;
    }
}
