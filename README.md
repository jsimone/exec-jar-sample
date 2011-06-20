run mvn package to build the applicaiton.

Once you have the project built it can be run with:

java -jar target/exec-jar-sample-1.0-SNAPSHOT.jar src/main/webapp

Execute the package jar file with the java -jar command. The argument points to the webapp folder. If run from the root of the project you can omit this parameter and it will automatically default to src/main/webapp/.
