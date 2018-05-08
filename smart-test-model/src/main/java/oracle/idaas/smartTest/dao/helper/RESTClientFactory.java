package oracle.idaas.smartTest.dao.helper;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class RESTClientFactory {

    public static Client getRESTClient() {
        ClientConfig cc = new ClientConfig();
        cc.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);

        // cc.connectorProvider(new ApacheConnectorProvider());
        // cc.property(ClientProperties.PROXY_URI,
        // "www-proxy.us.oracle.com:80");
        // ClientBuilder clientBuilder =
        // ClientBuilder.newBuilder().withConfig(cc);
        System.setProperty("http.proxyHost", "www-proxy.us.oracle.com");
        System.setProperty("http.proxyPort", "80");
        System.setProperty("http.nonProxyHosts", "localhost|127.*|*oracle.com");
        System.setProperty("https.proxyHost", "www-proxy.us.oracle.com");
        System.setProperty("https.proxyPort", "80");
        System.setProperty("https.nonProxyHosts", "localhost|127.*|*oracle.com");
        ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(cc);
        Client client = clientBuilder.sslContext(getSSLContext()).build();
        // client.property(ClientProperties.PROXY_URI,
        // "www-proxy.us.oracle.com:80");
        return client;
    }

    private static SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sslContext;

    }

}
