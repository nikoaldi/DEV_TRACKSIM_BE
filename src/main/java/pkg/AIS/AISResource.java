package pkg.AIS;

import com.len.ccs.common.datatypes.AisType;
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
public class AISResource {

    @Inject
    @Channel("ais-out")
    Emitter<String> sendKafka;

    @Inject
    AISRepository aisRepository;

    List<AIS> listAIS = new ArrayList<>();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy

    // Delete Track AIS
    public Response deleteAllTrack(Long[] id){
        boolean deleted = false;
        for (int i =0; i< id.length; i++) {
            deleted = aisRepository.deleteById(id[i]);
        }
        return deleted ? Response.noContent().build() : Response.status(Response.Status.BAD_REQUEST).build();
    }

    // HANDLER SEND AIS TRACK
    public Response HandlerSendAISTrack(Long[] id){
        for (int i =0; i< id.length; i++) {
            updateSendAISTrack(id[i]);
            sendTrackToKafka(aisRepository.findById(id[i]));
        }
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE SEND AIS TRACK
    @Transactional
    public Response updateSendAISTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return aisRepository
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

    // HANDLER STOP AIS TRACK
    public Response HandlerStopAISTrack(Long[] id){
        for (int i =0; i< id.length; i++) {
            updateStopAISTrack(id[i]);
        }
        return Response.accepted(id).build();
    }

    // UPDATE STATUS & LAST UPDATE STOP AIS TRACK
    @Transactional
    public Response updateStopAISTrack(Long id){
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return aisRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            if (m.getTrackMode().equals("automatic")) {
                                m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                                m.setStatus("Stopped");
                                return Response.ok(m).build();
                            } else {
                                m.setStatus("Sended");
                                m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                                return Response.ok(m).build();
                            }
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    // SEND AIS TRACK TO KAFKA
    public void sendTrackToKafka(AIS ais){
        String radarSend;
        radarSend = "{\"trackNumber\":"+ais.getId()+",\"speed\":"+ais.getSpeed()+",\"course\":"+ais.getCourse()+",\"latitude\":"+ais.getLatitude()+",\"longitude\":"+ais.getLongitude()+",\"mmsi\":"+ais.getMmsiNumber()+",\"imo\":"+ais.getImoNumber()+",\"aisName\":\""+ais.getAisName()+"\",\"aisType\":"+ais.getAisType()+",\"eta\":\""+ais.getEta()+"\",\"navigationalStatus\":"+ais.getNavigationStatus()+",\"shipCallSign\":\""+ais.getShipCallSign()+"\",\"rateOfTurn\":"+ais.getRateOfTurn()+",\"dimensionsA\":"+ais.getDimensionsA()+",\"dimensionsB\":"+ais.getDimensionsB()+",\"dimensionsC\":"+ais.getDimensionsC()+",\"dimensionsD\":"+ais.getDimensionsD()+",\"vendorId\":\""+ais.getVendorId()+"\",\"destination\":\""+ais.getDestination()+"\"}";
        sendKafka.send(radarSend);
    }

    // FUNGSI SAVE SINGLE AIS TRACK
    @Transactional
    public Response insertSingleAISTrack(AIS ais){
        ais.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        aisRepository.persist(ais);
        if (aisRepository.isPersistent(ais)){
            return Response.created(URI.create("/ais/"+ais.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // HANDLER SAVE AND SEND SINGLE AIS TRACK
    @Transactional
    public Response insertAndSendSingleAISTrack(AIS ais){
        ais.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
        aisRepository.persist(ais);
        if (aisRepository.isPersistent(ais)){
            saveAndSendTrackToKafka(ais.getId());
            return Response.created(URI.create("/ais/"+ais.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // FUNGSI SAVE AND SEND MULTI AIS TRACK
    public Response insertAndSendMultiAISTrack(AIS ais){
        Integer check1 = (int) aisRepository.count() + ais.getCount();
        if (ais.getTrackMode().equals("automatic")){
            for (int i=0; i < ais.getCount(); i++) {
                AIS ais1 = new AIS();
                ais1.setStatus("Saved");
                ais1.setTrackInput("multi");
                ais1.setTrackMode("automatic");
                ais1.setStartTime("-");
                ais1.setEndTime("-");
                ais1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                ais1.setCourse(randomNumber(0, 100));
                ais1.setSpeed(randomNumber(0, 100));
                ais1.setCourseRangeMin(1);
                ais1.setCourseRangeMax(100);
                ais1.setSpeedRangeMin(1);
                ais1.setSpeedRangeMax(100);
                ais1.setCourseIncrement(5);
                ais1.setSpeedIncrement(5);
                ais1.setLatitude(randomNumber(0, 100));
                ais1.setLongitude(randomNumber(0, 100));
                ais1.setBearing(randomNumber(0, 360));
                ais1.setDistance(randomNumber(0, 360));
                ais1.setMmsiNumber(randomNumber(0, 100));
                ais1.setImoNumber(randomNumber(0, 100));
                ais1.setDimensionsA(randomNumber(0, (int) 65.535));
                ais1.setVendorId("-");
                ais1.setAisName("-");
                ais1.setEta("-");
                ais1.setDimensionsB(randomNumber(0, (int) 65.535));
                ais1.setDestination("-");
                ais1.setAisType("Indonesia");
                ais1.setNavigationStatus(randomNumber(0, 255));
                ais1.setDimensionsC(randomNumber(0, (int) 65.535));
                ais1.setShipCallSign("-");
                ais1.setRateOfTurn(randomNumber(0, 255));
                ais1.setDimensionsD(randomNumber(0, (int) 65.535));
                aisRepository.persist(ais1);
                saveAndSendTrackToKafka(ais1.getId());
            }
        } else {
            for (int i=0; i < ais.getCount(); i++) {
                AIS ais1 = new AIS();
                ais1.setStatus(ais.getStatus());
                ais1.setLastSend(ais.getLastSend());
                ais1.setTrackInput(ais.getTrackInput());
                ais1.setTrackMode(ais.getTrackMode());
                ais1.setCount(ais.getCount());
                ais1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                ais1.setStartTime(ais.getStartTime());
                ais1.setEndTime(ais.getEndTime());
                ais1.setCourse(ais.getCourse());
                ais1.setSpeed(ais.getSpeed());
                ais1.setCourseRangeMin(ais.getCourseRangeMin());
                ais1.setCourseRangeMax(ais.getCourseRangeMax());
                ais1.setSpeedRangeMin(ais.getSpeedRangeMin());
                ais1.setSpeedRangeMax(ais.getSpeedRangeMax());
                ais1.setSpeedIncrement(ais.getSpeedIncrement());
                ais1.setCourseIncrement(ais.getCourseIncrement());
                ais1.setLatitude(ais.getLatitude());
                ais1.setLongitude(ais.getLongitude());
                ais1.setBearing(ais.getBearing());
                ais1.setDistance(ais.getDistance());

                ais1.setMmsiNumber(ais.getMmsiNumber());
                ais1.setImoNumber(ais.getImoNumber());
                ais1.setDimensionsA(ais.getDimensionsA());
                ais1.setVendorId(ais.getVendorId());
                ais1.setAisName(ais.getAisName());
                ais1.setEta(ais.getEta());
                ais1.setDimensionsB(ais.getDimensionsB());
                ais1.setDestination(ais.getDestination());
                ais1.setAisType(ais.getAisType());
                ais1.setNavigationStatus(ais.getNavigationStatus());
                ais1.setDimensionsC(ais.getDimensionsC());
                ais1.setShipCallSign(ais.getShipCallSign());
                ais1.setRateOfTurn(ais.getRateOfTurn());
                ais1.setDimensionsD(ais.getDimensionsD());
                aisRepository.persist(ais1);
                saveAndSendTrackToKafka(ais1.getId());
            }
        }

        if (check1 == aisRepository.count()){
            return Response.created(URI.create("/ais/"+ais.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    // SEND AIS TRACK TO KAFKA
    public void saveAndSendTrackToKafka(Long id){
        sendTrackToKafka(aisRepository.findById(id));
        updateSendAISTrack(id);
    }

    // FUNGSI SAVE MULTI AIS TRACK
    public Response insertMultiAISTrack(AIS ais){
        Integer check1 = (int) aisRepository.count() + ais.getCount();
        if (ais.getTrackMode().equals("automatic")){
            for (int i=0; i < ais.getCount(); i++) {
                AIS ais1 = new AIS();
                ais1.setStatus("Saved");
                ais1.setTrackInput("multi");
                ais1.setTrackMode("automatic");
                ais1.setStartTime("-");
                ais1.setEndTime("-");
                ais1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                ais1.setCourse(randomNumber(0, 100));
                ais1.setSpeed(randomNumber(0, 100));
                ais1.setCourseRangeMin(1);
                ais1.setCourseRangeMax(100);
                ais1.setSpeedRangeMin(1);
                ais1.setSpeedRangeMax(100);
                ais1.setCourseIncrement(5);
                ais1.setSpeedIncrement(5);
                ais1.setLatitude(randomNumber(0, 100));
                ais1.setLongitude(randomNumber(0, 100));
                ais1.setBearing(randomNumber(0, 360));
                ais1.setDistance(randomNumber(0, 360));
                ais1.setMmsiNumber(randomNumber(0, 100));
                ais1.setImoNumber(randomNumber(0, 100));
                ais1.setDimensionsA(randomNumber(0, (int) 65.535));
                ais1.setVendorId("-");
                ais1.setAisName("-");
                ais1.setEta("-");
                ais1.setDimensionsB(randomNumber(0, (int) 65.535));
                ais1.setDestination("-");
                ais1.setAisType("Indonesia");
                ais1.setNavigationStatus(randomNumber(0, 255));
                ais1.setDimensionsC(randomNumber(0, (int) 65.535));
                ais1.setShipCallSign("-");
                ais1.setRateOfTurn(randomNumber(0, 255));
                ais1.setDimensionsD(randomNumber(0, (int) 65.535));
                aisRepository.persist(ais1);
            }
        } else {
            for (int i=0; i < ais.getCount(); i++) {
                AIS ais1 = new AIS();
                ais1.setStatus(ais.getStatus());
                ais1.setLastSend(ais.getLastSend());
                ais1.setTrackInput(ais.getTrackInput());
                ais1.setTrackMode(ais.getTrackMode());
                ais1.setCount(ais.getCount());
                ais1.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                ais1.setStartTime(ais.getStartTime());
                ais1.setEndTime(ais.getEndTime());
                ais1.setCourse(ais.getCourse());
                ais1.setSpeed(ais.getSpeed());
                ais1.setCourseRangeMin(ais.getCourseRangeMin());
                ais1.setCourseRangeMax(ais.getCourseRangeMax());
                ais1.setSpeedRangeMin(ais.getSpeedRangeMin());
                ais1.setSpeedRangeMax(ais.getSpeedRangeMax());
                ais1.setSpeedIncrement(ais.getSpeedIncrement());
                ais1.setCourseIncrement(ais.getCourseIncrement());
                ais1.setLatitude(ais.getLatitude());
                ais1.setLongitude(ais.getLongitude());
                ais1.setBearing(ais.getBearing());
                ais1.setDistance(ais.getDistance());

                ais1.setMmsiNumber(ais.getMmsiNumber());
                ais1.setImoNumber(ais.getImoNumber());
                ais1.setDimensionsA(ais.getDimensionsA());
                ais1.setVendorId(ais.getVendorId());
                ais1.setAisName(ais.getAisName());
                ais1.setEta(ais.getEta());
                ais1.setDimensionsB(ais.getDimensionsB());
                ais1.setDestination(ais.getDestination());
                ais1.setAisType(ais.getAisType());
                ais1.setNavigationStatus(ais.getNavigationStatus());
                ais1.setDimensionsC(ais.getDimensionsC());
                ais1.setShipCallSign(ais.getShipCallSign());
                ais1.setRateOfTurn(ais.getRateOfTurn());
                ais1.setDimensionsD(ais.getDimensionsD());
                aisRepository.persist(ais1);
            }
        }

        if (check1 == aisRepository.count()){
            return Response.created(URI.create("/ais/"+ais.getId())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    // GENERATE RANDOM NUMBER RANDOM
    public static int randomNumber(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    // FUNGSI UPDATE AIS TRACK
    public Response updateAISTrack(Long id,AIS ais){
        return aisRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setStatus(ais.getStatus());
                            m.setLastSend(ais.getLastSend());
                            m.setTrackInput(ais.getTrackInput());
                            m.setTrackMode(ais.getTrackMode());
                            m.setCount(ais.getCount());
                            m.setTime(Calendar.getInstance().getTimeInMillis() / 1000);
                            m.setStartTime(ais.getStartTime());
                            m.setEndTime(ais.getEndTime());
                            m.setCourse(ais.getCourse());
                            m.setSpeed(ais.getSpeed());
                            m.setCourseRangeMin(ais.getCourseRangeMin());
                            m.setCourseRangeMax(ais.getCourseRangeMax());
                            m.setSpeedRangeMin(ais.getSpeedRangeMin());
                            m.setSpeedRangeMax(ais.getSpeedRangeMax());
                            m.setSpeedIncrement(ais.getSpeedIncrement());
                            m.setCourseIncrement(ais.getCourseIncrement());
                            m.setLatitude(ais.getLatitude());
                            m.setLongitude(ais.getLongitude());
                            m.setBearing(ais.getBearing());
                            m.setDistance(ais.getDistance());
                            m.setMmsiNumber(ais.getMmsiNumber());
                            m.setImoNumber(ais.getImoNumber());
                            m.setDimensionsA(ais.getDimensionsA());
                            m.setVendorId(ais.getVendorId());
                            m.setAisName(ais.getAisName());
                            m.setEta(ais.getEta());
                            m.setDimensionsB(ais.getDimensionsB());
                            m.setAisType(ais.getAisType());
                            m.setNavigationStatus(ais.getNavigationStatus());
                            m.setDimensionsC(ais.getDimensionsC());
                            m.setShipCallSign(ais.getShipCallSign());
                            m.setRateOfTurn(ais.getRateOfTurn());
                            m.setDimensionsD(ais.getDimensionsD());
                            m.setDestination(ais.getDestination());
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

    // HANDLER SCHEDULER AIS TRACK
    public void handlerSchedulerAISTrack(Long id){
        updateShedulerSendingAISTrack(id);
        sendTrackToKafka(aisRepository.findById(id));
    }

    @Scheduled(every = "1s")
    public void onStart(){
        listAIS = (aisRepository.listAll());
        if (listAIS.size() > 0 ){
            for (int i = 0; i < listAIS.size(); i++) {
                if (listAIS.get(i).getTrackMode().equals("automatic") && !listAIS.get(i).getStatus().equals("Stopped") ) {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//dd/MM/yyyy
                    Date now = new Date();
                    String strDate = sdfDate.format(now);
                    LocalDateTime waktusekarang = LocalDateTime.parse(strDate);

                    if (listAIS.get(i).getStartTime().equals("-") && listAIS.get(i).getEndTime().equals("-")){
                        handlerSchedulerAISTrack(listAIS.get(i).getId());
                    } else  if (listAIS.get(i).getStartTime().equals("-") && !listAIS.get(i).getEndTime().equals("-")) {
                        LocalDateTime endTime1 = LocalDateTime.parse(listAIS.get(i).getEndTime());
                        if (waktusekarang.isBefore(endTime1) || waktusekarang.equals(endTime1) ) {
                            handlerSchedulerAISTrack(listAIS.get(i).getId());
                        } else {
                            updateShedulerStoppedAISTrack(listAIS.get(i).getId());
                        }
                    } else if (!listAIS.get(i).getStartTime().equals("-") && listAIS.get(i).getEndTime().equals("-")){
                        LocalDateTime startTime1 = LocalDateTime.parse(listAIS.get(i).getStartTime());
                        if (waktusekarang.isAfter(startTime1) || waktusekarang.equals(startTime1) ) {
                            handlerSchedulerAISTrack(listAIS.get(i).getId());
                        }
                    } else if (!listAIS.get(i).getStartTime().equals("-") && !listAIS.get(i).getEndTime().equals("-")){
                        LocalDateTime startTime = LocalDateTime.parse(listAIS.get(i).getStartTime());
                        LocalDateTime endTime = LocalDateTime.parse(listAIS.get(i).getEndTime());
                        if (waktusekarang.isAfter(startTime) || waktusekarang.equals(startTime)) {
                            if (waktusekarang.isBefore(endTime)) {
                                handlerSchedulerAISTrack(listAIS.get(i).getId());
                            } else {
                                updateShedulerStoppedAISTrack(listAIS.get(i).getId());
                            }
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public Response updateShedulerSendingAISTrack(Long id){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return aisRepository
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
    public Response updateShedulerStoppedAISTrack(Long id){
        return aisRepository
                .findByIdOptional(id)
                .map(
                        m -> {
                            m.setStatus("Stopped");
                            return Response.ok(m).build();
                        })
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }









}
