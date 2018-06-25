# kafka-connect-spotify

Kafka Source Connector using Spotify as the data source.

### Getting Started
1. Clone and build project
```
git clone https://github.com/msschroe3/kafka-connect-spotify
cd kafka-connect-spotify
./gradlew clean shadowJar
```
2. Start docker environment by running
```
docker-compose up -d
```
3. Update `spotify-source.json` with Spotify access token
4. POST configuration to connect worker to start connector
```
curl -X POST -H "Content-Type: application/json" --data @spotify-source.json localhost:8083/connectors
```
5. Navigate to Confluent Control Center to verify connector was configured

### Confluent Control Center
The confluent control center is running. Navigate to `localhost:9021` to see the control center.

### [Managing Running Connectors](https://docs.confluent.io/current/connect/managing.html#managing-running-connectors)

### Connector Configuration
See `spotify-source.json` for full configuration set.

Spotify Config Values:
- `spotify.oauth.accessToken`
- `spotify.kafka.topic`
