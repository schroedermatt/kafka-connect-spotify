# kafka-connect-spotify

Kafka Source Connector using Spotify as the data source.

### Getting Started
1. Clone and build the project
    ```
    > git clone https://github.com/msschroe3/kafka-connect-spotify
    > cd kafka-connect-spotify
    > ./gradlew clean shadowJar
    ```
    
2. Start docker environment by running
    ```
    > docker-compose up -d
    ```

3. Update `spotify-source.json` with Spotify access token ([Get token here](https://developer.spotify.com/console/get-recently-played))
4. POST configuration to connect worker to start connector
    ```
    > curl -X POST -H "Content-Type: application/json" --data @spotify-source.json localhost:8083/connectors
    ```

5. If `DEBUG_SUSPEND_FLAG` is set to `y` in the `docker-compose` file, the connect service's startup will pause until 
a remote debugger is connected.
    - IntelliJ Remote Debugger Setup
        - Run > Edit Configurations
        - Add New Configuration (⌘N)
        - Name Configuration, Set Host=location and Port=5005
        - Save Configuration and Start Debugger

### Confluent Control Center
The confluent control center is running. Navigate to `localhost:9021` to see the control center.

### [Managing Running Connectors](https://docs.confluent.io/current/connect/managing.html#managing-running-connectors)
This link has examples of performing various actions on Connectors (i.e. restart, pause, delete, etc)

### Connector Configuration
See `spotify-source.json` for full configuration set.

Spotify Config Values:
- `spotify.oauth.accessToken`: Access token for calling the Spotify API.
- `spotify.kafka.topic`: Name of the topic that your messages will be sent to.

### Reading from a Topic
To see if messages are flying around, exec into the broker container and use the built in `kafka-console-consumer` CLI.

```
> docker exec -it ${broker-container-id} bash
> kafka-console-consumer --bootstrap-server localhost:9092 --topic spotify_source --from-beginning
```