Requirements:
----------------------------------------------------------------------------------------------------------------
    JDK 1.7 or above
    Apache Maven
	
To compile
-----------------------------------------------------------------------------------------------------------------
Execute the following command from the 'DistributedSearchUDP' directory
mvn clean compile assembly:single

To Run
-----------------------------------------------------------------------------------------------------------------
Execute the following command from the 'DistributedSearchUDP' directory
java -cp target/DistributedSearchSystem-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.uom.cse.distsearch.NodeApp