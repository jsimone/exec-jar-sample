import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
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
    
    private static boolean isJarOrWar(URL artifactClasspathUrl) {
        String extension = 
            artifactClasspathUrl.getPath().substring(artifactClasspathUrl.getPath().length() - 3);
        return "jar".equalsIgnoreCase(extension) || "war".equalsIgnoreCase(extension);
    }
    
    private boolean isCurrentJar(URL artifactClasspathUrl) {
        return artifactClasspathUrl.getPath().equals(getArtifactLocation().getPath());
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        if(args.length<1) {
            System.out.println("Missing required argument: path_to_webapp");
            System.exit(1);
        }
        
        Server server = new Server(8080);
        WebAppContext root = new WebAppContext();
        JettyLauncher jettyLauncher = new JettyLauncher();

        root.setContextPath("/");
        root.setDescriptor(args[0]+"/WEB-INF/web.xml");
        root.setResourceBase(args[0]);
        
        URLClassLoader defaultClassloader = (URLClassLoader)JettyLauncher.class.getClassLoader();
        URL[] classloaderUrls = defaultClassloader.getURLs();
        
        //if the classpath contains a single jar or war which is the current
        //archive then this is a -jar execution and we need to enhance the classpath
        //with the values from the manifest of the archive
        if(classloaderUrls.length == 1 
                && isJarOrWar(classloaderUrls[0]) 
                && jettyLauncher.isCurrentJar(classloaderUrls[0])) {
            JarFile jarFile = new JarFile(new File(jettyLauncher.getArtifactLocation().getPath()));
            Attributes attributes = jarFile.getManifest().getMainAttributes();
            String classpath = attributes.getValue("Class-Path");
            classpath = StringUtil.replace(classpath, " ", ";");
            root.setExtraClasspath(classpath);            
        }
        
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

}
