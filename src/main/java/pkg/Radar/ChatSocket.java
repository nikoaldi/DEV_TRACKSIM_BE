package pkg.Radar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.kafka.common.protocol.types.Field;

import static io.quarkus.agroal.runtime.AgroalConnectionConfigurer.log;

@ServerEndpoint("/chat")
@ApplicationScoped
public class ChatSocket {


    @Inject
    RadarRepository radarRepository;

    List<Radar> radars = new ArrayList<>();

    private Session session;

    Map<String[],Session> sessions = new ConcurrentHashMap<>();

    List<Radar> radar1 = new ArrayList<>();

    String[] angka = {"hallo","juga"};




    @OnOpen
    public void onOpen(Session session){
        sessions.put(angka,session);
        onMessage(angka.toString());
    }

    @OnMessage
    public void onMessage(String angka){
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendText(angka, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }


//    private void broadcast(Integer[] message) {
//        sessions.values().stream().map(session1 -> session1.se)
//
//        sessions.values().forEach(s -> {
//            s.getAsyncRemote().sendObject(message, result ->  {
//                if (result.getException() != null) {
//                    System.out.println("Unable to send message: " + result.getException());
//                }
//            });
//        });
//    }

//    @Scheduled(every = "2s")
//    public void increment() {
//     count = count +1;
//     onMessage(count);
//    }



}