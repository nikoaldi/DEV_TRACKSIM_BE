package pkg.ADSB;

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


import java.util.ArrayList;
import java.util.List;

@Path("/adsb")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class ADSBEndPoint {
    @Inject
    ADSBRepository adsbRepository;

    @Inject
    ADSBResource adsbResource;

    public static List<ADSB> adsb = new ArrayList<>();

    // GET ALL DATA ADSB
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all ADSB Track",
            description = "method to get all ADSB Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/check")
    public AisShipCountryType[] check(){
        return AisShipCountryType.values();
    }

    // GET ALL DATA ADSB
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all ADSB Track",
            description = "method to get all ADSB Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getAll(){
        adsb = adsbRepository.listAll();
        return Response.ok(adsb).build();
    }

    // SAVE ADSB TRACK
    @POST
    @Operation(
            operationId = "inputDataADSB",
            summary = "create new Data ADSB",
            description = "Create a new Data ADSB to add inside the database"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data ADSB Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    public Response inputADSBTrack(
            @RequestBody(
                    description = "ADSB Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ADSB.class))
            )
            ADSB adsb) {
        if (adsb.getTrackInput().equals("single")){
            return Response.status(adsbResource.insertSingleADSBTrack(adsb).getStatus()).build();
        } else {
            return Response.status(adsbResource.insertMultiADSBTrack(adsb).getStatus()).build();
        }
    }

    // SAVE AND SEND ADSB TRACK
    @POST
    @Operation(
            operationId = "inputAndSendDataADSB",
            summary = "create and Send new ADSB Track",
            description = "Create a new and Send Data ADSB Track"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data ADSB Created and Sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("saveandsend")
    @Transactional
    public Response inputAndSendADSBTrack(
            @RequestBody(
                    description = "ADSB Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ADSB.class))
            )
            ADSB adsb) {
        if (adsb.getTrackInput().equals("single")){
            return Response.status(adsbResource.insertAndSendSingleADSBTrack(adsb).getStatus()).build();
        } else {
            return Response.status(adsbResource.insertAndSendMultiADSBTrack(adsb).getStatus()).build();
        }
    }

    // POST SEND TRACK
    @POST
    @Operation(
            operationId = "sendTrackADSB",
            summary = "Send ADSB Track",
            description = "Sending ADSB Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "ADSB track sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/sendtrack")
    public Response sendADSBTrack(Long[] id){
        return Response.status(adsbResource.HandlerSendADSBTrack(id).getStatus()).build();
    }

    // POST STOP TRACK
    @POST
    @Operation(
            operationId = "stopTrackADSB",
            summary = "Stop ADSB Track",
            description = "Stoping ADSB Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "ADSB track stoped",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    @Path("/stoptrack")
    public Response stopADSBTrack(Long[] id){
        return Response.status(adsbResource.HandlerStopADSBTrack(id).getStatus()).build();
    }

    // UPDATE ADSB TRACK
    @PUT
    @Operation(
            operationId = "updateDataADSB",
            summary = "Update ADSB Track Data",
            description = "Update ADSB Track Data by ID"
    )
    @APIResponse(
            responseCode = "200",
            description = "ADSB Track Updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("{id}")
    @Transactional
    public Response updateById(@PathParam("id") Long id, ADSB adsb) {
        return Response.status(adsbResource.updateADSBTrack(id,adsb).getStatus()).build();
    }

    // DELETE ALL ADSB TRACK
    @DELETE
    @Operation(
            operationId = "inputIdDelete",
            summary = "Delete ADSB Track by Id",
            description = "Delete ADSB Track from database"
    )
    @APIResponse(
            responseCode = "204",
            description = "ADSB Track Deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/deleteall")
    @Transactional
    public Response inputIdDelete(Long[] id){
        return Response.status(adsbResource.deleteAllTrack(id).getStatus()).build();
    }
}
