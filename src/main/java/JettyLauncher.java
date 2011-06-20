import java.io.File;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * 
 * This class launches the web application in an embedded Jetty container.
 * This is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 * @author John Simone
 */
public class JettyLauncher {
    
    private URL getArtifactLocation() {
        return this.getClass().getProtectionDomain().getCodeSource().getLocation();
    }
    
    private static void startJetty(String webappDirLocation, String classpath, int port) throws Exception {
        Server server = new Server(port);
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation+"/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        //Jetty expects the classpath to be semi-colon 
        classpath = StringUtil.replace(classpath, " ", ";");
        
        root.setExtraClasspath(classpath);            
        
        //Parent loader priority is a class loader setting that Jetty accepts.
        //By default Jetty will behave like most web containers in that it will
        //allow your application to replace libraries even if they are part of the
        //container. Setting parent loader priority to true changes this behavior.
        //Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);
        
        server.setHandler(root);
 
        server.start();
        server.join();        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        String webappDirLocation = null;
        if(args.length<1) {
            System.out.println("Missing argument: path_to_webapp_directory. Checking default location: 'src/main/webapp/'");
            webappDirLocation = "src/main/webapp/";
        } else {
            webappDirLocation = args[0];
        }
        
        File webAppDir = new File(webappDirLocation);
        if(!webAppDir.exists() || !webAppDir.isDirectory()) {
            System.out.println("Error locating the webapp directory specified: " + webappDirLocation);
            System.exit(1);
        }

        //Retrieve the classpath value from the manifest of the archive
        //this will be used to add to the classpath of the webapp. This replaces
        //the usual process of storing jars in the WEB-INF/lib and allows them to
        //be accessed externally
        JettyLauncher jettyLauncher = new JettyLauncher();
        JarFile jarFile = new JarFile(new File(jettyLauncher.getArtifactLocation().getPath()));
        Attributes attributes = jarFile.getManifest().getMainAttributes();
        String classpath = attributes.getValue("Class-Path");

        //Start the jetty server
        startJetty(webappDirLocation, classpath, 8080);
    }

}
