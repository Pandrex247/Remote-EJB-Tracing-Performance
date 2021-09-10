package fish.payara.samples.remote.ejb.tracing;

import io.opentracing.Tracer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/remote/request")
@ApplicationScoped
public class RequestScopedRemoteEjbEndpoint {

    @Inject
    private Tracer tracer;

    private static final Logger logger = Logger.getLogger(RequestScopedRemoteEjbEndpoint.class.getName());

    @GET
    @Path("/")
    public String invokeAll() {
        String returnString = "";

        Properties contextProperties = new Properties();
        contextProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
        contextProperties.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        contextProperties.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

        try {
            Context context = new InitialContext(contextProperties);
            EjbRemote ejb = (EjbRemote) context.lookup("java:global/remote-ejb-tracing-server/Ejb");

            tracer.activeSpan().setBaggageItem("Wibbles", "Wobbles");
            returnString = ejb.annotatedMethod();
            logger.log(Level.INFO, ejb.annotatedMethod());

            tracer.activeSpan().setBaggageItem("Nibbles", "Nobbles");
            returnString += "\n" + ejb.nonAnnotatedMethod();
            logger.log(Level.INFO, ejb.nonAnnotatedMethod());

            tracer.activeSpan().setBaggageItem("Bibbles", "Bobbles");
            returnString += "\n" + ejb.shouldNotBeTraced();
            logger.log(Level.INFO, ejb.nonAnnotatedMethod());
        } catch (NamingException namingException) {
            returnString = "Naming Exception!";
        }

        return returnString;
    }
}
