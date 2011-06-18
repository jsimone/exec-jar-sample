run mvn package to build the applicaiton.

Once you have the project built it can be run in two ways:

Use the provided exec.sh script to launch. This loads the maven classpath into the CLASSPATH environment variable and then directly runs the JettyLauncher from the target/classes folder.

Execute the package jar file with the java -jar command. This requires an argument pointing to the webapp folder to be passed on the command line. If run from the root of the project this will be src/main/webapp. Example below:

java -jar target/exec-jar-sample-1.0-SNAPSHOT.jar src/main/webapp
