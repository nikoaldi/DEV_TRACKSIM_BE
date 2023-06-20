package pkg.ADSB;

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
public class ADSBResource {
    @Inject
    @Channel("adsb-out")
    Emitter<String> sendKafka;

    @Inject
    ADSBRepository adsbRepository;

    List<ADSB> listADSB = new ArrayList<>();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy

    // Delete Track ADSB
    public Response deleteAllTrack(Long[] id){
        boolean deleted = false;
        for (int i =0; i< id.length; i++) {
            deleted = adsbRepository.deleteById(id[i]);
        }
        return deleted ? Response.noContent().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }

    // HANDLER SEND ADSB TRACK
    public Response HandlerSendADSBTrack(Long[] id){
        for (int i =0; i< id.length; i++) {
            updateSendADSBTrack(id[i]);
            sendTrackToKafka(adsbRepository.findById(id[i]));
        }
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE SEND ADSB TRACK
    @Transactional
    public Response updateSendADSBTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return adsbRepository
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

    // HANDLER STOP ADSB TRACK
    public Response HandlerStopADSBTrack(Long[] id){
        for (int i =0; i< id.length; i++) {
            updateStopADSBTrack(id[i]);
        }
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE STOP ADSB TRACK
    @Transactional
    public Response updateStopADSBTrack(Long id){
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return adsbRepository
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

    // SEND ADSB TRACK TO KAFKA
    public void sendTrackToKafka(ADSB adsb){
        String radarSend;
        radarSend = "{\"trackNumber\":"+adsb.getId()+",\"sensor\":"+adsb.getSensor()+",\"course\":"+adsb.getCourse()+",\"speed\":"+adsb.getSpeed()+",\"altitude\":"+adsb.getCourse()+",\"icao\":"+adsb.getIcao()+",\"latitude\":"+adsb.getLatitude()+",\"longitude\":"+adsb.getLongitude()+",\"country\":"+adsb.getCountry()+",\"position\":"+adsb.getPosition()+",\"heading\":"+adsb.getHeading()+",\"callSign\":"+adsb.getCallSign() +",\"verticalRate\":"+adsb.getVerticalRate()+",\"time\":"+adsb.getTime()+"}";
        sendKafka.send(radarSend);
    }

    // FUNGSI SAVE SINGLE ADSB TRACK
    @Transactional
    public Response insertSingleADSBTrack(ADSB adsb){
        adsb.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        adsbRepository.persist(adsb);
        if (adsbRepository.isPersistent(adsb)){
            return Response.created(URI.create("/adsb/"+adsb.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // HANDLER SAVE AND SEND SINGLE ADSB TRACK
    @Transactional
    public Response insertAndSendSingleADSBTrack(ADSB adsb){
        adsb.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        adsbRepository.persist(adsb);
        if (adsbRepository.isPersistent(adsb)){
            saveAndSendTrackToKafka(adsb.getId());
            return Response.created(URI.create("/adsb/"+adsb.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // FUNGSI SAVE AND SEND MULTI ADSB TRACK
    public Response insertAndSendMultiADSBTrack(ADSB adsb){
        Integer check1 = (int) adsbRepository.count() + adsb.getCount();
        if (adsb.getTrackMode().equals("automatic")){
            for (int i=0; i < adsb.getCount(); i++) {
                ADSB adsb1 = new ADSB();
                adsb1.setStatus("Saved");
                adsb1.setTrackInput("multi");
                adsb1.setTrackMode("automatic");
                adsb1.setStartTime("-");
                adsb1.setEndTime("-");
                adsb1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                adsb1.setCourse(randomNumber(0, 100));
                adsb1.setSpeed(randomNumber(0, 100));
                adsb1.setAltitude(randomNumber(0, 360));
                adsb1.setCourseRangeMin(1);
                adsb1.setCourseRangeMax(100);
                adsb1.setSpeedRangeMin(1);
                adsb1.setSpeedRangeMax(100);
                adsb1.setAltitudeRangeMin(1);
                adsb1.setAltitudeRangeMax(100);
                adsb1.setCourseIncrement(5);
                adsb1.setSpeedIncrement(5);
                adsb1.setAltitudeIncrement(5);
                adsb1.setLatitude(randomNumber(0, 100));
                adsb1.setLongitude(randomNumber(0, 100));
                adsb1.setBearing(randomNumber(0, 360));
                adsb1.setDistance(randomNumber(0, 360));
                adsb1.setCountry("IND");
                adsb1.setIcao(1);
                adsb1.setCallSign("Call Sign");
                adsb1.setPosition(randomNumber(1, 2));
                adsb1.setHeading((float) 0);
                adsb1.setVerticalRate((float) 0);
                adsbRepository.persist(adsb1);
                saveAndSendTrackToKafka(adsb1.getId());
            }
        } else {
            for (int i=0; i < adsb.getCount(); i++) {
                ADSB adsb1 = new ADSB();
                adsb1.setStatus(adsb.getStatus());
                adsb1.setTrackInput(adsb.getTrackInput());
                adsb1.setTrackMode(adsb.getTrackMode());
                adsb1.setStartTime(adsb.getStartTime());
                adsb1.setEndTime(adsb.getEndTime());
                adsb1.setCourse(adsb.getCourse());
                adsb1.setSpeed(adsb.getSpeed());
                adsb1.setAltitude(adsb.getAltitude());
                adsb1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                adsb1.setCourseRangeMin(adsb.getCourseRangeMin());
                adsb1.setCourseRangeMax(adsb.getCourseRangeMax());
                adsb1.setSpeedRangeMin(adsb.getSpeedRangeMin());
                adsb1.setSpeedRangeMax(adsb.getSpeedRangeMax());
                adsb1.setAltitudeRangeMin(adsb.getAltitudeRangeMin());
                adsb1.setAltitudeRangeMax(adsb.getAltitudeRangeMax());
                adsb1.setCourseIncrement(adsb.getCourseIncrement());
                adsb1.setSpeedIncrement(adsb.getSpeedIncrement());
                adsb1.setAltitudeIncrement(adsb.getAltitudeIncrement());
                adsb1.setLatitude(adsb.getLatitude());
                adsb1.setLongitude(adsb.getLongitude());
                adsb1.setBearing(adsb.getBearing());
                adsb1.setDistance(adsb.getDistance());
                adsb1.setCountry(adsb.getCountry());
                adsb1.setIcao(adsb.getIcao());
                adsb1.setCallSign(adsb.getCallSign());
                adsb1.setPosition(adsb.getPosition());
                adsb1.setHeading(adsb.getHeading());
                adsb1.setVerticalRate(adsb.getVerticalRate());
                adsbRepository.persist(adsb1);
                saveAndSendTrackToKafka(adsb1.getId());
            }
        }

        if (check1 == adsbRepository.count()){
            return Response.created(URI.create("/adsb/"+adsb.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    // SEND ADSB TRACK TO KAFKA
    public void saveAndSendTrackToKafka(Long id){
        sendTrackToKafka(adsbRepository.findById(id));
        updateSendADSBTrack(id);
    }

    // FUNGSI SAVE MULTI ADSB TRACK
    public Response insertMultiADSBTrack(ADSB adsb){
        Integer check1 = (int) adsbRepository.count() + adsb.getCount();
        if (adsb.getTrackMode().equals("automatic")){
            for (int i=0; i < adsb.getCount(); i++) {
                ADSB adsb1 = new ADSB();
                adsb1.setStatus("Saved");
                adsb1.setTrackInput("multi");
                adsb1.setTrackMode("automatic");
                adsb1.setStartTime("-");
                adsb1.setEndTime("-");
                adsb1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                adsb1.setCourse(randomNumber(0, 100));
                adsb1.setSpeed(randomNumber(0, 100));
                adsb1.setAltitude(randomNumber(0, 360));
                adsb1.setCourseRangeMin(1);
                adsb1.setCourseRangeMax(100);
                adsb1.setSpeedRangeMin(1);
                adsb1.setSpeedRangeMax(100);
                adsb1.setAltitudeRangeMin(1);
                adsb1.setAltitudeRangeMax(100);
                adsb1.setCourseIncrement(5);
                adsb1.setSpeedIncrement(5);
                adsb1.setAltitudeIncrement(5);
                adsb1.setLatitude(randomNumber(0, 100));
                adsb1.setLongitude(randomNumber(0, 100));
                adsb1.setBearing(randomNumber(0, 360));
                adsb1.setDistance(randomNumber(0, 360));
                adsb1.setCountry("IND");
                adsb1.setIcao(1);
                adsb1.setCallSign("Call Sign");
                adsb1.setPosition(randomNumber(1, 2));
                adsb1.setHeading((float) 0);
                adsb1.setVerticalRate((float) 0);
                adsbRepository.persist(adsb1);
            }
        } else {
            for (int i=0; i < adsb.getCount(); i++) {
                ADSB adsb1 = new ADSB();
                adsb1.setStatus(adsb.getStatus());
                adsb1.setTrackInput(adsb.getTrackInput());
                adsb1.setTrackMode(adsb.getTrackMode());
                adsb1.setStartTime(adsb.getStartTime());
                adsb1.setEndTime(adsb.getEndTime());
                adsb1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                adsb1.setCourse(adsb.getCourse());
                adsb1.setSpeed(adsb.getSpeed());
                adsb1.setAltitude(adsb.getAltitude());
                adsb1.setCourseRangeMin(adsb.getCourseRangeMin());
                adsb1.setCourseRangeMax(adsb.getCourseRangeMax());
                adsb1.setSpeedRangeMin(adsb.getSpeedRangeMin());
                adsb1.setSpeedRangeMax(adsb.getSpeedRangeMax());
                adsb1.setAltitudeRangeMin(adsb.getAltitudeRangeMin());
                adsb1.setAltitudeRangeMax(adsb.getAltitudeRangeMax());
                adsb1.setCourseIncrement(adsb.getCourseIncrement());
                adsb1.setSpeedIncrement(adsb.getSpeedIncrement());
                adsb1.setAltitudeIncrement(adsb.getAltitudeIncrement());
                adsb1.setLatitude(adsb.getLatitude());
                adsb1.setLongitude(adsb.getLongitude());
                adsb1.setBearing(adsb.getBearing());
                adsb1.setDistance(adsb.getDistance());
                adsb1.setCountry(adsb.getCountry());
                adsb1.setIcao(adsb.getIcao());
                adsb1.setCallSign(adsb.getCallSign());
                adsb1.setPosition(adsb.getPosition());
                adsb1.setHeading(adsb.getHeading());
                adsb1.setVerticalRate(adsb.getVerticalRate());
                adsbRepository.persist(adsb1);
            }
        }

        if (check1 == adsbRepository.count()){
            return Response.created(URI.create("/adsb/"+adsb.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    // GENERATE RANDOM NUMBER RANDOM
    public static int randomNumber(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    // FUNGSI UPDATE ADSB TRACK
    public Response updateADSBTrack(Long id,ADSB adsb){
        return adsbRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setStatus(adsb.getStatus());
                            m.setTrackInput(adsb.getTrackInput());
                            m.setTrackMode(adsb.getTrackMode());
                            m.setStartTime(adsb.getStartTime());
                            m.setEndTime(adsb.getEndTime());
                            m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                            m.setCourse(adsb.getCourse());
                            m.setSpeed(adsb.getSpeed());
                            m.setAltitude(adsb.getAltitude());
                            m.setCourseRangeMin(adsb.getCourseRangeMin());
                            m.setCourseRangeMax(adsb.getCourseRangeMax());
                            m.setSpeedRangeMin(adsb.getSpeedRangeMin());
                            m.setSpeedRangeMax(adsb.getSpeedRangeMax());
                            m.setAltitudeRangeMin(adsb.getAltitudeRangeMin());
                            m.setAltitudeRangeMax(adsb.getAltitudeRangeMax());
                            m.setCourseIncrement(adsb.getCourseIncrement());
                            m.setSpeedIncrement(adsb.getSpeedIncrement());
                            m.setAltitudeIncrement(adsb.getAltitudeIncrement());
                            m.setLatitude(adsb.getLatitude());
                            m.setLongitude(adsb.getLongitude());
                            m.setBearing(adsb.getBearing());
                            m.setDistance(adsb.getDistance());
                            m.setCountry(adsb.getCountry());
                            m.setIcao(adsb.getIcao());
                            m.setCallSign(adsb.getCallSign());
                            m.setPosition(adsb.getPosition());
                            m.setHeading(adsb.getHeading());
                            m.setVerticalRate(adsb.getVerticalRate());
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

    // HANDLER SCHEDULER ADSB TRACK
    public void handlerSchedulerADSBTrack(Long id){
        updateShedulerSendingADSBTrack(id);
        sendTrackToKafka(adsbRepository.findById(id));
    }

    @Scheduled(every = "1s")
    public void onStart(){
        listADSB = (adsbRepository.listAll());
        if (listADSB.size() > 0 ){
            for (int i = 0; i < listADSB.size(); i++) {
                if (listADSB.get(i).getTrackMode().equals("automatic") && !listADSB.get(i).getStatus().equals("Stopped") ) {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
                    Date now = new Date();
                    String strDate = sdfDate.format(now);
                    LocalDateTime waktusekarang = LocalDateTime.parse(strDate);

                    if (listADSB.get(i).getStartTime().equals("-") && listADSB.get(i).getEndTime().equals("-")){
                        handlerSchedulerADSBTrack(listADSB.get(i).getId());
                    } else  if (listADSB.get(i).getStartTime().equals("-") && !listADSB.get(i).getEndTime().equals("-")) {
                        LocalDateTime endTime1 = LocalDateTime.parse(listADSB.get(i).getEndTime());
                        if (waktusekarang.isBefore(endTime1) || waktusekarang.equals(endTime1) ) {
                            handlerSchedulerADSBTrack(listADSB.get(i).getId());
                        } else {
                            updateShedulerStoppedADSBTrack(listADSB.get(i).getId());
                        }
                    } else if (!listADSB.get(i).getStartTime().equals("-") && listADSB.get(i).getEndTime().equals("-")){
                        LocalDateTime startTime1 = LocalDateTime.parse(listADSB.get(i).getStartTime());
                        if (waktusekarang.isAfter(startTime1) || waktusekarang.equals(startTime1) ) {
                            handlerSchedulerADSBTrack(listADSB.get(i).getId());
                        }
                    } else if (!listADSB.get(i).getStartTime().equals("-") && !listADSB.get(i).getEndTime().equals("-")){
                        LocalDateTime startTime = LocalDateTime.parse(listADSB.get(i).getStartTime());
                        LocalDateTime endTime = LocalDateTime.parse(listADSB.get(i).getEndTime());
                        if (waktusekarang.isAfter(startTime) || waktusekarang.equals(startTime)) {
                            if (waktusekarang.isBefore(endTime)) {
                                handlerSchedulerADSBTrack(listADSB.get(i).getId());
                            } else {
                                updateShedulerStoppedADSBTrack(listADSB.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public Response updateShedulerSendingADSBTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return adsbRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                            m.setLastSend(strDate);
                            m.setCourse(numberIncrement(m.getCourse(), m.getCourseRangeMin(),m.getCourseRangeMax(), m.getCourseIncrement()));
                            m.setSpeed(numberIncrement(m.getSpeed(), m.getSpeedRangeMin(),m.getSpeedRangeMax(), m.getSpeedIncrement()));
                            m.setLatitude(numberIncrement(m.getLatitude(),  -90,90, (float) 0.2));
                            m.setLongitude(numberIncrement(m.getLongitude(), -180,180, (float) 0.2));
                            m.setStatus("Sending");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    @Transactional
    public Response updateShedulerStoppedADSBTrack(Long id){
        return adsbRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setStatus("Stopped");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }







}
