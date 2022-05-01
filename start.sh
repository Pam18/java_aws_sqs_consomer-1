export CLASSPATH=target/sqs_consomer-1.0-SNAPSHOT.jar
export className=App
echo "## Running $className..."
mvn exec:java -Dexec.mainClass="br.com.ecommerce_sqs.$className"