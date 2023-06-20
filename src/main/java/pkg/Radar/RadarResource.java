package pkg.Radar;


import com.len.ccs.common.datatypes.AisShipCountryType;
import com.len.ccs.common.kinematics.GeoCoordinate;
import com.len.ccs.common.kinematics.util.GeoUtil;
import io.quarkus.panache.common.Parameters;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.hibernate.Session;
import pkg.OwnShip.OwnShipRepository;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class RadarResource {

    @Inject
    @Channel("radar-out")
    Emitter<String> sendKafka;

    @Inject
    RadarRepository radarRepository;

    @Inject
    OwnShipRepository ownShipRepository;

    List<Radar> listRadar = new ArrayList<>();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy

    //Delete Track Radar
    public Response deleteAllTrack(Long[] id){
        boolean deleted = false;
        for (int i =0; i< id.length; i++) {
            deleted = radarRepository.deleteById(id[i]);
        }
        return deleted ? Response.noContent().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }

    // HANDLER SEND RADAR TRACK
    public Response HandlerSendRadarTrack(Long[] id){
        for (int i =0; i< id.length; i++) {
            updateSendRadarTrack(id[i]);
            sendTrackToKafka(radarRepository.findById(id[i]));
        }
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE SEND RADAR TRACK
    @Transactional
    public Response updateSendRadarTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return radarRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            if (m.getTrackMode().equals("manual")) {
                                m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                                m.setStatus("Sended");
                                m.setLastSend(strDate);
                                return Response.ok(m).build();
                            } else {
                                m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                                m.setStatus("Sending");
                                return Response.ok(m).build();
                            }

                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    // HANDLER STOP RADAR TRACK
    public Response HandlerStopRadarTrack(Long[] id){
        for (int i =0; i< id.length; i++) {
            updateStopRadarTrack(id[i]);
        }
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST  UPDATE STOP RADAR TRACK
    @Transactional
    public Response updateStopRadarTrack(Long id){
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return radarRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            if (m.getTrackMode().equals("automatic")) {
                                m.setStatus("Stopped");
                                return Response.ok(m).build();
                            } else {
                                m.setStatus("Sended");
                                return Response.ok(m).build();
                            }
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    // SEND RADAR TRACK TO KAFKA
    public void sendTrackToKafka(Radar radar){
        String radarSend;
        radarSend = "{\"trackNumber\":"+radar.getId()+",\"sensor\":"+radar.getSensor()+",\"category\":"+radar.getEnvironment()+",\"bearing\":"+radar.getBearing()+",\"distance\":"+radar.getDistance()+",\"altitude\":"+radar.getAltitude()+",\"time\":"+radar.getTime()+",\"course\":"+radar.getCourse()+",\"speed\":"+radar.getSpeed()+",\"mode1code\":"+radar.getMode1code()+",\"mode2code\":"+radar.getMode2code()+",\"mode3code\":"+radar.getMode3code()+",\"mode4code\":"+radar.getMode4code()+",\"mode5code\":"+radar.getMode5code()+"}";
        sendKafka.send(radarSend);
    }

    // GENERATE BEARING
    public float generateBearing(float latitude, float longitude){
        return (float) hitungBearing(ownShipRepository.findById(1L).getLatitude(), ownShipRepository.findById(1L).getLongitude(), latitude, longitude);
    }

    // GENERATE Distance
    public float generateDistance(float lat, float lon, float alt){
        return (float) hitungDistance(ownShipRepository.findById(1L).getLatitude(), lat, ownShipRepository.findById(1L).getLongitude(), lon, ownShipRepository.findById(1L).getAltitude(), alt);
    }

    // FUNGSI SAVE SINGLE RADAR TRACK
    @Transactional
    public Response insertSingleRadarTrack(Radar radar){
        if (radar.getRadio() == 1) {
            radar.setBearing(generateBearing(radar.getLatitude(), radar.getLongitude()));
            radar.setDistance(generateDistance(radar.getLatitude(), radar.getLongitude(), radar.getAltitude()));
        }
        radar.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        radarRepository.persist(radar);
        if (radarRepository.isPersistent(radar)){
            return Response.created(URI.create("/radar/"+radar.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // HANDLER SAVE AND SEND SINGLE RADAR TRACK
    @Transactional
    public Response insertAndSendSingleRadarTrack(Radar radar){
        if (radar.getRadio() == 1) {
            radar.setBearing(generateBearing(radar.getLatitude(), radar.getLongitude()));
            radar.setDistance(generateDistance(radar.getLatitude(), radar.getLongitude(), radar.getAltitude()));
        }
        radar.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        radarRepository.persist(radar);
        if (radarRepository.isPersistent(radar)){
            saveAndSendTrackToKafka(radar.getId());
            return Response.created(URI.create("/radar/"+radar.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // FUNGSI SAVE AND SEND MULTI RADAR TRACK
    public Response insertAndSendMultiRadarTrack(Radar radar){
        Integer check1 = (int) radarRepository.count() + radar.getCount();
            for (int i=0; i < radar.getCount(); i++) {
                Radar radar1 = new Radar();
                if (radar.getTrackMode().equals("automatic")) {
                    radar1.setTrackMode("automatic");
                } else {
                    radar1.setTrackMode("manual");
                }
                radar1.setStatus("Sended");
                radar1.setTrackInput("multi");
                radar1.setEnvironment(randomNumber(1, 4));
                radar1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                radar1.setStartTime("-");
                radar1.setEndTime("-");
                radar1.setCourse(randomNumber(0, 100));
                radar1.setSpeed(randomNumber(0, 100));
                radar1.setAltitude(randomNumber(0, 360));
                radar1.setCourseRangeMin(1);
                radar1.setCourseRangeMax(100);
                radar1.setSpeedRangeMin(1);
                radar1.setSpeedRangeMax(100);
                radar1.setAltitudeRangeMin(1);
                radar1.setAltitudeRangeMax(100);
                radar1.setCourseIncrement(5);
                radar1.setSpeedIncrement(5);
                radar1.setAltitudeIncrement(5);
                radar1.setLatitude(randomNumber(-10, 200));
                radar1.setLongitude(randomNumber(-10, 200));
                radar1.setBearing(generateBearing(radar1.getLatitude(), radar1.getLongitude()));
                radar1.setDistance(generateDistance(radar1.getLatitude(), radar1.getLongitude(), radar1.getAltitude()));
                radar1.setMode1code(randomNumber(0, 59));
                radar1.setMode2code(randomNumber(0, 4095));
                radar1.setMode3code(randomNumber(0, 4095));
                radar1.setMode4code(randomNumber(0, 3));
                radar1.setMode5code(randomNumber(0, 3));
                radarRepository.persist(radar1);
                saveAndSendTrackToKafka(radar1.getId());
            }
        if (check1 == radarRepository.count()){
            return Response.created(URI.create("/radar/"+radar.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    // SEND RADAR TRACK TO KAFKA
    public void saveAndSendTrackToKafka(Long id){
        sendTrackToKafka(radarRepository.findById(id));
        updateSendRadarTrack(id);
    }

    // FUNGSI SAVE MULTI RADAR TRACK
    public Response insertMultiRadarTrack( Radar radar){
        Integer check1 = (int) radarRepository.count() + radar.getCount();
        for (int i=0; i < radar.getCount(); i++) {
            Radar radar1 = new Radar();
            if (radar.getTrackMode().equals("automatic")) {
                radar1.setTrackMode("automatic");
            } else {
                radar1.setTrackMode("manual");
            }
            radar1.setTrackInput("multi");
            radar1.setStatus("Saved");
            radar1.setEnvironment(randomNumber(1, 4));
            radar1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
            radar1.setStartTime("-");
            radar1.setEndTime("-");
            radar1.setCourse(randomNumber(0, 100));
            radar1.setSpeed(randomNumber(0, 100));
            radar1.setAltitude(randomNumber(0, 360));
            radar1.setCourseRangeMin(1);
            radar1.setCourseRangeMax(100);
            radar1.setSpeedRangeMin(1);
            radar1.setSpeedRangeMax(100);
            radar1.setAltitudeRangeMin(1);
            radar1.setAltitudeRangeMax(100);
            radar1.setCourseIncrement(5);
            radar1.setSpeedIncrement(5);
            radar1.setAltitudeIncrement(5);
            radar1.setLatitude(randomNumber(-10, 200));
            radar1.setLongitude(randomNumber(-10, 200));
            radar1.setBearing(generateBearing(radar1.getLatitude(), radar1.getLongitude()));
            radar1.setDistance(generateDistance(radar1.getLatitude(), radar1.getLongitude(), radar1.getAltitude()));
            radar1.setMode1code(randomNumber(0, 59));
            radar1.setMode2code(randomNumber(0, 4095));
            radar1.setMode3code(randomNumber(0, 4095));
            radar1.setMode4code(randomNumber(0, 3));
            radar1.setMode5code(randomNumber(0, 3));
            radarRepository.persist(radar1);
            saveAndSendTrackToKafka(radar1.getId());
        }

        if (check1 == radarRepository.count()){
            return Response.created(URI.create("/radar/"+radar.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    // GENERATE RANDOM NUMBER RANDOM
    public static int randomNumber(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    // FUNGSI UPDATE RADAR TRACK
    public Response updateRadarTrack(Long id,Radar radar){
        return radarRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            if (radar.getRadio() == 1){
                                m.setBearing(generateBearing(radar.getLatitude(), radar.getLongitude()));
                                m.setDistance(generateDistance(radar.getLatitude(), radar.getLongitude(), radar.getAltitude()));
                            } else {
                                m.setBearing(radar.getBearing());
                                m.setDistance(radar.getDistance());
                            }
                            m.setTrackMode(radar.getTrackMode());
                            m.setStartTime(radar.getStartTime());
                            m.setEndTime(radar.getEndTime());
                            m.setRadio(radar.getRadio());
                            m.setCourseRangeMin(radar.getCourseRangeMin());
                            m.setCourseRangeMax(radar.getCourseRangeMax());
                            m.setCourseIncrement(radar.getCourseIncrement());
                            m.setSpeedRangeMin(radar.getSpeedRangeMin());
                            m.setSpeedRangeMax(radar.getSpeedRangeMax());
                            m.setSpeedIncrement(radar.getSpeedIncrement());
                            m.setAltitudeRangeMin(radar.getAltitudeRangeMin());
                            m.setAltitudeRangeMax(radar.getAltitudeRangeMax());
                            m.setAltitudeIncrement(radar.getAltitudeIncrement());
                            m.setLatitude(radar.getLatitude());
                            m.setLongitude(radar.getLongitude());
                            m.setEnvironment(radar.getEnvironment());
                            m.setAltitude(radar.getAltitude());
                            m.setCourse(radar.getCourse());
                            m.setSpeed(radar.getSpeed());
                            m.setMode1code(radar.getMode1code());
                            m.setMode2code(radar.getMode2code());
                            m.setMode3code(radar.getMode3code());
                            m.setMode4code(radar.getMode4code());
                            m.setMode5code(radar.getMode5code());

                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // NUMBER INCREMENT
    public static float numberIncrement(float before,float min, float max, float increment) {
        if (before >= min && before < max ){
            return before + increment;
        }
        return before;
    }

    // HANDLER SCHEDULER RADAR TRACK
    public void handlerSchedulerRadarTrack(Long id){
        updateShedulerSendingRadarTrack(id);
        sendTrackToKafka(radarRepository.findById(id));
    }



    @Scheduled(every = "1s")
    public void onStart(){
        listRadar = (radarRepository.listAll());
        if (listRadar.size() > 0 ){
            for (int i = 0; i < listRadar.size(); i++) {
                if (listRadar.get(i).getTrackMode().equals("automatic") && !listRadar.get(i).getStatus().equals("Stopped") ) {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
                    Date now = new Date();
                    String strDate = sdfDate.format(now);
                    LocalDateTime waktusekarang = LocalDateTime.parse(strDate);

                    if ((listRadar.get(i).getStartTime().equals("-") && listRadar.get(i).getEndTime().equals("-")) || (listRadar.get(i).getStartTime().equals("") && listRadar.get(i).getEndTime().equals("")) ){
                        handlerSchedulerRadarTrack(listRadar.get(i).getId());
                    } else  if ((listRadar.get(i).getStartTime().equals("-") || listRadar.get(i).getStartTime().equals(""))&& !listRadar.get(i).getEndTime().equals("-")) {
                        LocalDateTime endTime1 = LocalDateTime.parse(listRadar.get(i).getEndTime());
                        if (waktusekarang.isBefore(endTime1)  ) {
                            handlerSchedulerRadarTrack(listRadar.get(i).getId());
                        } else {
                            updateShedulerStoppedRadarTrack(listRadar.get(i).getId());
                        }
                    } else if (!listRadar.get(i).getStartTime().equals("-") && (listRadar.get(i).getEndTime().equals("-")|| listRadar.get(i).getEndTime().equals("-"))){
                        LocalDateTime startTime1 = LocalDateTime.parse(listRadar.get(i).getStartTime());
                        if (waktusekarang.isAfter(startTime1) || waktusekarang.equals(startTime1) ) {
                            handlerSchedulerRadarTrack(listRadar.get(i).getId());
                        }
                    } else if (!listRadar.get(i).getStartTime().equals("-") && !listRadar.get(i).getEndTime().equals("-")){
                        LocalDateTime startTime = LocalDateTime.parse(listRadar.get(i).getStartTime());
                        LocalDateTime endTime = LocalDateTime.parse(listRadar.get(i).getEndTime());
                        if (waktusekarang.isAfter(startTime) || waktusekarang.equals(startTime)) {
                            if (waktusekarang.isBefore(endTime)) {
                                handlerSchedulerRadarTrack(listRadar.get(i).getId());
                            } else {
                                updateShedulerStoppedRadarTrack(listRadar.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public Response updateShedulerSendingRadarTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return radarRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                            m.setLastSend(strDate);
                            m.setCourse(numberIncrement(m.getCourse(), m.getCourseRangeMin(),m.getCourseRangeMax(), m.getCourseIncrement()));
                            m.setSpeed(numberIncrement(m.getSpeed(), m.getSpeedRangeMin(),m.getSpeedRangeMax(), m.getSpeedIncrement()));
                            m.setAltitude(numberIncrement(m.getAltitude(), m.getAltitudeRangeMin(),m.getAltitudeRangeMax(), m.getAltitudeIncrement()));
                            m.setLatitude(numberIncrement(m.getLatitude(),  -90,90, (float) 0.2));
                            m.setLongitude(numberIncrement(m.getLongitude(), -180,180, (float) 0.2));
                            m.setBearing(generateBearing(radarRepository.findById(id).getLatitude(), radarRepository.findById(id).getLongitude()));
                            m.setDistance(generateDistance(radarRepository.findById(id).getLatitude(),radarRepository.findById(id).getLongitude(), radarRepository.findById(id).getAltitude()));

                            m.setBearing((float) hitungBearing(ownShipRepository.findById(1L).getLatitude(),ownShipRepository.findById(1L).getLongitude(),m.getLatitude(), m.getLongitude()));
                            m.setDistance((float) hitungDistance(ownShipRepository.findById(1L).getLatitude(),m.getLatitude(),ownShipRepository.findById(1L).getLongitude(),m.getLongitude(),ownShipRepository.findById(1L).getAltitude(),m.getAltitude()));
                            m.setStatus("Sending");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    @Transactional
    public Response updateShedulerStoppedRadarTrack(Long id){
        return radarRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setStatus("Stopped");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }


    protected double hitungBearing(double lat1, double lon1, double lat2, double lon2){
        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        double bearing =(Math.toDegrees(Math.atan2(y, x))+360)%360;
        return bearing;
    }


    public double hitungDistance(double lat1, double lat2, double lon1,
                                 double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceToMeter = R * c * 1000; // convert to meters

        double height = el1 - el2;

        double distance = Math.pow(distanceToMeter, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
