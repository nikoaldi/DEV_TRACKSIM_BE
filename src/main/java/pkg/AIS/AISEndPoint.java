package pkg.AIS;

import com.len.ccs.common.datatypes.AisShipCountryType;
import com.len.ccs.common.datatypes.AisType;
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

@Path("/ais")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AISEndPoint {

    @Inject
    AISRepository aisRepository;

    @Inject
    AISResource aisResource;

    public static List<AIS> ais = new ArrayList<>();

    // GET ENUM AIS TYPE
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all AIS Track",
            description = "method to get all AIS Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("enum")
    public Response getEnum(){
        return Response.ok(AisType.getEnum("SAR Air Craft").getValue()).build();
    }

    // GET ALL DATA AIS
    @GET
    @Operation(
            operationId = "getAll",
            summary = "Get all AIS Track",
            description = "method to get all AIS Track"
    )
    @APIResponse(
            responseCode = "200",
            description = "Operation Complited",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getAll(){
        ais = aisRepository.listAll();
        return Response.ok(ais).build();
    }

    // SAVE AIS TRACK
    @POST
    @Operation(
            operationId = "inputDataAIS",
            summary = "create new Data AIS",
            description = "Create a new Data AIS to add inside the database"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data AIS Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    public Response inputAISTrack(
            @RequestBody(
                    description = "AIS Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AIS.class))
            )
            AIS ais) {
        if (ais.getTrackInput().equals("single")){
            return Response.status(aisResource.insertSingleAISTrack(ais).getStatus()).build();
        } else {
            return Response.status(aisResource.insertMultiAISTrack(ais).getStatus()).build();
        }
    }

    // SAVE AND SEND AIS TRACK
    @POST
    @Operation(
            operationId = "inputAndSendDataAIS",
            summary = "create and Send new AIS Track",
            description = "Create a new and Send Data AIS Track"
    )
    @APIResponse(
            responseCode = "201",
            description = "Data AIS Created and Sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("saveandsend")
    @Transactional
    public Response inputAndSendAISTrack(
            @RequestBody(
                    description = "AIS Track to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AIS.class))
            )
            AIS ais) {
        if (ais.getTrackInput().equals("single")){
            return Response.status(aisResource.insertAndSendSingleAISTrack(ais).getStatus()).build();
        } else {
            return Response.status(aisResource.insertAndSendMultiAISTrack(ais).getStatus()).build();
        }
    }

    // POST SEND TRACK
    @POST
    @Operation(
            operationId = "sendTrackRAIS",
            summary = "Send AIS Track",
            description = "Sending AIS Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "AIS track sended",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/sendtrack")
    public Response sendAISTrack(Long[] id){
        return Response.status(aisResource.HandlerSendAISTrack(id).getStatus()).build();
    }

    // POST STOP TRACK
    @POST
    @Operation(
            operationId = "stopTrackAIS",
            summary = "Stop AIS Track",
            description = "Stoping AIS Track to Kafka"
    )
    @APIResponse(
            responseCode = "200",
            description = "AIS track stoped",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    @Path("/stoptrack")
    public Response stopAISTrack(Long[] id){
        return Response.status(aisResource.HandlerStopAISTrack(id).getStatus()).build();
    }

    // UPDATE AIS TRACK
    @PUT
    @Operation(
            operationId = "updateDataAIS",
            summary = "Update AIS Track Data",
            description = "Update AIS Track Data by ID"
    )
    @APIResponse(
            responseCode = "200",
            description = "AIS Track Updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("{id}")
    @Transactional
    public Response updateById(@PathParam("id") Long id, AIS ais) {
        return Response.status(aisResource.updateAISTrack(id,ais).getStatus()).build();
    }

    // DELETE ALL AIS TRACK
    @DELETE
    @Operation(
            operationId = "inputIdDelete",
            summary = "Delete AIS Track by Id",
            description = "Delete AIS AIS from database"
    )
    @APIResponse(
            responseCode = "204",
            description = "AIS Track Deleted",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Path("/deleteall")
    @Transactional
    public Response inputIdDelete(Long[] id){
        return Response.status(aisResource.deleteAllTrack(id).getStatus()).build();
    }

}
