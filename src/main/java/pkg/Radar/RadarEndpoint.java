package pkg.Radar;

import com.len.ccs.common.datatypes.AisShipCountryType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.*;

@Path("/radar")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RadarEndpoint {

    @Inject
    RadarRepository radarRepository;

    @Inject
    RadarResource radarResource;


    public static List<Radar> radars = new ArrayList<>();


    // GET ALL DATA RADAR
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all Radar Track",
            description = "method to get all Radar Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("enum")
    public Response getEnum(){
        return Response.ok(AisShipCountryType.values()).build();
    }

    // GET ALL DATA RADAR
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all Radar Track",
            description = "method to get all Radar Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getAll(){
        radars = radarRepository.listAll();
        return Response.ok(radars).build();
    }

    // SAVE RADAR TRACK
    @POST
    @Operation(
            operationId = "inputDataRadar",
            summary = "create new Data Radar",
            description = "Create a new Data Radar to add inside the database"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data Radar Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    public Response inputRadarTrack(
            @RequestBody(
                    description = "Radar Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Radar.class))
            )
            Radar radar) {
        if (radar.getTrackInput().equals("single")){
            return Response.status(radarResource.insertSingleRadarTrack(radar).getStatus()).build();
        } else {
            return Response.status(radarResource.insertMultiRadarTrack(radar).getStatus()).build();
        }
    }

    // SAVE AND SEND RADAR TRACK
    @POST
    @Operation(
            operationId = "inputAndSendDataRadar",
            summary = "create and Send new Radar Track",
            description = "Create a new and Send Data Radar Track"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data Radar Created and Sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("saveandsend")
    @Transactional
    public Response inputAndSendRadarTrack(
            @RequestBody(
                    description = "Radar Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Radar.class))
            )
            Radar radar) {
        if (radar.getTrackInput().equals("single")){

            return Response.status(radarResource.insertAndSendSingleRadarTrack(radar).getStatus()).build();
        } else {
            return Response.status(radarResource.insertAndSendMultiRadarTrack(radar).getStatus()).build();
        }
    }

    // POST SEND TRACK
    @POST
    @Operation(
            operationId = "sendTrackRadar",
            summary = "Send Radar Track",
            description = "Sending Radar Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "Radar track sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/sendtrack")
    public Response sendRadarTrack(Long[] id){
        return Response.status(radarResource.HandlerSendRadarTrack(id).getStatus()).build();
    }

    // POST STOP TRACK
    @POST
    @Operation(
            operationId = "stopTrackRadar",
            summary = "Stop Radar Track",
            description = "Stoping Radar Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "Radar track stoped",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    @Path("/stoptrack")
    public Response stopRadarTrack(Long[] id){
        return Response.status(radarResource.HandlerStopRadarTrack(id).getStatus()).build();
    }

    // UPDATE RADAR TRACK
    @PUT
    @Operation(
            operationId = "updateDataRadar",
            summary = "Update Radar Track Data",
            description = "Update Radar Track Data by ID"
    )
    @APIResponse(
            responseCode = "200",
            description = "Radar Track Updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("{id}")
    @Transactional
    public Response updateById(@PathParam("id") Long id, Radar radar) {
        return Response.status(radarResource.updateRadarTrack(id,radar).getStatus()).build();
    }

    // DELETE ALL RADAR TRACK
    @DELETE
    @Operation(
            operationId = "inputIdDelete",
            summary = "Delete Radar Track by Id",
            description = "Delete Radar Track from database"
    )
    @APIResponse(
            responseCode = "204",
            description = "Radar Track Deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/deleteall")
    @Transactional
    public Response inputIdDelete(Long[] id){
        return Response.status(radarResource.deleteAllTrack(id).getStatus()).build();
    }


}
