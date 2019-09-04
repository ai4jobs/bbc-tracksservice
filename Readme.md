#### Build
Build uber executable jar(with tests run)
````shell script
mvn clean install
````
An executable jar is built via the maven shade plugin and tests are run using maven scalatest plugin
#### Run
Locate the uber jar in the target directory and run
````shell script
java -jar <jarfile> <hostname> <port>
eg: java -jar track-service-1.0-SNAPSHOT-uber.jar localhost 8080
````
You should see log
````text
[INFO] [09/04/2019 22:44:22.894] [TracksServer-akka.actor.default-dispatcher-3] [TracksRoutes(akka://TracksServer)] Server online at http://127.0.0.1:8080/
````
#### Usage
###### Create or Update a Track (Matched via id):
````shell script
curl -H "Content-Type: application/json" -X POST -d '{"trackType": "track", "id": "nznx3r", "urn": "urn:bbc:sounds:track:nznx3r", "titles": {"primary": "AC/DC","secondary": "Highway to Hell","tertiary": "null" }, "availability": {"from": "2019-02-13T11:03:05Z","to": "2019-03-15T11:00:00Z","label": "Available for 29 days"} }' http://localhost:8080/tracks
````
###### Fetch all Tracks: 
 ````shell script
curl -X GET http://localhost:8080/tracks
````
###### Fetch a Track:
````shell script
curl -X GET http://localhost:8080/tracks/nznx3r
````
###### Delete a track:
````shell script
curl -X DELETE http://localhost:8080/tracks/nznx3r
````