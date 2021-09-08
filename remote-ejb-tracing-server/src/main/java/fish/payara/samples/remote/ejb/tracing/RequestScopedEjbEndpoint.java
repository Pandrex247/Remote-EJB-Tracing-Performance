package fish.payara.samples.remote.ejb.tracing;

import io.opentracing.Tracer;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/request")
@RequestScoped
public class RequestScopedEjbEndpoint {

    @EJB
    private EjbRemote ejb;

    @Inject
    private Tracer tracer;

    private static final Logger logger = Logger.getLogger(RequestScopedEjbEndpoint.class.getName());

    @GET
    @Path("/invokeAll")
    public String invokeAll() {
        String returnString = "";

        tracer.activeSpan().setBaggageItem("Wibbles", "Wobbles");
        returnString = ejb.annotatedMethod();
        logger.log(Level.INFO, returnString);

        tracer.activeSpan().setBaggageItem("Nibbles", "Nobbles");
        returnString += "\n" + ejb.nonAnnotatedMethod();
        logger.log(Level.INFO, returnString);

        tracer.activeSpan().setBaggageItem("Bibbles", "Bobbles");
        returnString += "\n" + ejb.shouldNotBeTraced();
        logger.log(Level.INFO, returnString);

        return returnString;
    }
}
