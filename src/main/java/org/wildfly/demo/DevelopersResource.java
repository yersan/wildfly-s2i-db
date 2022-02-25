package org.wildfly.demo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Path("developers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class DevelopersResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    public Response getAll() {
        final List<Developer> developers = em.createQuery("SELECT d FROM Developer d").getResultList();
        GenericEntity entity = new GenericEntity<>(developers) {};

        return Response.ok()
                .entity(entity)
                .build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") long id) {
        final Developer developer = em.find(Developer.class, id);
        if (developer != null) {
            return Response.ok()
                    .entity(developer)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("Developer with ID %d not found", id))
                .build();
    }

    @POST
    public Response create(Developer developer) {
        em.persist(developer);

        return Response.created(
                        UriBuilder.fromResource(DevelopersResource.class)
                                .path(Long.toString(developer.getId()))
                                .build())
                .entity(developer)
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") long id) {
        final Developer d = em.find(Developer.class, id);
        if (d != null) {
            em.remove(d);
            return Response.status(Response.Status.NO_CONTENT)
                    .build();
        }

        return Response.notModified()
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, Developer developer) {
        final Developer d = em.find(Developer.class, id);
        if (d != null) {
            d.setName(developer.getName());
            d.setSurname(developer.getSurname());
            return Response.status(Response.Status.NO_CONTENT)
                    .build();
        }

        return Response.notModified()
                .build();
    }
}
