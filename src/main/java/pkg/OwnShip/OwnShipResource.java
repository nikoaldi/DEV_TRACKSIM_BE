package pkg.OwnShip;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;


import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class OwnShipResource {
    @Inject
    @Channel("ownship-out")
    Emitter<String> sendKafka;

    @Inject
    OwnShipRepository ownShipRepository;

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy

    //Delete Track OwnShip
    public Response deleteAllTrack(Long id){
        boolean deleted = ownShipRepository.deleteById(1L);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }

    // HANDLER SEND OWNSHIP TRACK
    public Response HandlerSendOwnShipTrack(Long id){
        updateSendOwnShipTrack(id);
        sendTrackToKafka(ownShipRepository.findById(id));
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE SEND OWNSHIP TRACK
    @Transactional
    public Response updateSendOwnShipTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return ownShipRepository
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

    // HANDLER STOP OWNSHIP TRACK
    public Response HandlerStopOwnShipTrack(Long id){
        updateStopOwnShipTrack(id);
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE STOP OWNSHIP TRACK
    @Transactional
    public Response updateStopOwnShipTrack(Long id){
        return ownShipRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            if (m.getTrackMode().equals("automatic")) {
                                m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                                m.setStatus("Stopped");
                                return Response.ok(m).build();
                            } else {
                                m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                                m.setStatus("Sended");
                                return Response.ok(m).build();
                            }
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    // SEND OWNSHIP TRACK TO KAFKA
    public void sendTrackToKafka(OwnShip ownShip){
        String ADSBSend;
        ADSBSend = "{\"trackNumber\":"+ownShip.getId()+",\"sensor\":"+ownShip.getSensor()+",\"category\":"+ownShip.getEnvironment()+",\"time\":"+Calendar.getInstance().getTimeInMillis() / 1000+",\"latitude\":"+ownShip.getLatitude()+",\"longitude\":"+ownShip.getLongitude()+",\"course\":"+ownShip.getCourse()+",\"speed\":"+ownShip.getSpeed()+",\"heading\":"+ownShip.getHeading()+",\"altitude\":"+ownShip.getAltitude()+",\"pitch\":"+ownShip.getPitch()+",\"roll\":"+ownShip.getRoll()+",\"yaw\":"+ownShip.getYaw()+",\"accelerationX\":"+ownShip.getAccelerationX()+",\"accelerationY\":"+ownShip.getAccelerationY()+",\"accelerationZ\":"+ownShip.getAccelerationZ()+",\"velocityX\":"+ownShip.getVelocityX()+",\"velocityY\":"+ownShip.getVelocityY()+",\"velocityZ\":"+ownShip.getVelocityZ()+",\"humidity\":"+ownShip.getHumidity()+",\"windSpeed\":"+ownShip.getWindSpeed()+",\"windDirection\":"+ownShip.getWindDirection()+",\"airTemperature\":"+ownShip.getAirTemperature()+",\"barometricPressure\":"+ownShip.getBarometricPressure()+"}";
        sendKafka.send(ADSBSend);
    }

    // FUNGSI SAVE OWNSHIP TRACK
    @Transactional
    public Response insertOwnShipTrack(OwnShip ownShip){
        ownShip.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        ownShipRepository.persist(ownShip);
        if (ownShipRepository.isPersistent(ownShip)){
            return Response.created(URI.create("/ownShip/"+ownShip.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // HANDLER SAVE AND SEND OWNSHIP   TRACK
    @Transactional
    public Response insertAndSendOwnShipTrack(OwnShip ownShip){
        ownShipRepository.persist(ownShip);
        if (ownShipRepository.isPersistent(ownShip)){
            saveAndSendTrackToKafka(ownShip.getId());
            return Response.created(URI.create("/ownShip/"+ownShip.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // SEND OWNSHIP TRACK TO KAFKA
    public void saveAndSendTrackToKafka(Long id){
        sendTrackToKafka(ownShipRepository.findById(id));
        updateSendOwnShipTrack(id);
    }

    // GENERATE RANDOM NUMBER RANDOM
    public static int randomNumber(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    // FUNGSI UPDATE OWNSHIP TRACK
    public Response updateOwnShipTrack(Long id,OwnShip ownShip){
        return ownShipRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setTrackMode(ownShip.getTrackMode());
                            m.setStartTime(ownShip.getStartTime());
                            m.setEndTime(ownShip.getEndTime());
                            m.setStatus(ownShip.getStatus());
                            m.setCourseRangeMin(ownShip.getCourseRangeMin());
                            m.setCourseRangeMax(ownShip.getCourseRangeMax());
                            m.setCourseIncrement(ownShip.getCourseIncrement());
                            m.setSpeedRangeMin(ownShip.getSpeedRangeMin());
                            m.setSpeedRangeMax(ownShip.getSpeedRangeMax());
                            m.setSpeedIncrement(ownShip.getSpeedIncrement());
                            m.setAltitudeRangeMin(ownShip.getAltitudeRangeMin());
                            m.setAltitudeRangeMax(ownShip.getAltitudeRangeMax());
                            m.setAltitudeIncrement(ownShip.getAltitudeIncrement());
                            m.setEnvironment(ownShip.getEnvironment());
                            m.setLatitude(ownShip.getLatitude());
                            m.setLongitude(ownShip.getLongitude());
                            m.setCourse(ownShip.getCourse());
                            m.setSpeed(ownShip.getSpeed());
                            m.setAltitude(ownShip.getAltitude());
                            m.setPitch(ownShip.getPitch());
                            m.setRoll(ownShip.getRoll());
                            m.setYaw(ownShip.getYaw());
                            m.setAccelerationX(ownShip.getAccelerationX());
                            m.setAccelerationY(ownShip.getAccelerationY());
                            m.setAccelerationZ(ownShip.getAccelerationZ());
                            m.setVelocityX(ownShip.getVelocityX());
                            m.setVelocityY(ownShip.getVelocityY());
                            m.setVelocityZ(ownShip.getVelocityZ());
                            m.setHumidity(ownShip.getHumidity());
                            m.setHeading(ownShip.getHeading());
                            m.setWindSpeed(ownShip.getWindSpeed());
                            m.setWindDirection(ownShip.getWindDirection());
                            m.setAirTemperature(ownShip.getAirTemperature());
                            m.setBarometricPressure(ownShip.getBarometricPressure());
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

    // HANDLER SCHEDULER  OWNSHIP TRACK
    public void handlerSchedulerOwnShipTrack(Long id){
        updateShedulerSendingOwnShipTrack(id);
        sendTrackToKafka(ownShipRepository.findById(id));
    }

    @Scheduled(every = "1s")
    public void onStart(){
        if (ownShipRepository.count() > 0){
            if (ownShipRepository.findById((long) 1L).getTrackMode().equals("automatic") && !ownShipRepository.findById((long) 1L).getStatus().equals("Stopped") ) {
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
                Date now = new Date();
                String strDate = sdfDate.format(now);
                LocalDateTime waktusekarang = LocalDateTime.parse(strDate);

                if ((ownShipRepository.findById((long) 1L).getStartTime().equals("") && ownShipRepository.findById((long) 1L).getEndTime().equals("")) ||(ownShipRepository.findById((long) 1L).getStartTime().equals("") && ownShipRepository.findById((long) 1L).getEndTime().equals("")) ){
                    handlerSchedulerOwnShipTrack(ownShipRepository.findById((long) 1L).getId());
                } else  if (ownShipRepository.findById((long) 1L).getStartTime().equals("") && !ownShipRepository.findById((long) 1L).getEndTime().equals("")) {
                    LocalDateTime endTime1 = LocalDateTime.parse(ownShipRepository.findById((long) 1L).getEndTime());
                    if (waktusekarang.isBefore(endTime1)) {
                        handlerSchedulerOwnShipTrack(ownShipRepository.findById((long) 1L).getId());
                    } else {
                        updateShedulerStoppedOwnShipTrack(ownShipRepository.findById((long) 1L).getId());
                    }
                } else if (!ownShipRepository.findById((long) 1L).getStartTime().equals("") && ownShipRepository.findById((long) 1L).getEndTime().equals("")){
                    LocalDateTime startTime1 = LocalDateTime.parse(ownShipRepository.findById((long) 1L).getStartTime());
                    if (waktusekarang.isAfter(startTime1) || waktusekarang.equals(startTime1) ) {
                        handlerSchedulerOwnShipTrack(ownShipRepository.findById((long) 1L).getId());
                    }
                } else if (!ownShipRepository.findById((long) 1L).getStartTime().equals("") && !ownShipRepository.findById((long) 1L).getEndTime().equals("")){
                    LocalDateTime startTime = LocalDateTime.parse(ownShipRepository.findById((long) 1L).getStartTime());
                    LocalDateTime endTime = LocalDateTime.parse(ownShipRepository.findById((long) 1L).getEndTime());
                    if (waktusekarang.isAfter(startTime) || waktusekarang.equals(startTime)) {
                        if (waktusekarang.isBefore(endTime)) {
                            handlerSchedulerOwnShipTrack(ownShipRepository.findById((long) 1L).getId());
                        } else {
                            updateShedulerStoppedOwnShipTrack(ownShipRepository.findById((long) 1L).getId());
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public Response updateShedulerSendingOwnShipTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return ownShipRepository
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
                            m.setStatus("Sending");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    @Transactional
    public Response updateShedulerStoppedOwnShipTrack(Long id){
        return ownShipRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setStatus("Stopped");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

}
