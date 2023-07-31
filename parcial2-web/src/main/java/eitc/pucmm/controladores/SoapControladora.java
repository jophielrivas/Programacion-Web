package eitc.pucmm.controladores;


import com.sun.net.httpserver.HttpContext;
import eitc.pucmm.ApiServices.SoapWebService;
import io.javalin.Javalin;
import jakarta.xml.ws.Endpoint;
import org.eclipse.jetty.http.spi.HttpSpiContextHandler;
import org.eclipse.jetty.http.spi.JettyHttpContext;
import org.eclipse.jetty.http.spi.JettyHttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.lang.reflect.Method;

public class SoapControladora extends BaseControladora{

    public SoapControladora(Javalin app) {
        super(app);
    }

    public void aplicarRutas() {
        Server server = app.jettyServer().server();
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        server.setHandler(contextHandlerCollection);

        try {
            HttpContext context = build(server, "/ws");

            SoapWebService userWS = new SoapWebService();
            Endpoint userEndPoint = Endpoint.create(userWS);

            userEndPoint.publish(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HttpContext build(Server server, String contextString) throws Exception {
        JettyHttpServer jettyHttpServer = new JettyHttpServer(server, true);
        JettyHttpContext ctx = (JettyHttpContext) jettyHttpServer.createContext(contextString);
        Method method = JettyHttpContext.class.getDeclaredMethod("getJettyContextHandler");
        method.setAccessible(true);
        HttpSpiContextHandler contextHandler = (HttpSpiContextHandler) method.invoke(ctx);
        contextHandler.start();
        return ctx;
    }
}
