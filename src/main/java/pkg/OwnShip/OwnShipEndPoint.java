package pkg.OwnShip;

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

@Path("/ownship")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OwnShipEndPoint {

    @Inject
    OwnShipRepository ownShipRepository;

    @Inject
    OwnShipResource ownShipResource;

    public static List<OwnShip> ownShips = new ArrayList<>();


    // GET ALL DATA OWNSHIP
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all OwnShip Track",
            description = "method to get all OwnShip Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/test")
    public Response getAllTrack(){
        ownShips = ownShipRepository.listAll();
        return Response.ok(ownShips).build();
    }


    // GET ALL DATA OWNSHIP
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all OwnShip Track",
            description = "method to get all OwnShip Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getAll(){
        if (ownShipRepository.count() == 0){
            return Response.ok(ownShips).build();
        } else {
            return Response.ok(ownShipRepository.findByIdOptional(1L)).build();
        }
    }

    // SAVE OWNSHIP TRACK
    @POST
    @Operation(
            operationId = "inputDataOwnShip",
            summary = "create new Data OwnShip",
            description = "Create a new Data OwnShip to add inside the database"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data OwnShip Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    public Response inputOwnShipTrack(
            @RequestBody(
                    description = "OwnShip Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OwnShip.class))
            )
            OwnShip ownShip) {
            return Response.status(ownShipResource.insertOwnShipTrack(ownShip).getStatus()).build();
    }

    // SAVE AND SEND OWNSHIP TRACK
    @POST
    @Operation(
            operationId = "inputAndSendDataOwnShip",
            summary = "create and Send new OwnShip Track",
            description = "Create a new and Send Data OwnShip Track"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data OwnShip Created and Sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("saveandsend")
    @Transactional
    public Response inputAndSendOwnShipTrack(
            @RequestBody(
                    description = "OwnShip Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OwnShip.class))
            )
            OwnShip ownShip) {

            return Response.status(ownShipResource.insertAndSendOwnShipTrack(ownShip).getStatus()).build();
    }

    // POST SEND TRACK
    @POST
    @Operation(
            operationId = "sendTrackOwnShip",
            summary = "Send OwnShip Track",
            description = "Sending OwnShip Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "OwnShip track sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/sendtrack")
    public Response sendOwnShipTrack(Long id){
        return Response.status(ownShipResource.HandlerSendOwnShipTrack(id).getStatus()).build();
    }

    // POST STOP TRACK
    @POST
    @Operation(
            operationId = "stopTrackOwnShip",
            summary = "Stop OwnShip Track",
            description = "Stoping OwnShip Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "OwnShip track stoped",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    @Path("/stoptrack")
    public Response stopOwnShipTrack(Long id){
        return Response.status(ownShipResource.HandlerStopOwnShipTrack(id).getStatus()).build();
    }

    // UPDATE OWNSHIP TRACK
    @PUT
    @Operation(
            operationId = "updateDataOwnShip",
            summary = "Update OwnShip Track Data",
            description = "Update OwnShip Track Data by ID"
    )
    @APIResponse(
            responseCode = "200",
            description = "OwnShip Track Updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("{id}")
    @Transactional
    public Response updateById(@PathParam("id") Long id, OwnShip ownShip) {
        return Response.status(ownShipResource.updateOwnShipTrack(id,ownShip).getStatus()).build();
    }

    // DELETE ALL OWNSHIP TRACK
    @DELETE
    @Operation(
            operationId = "inputIdDelete",
            summary = "Delete OwnShip Track by Id",
            description = "Delete OwnShip Track from database"
    )
    @APIResponse(
            responseCode = "204",
            description = "OwnShip Track Deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/deleteall")
    @Transactional
    public Response inputIdDelete(Long id){
        return Response.status(ownShipResource.deleteAllTrack(id).getStatus()).build();
    }

}
