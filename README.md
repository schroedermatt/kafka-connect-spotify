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

3. Duplicate `spotify-source.template.json` and rename the new file `spotify-source.json` 
    - git will ignore `spotify-source.json` so that you don't have to worry about committing Spotify credentials
4. Update `spotify-source.json` with Spotify Credentials
    - See [Generating Spotify Credentials](#generating-spotify-credentials) section for more details
5. POST configuration to connect worker to start connector
    ```
    > curl -X POST -H "Content-Type: application/json" --data @spotify-source.json localhost:8083/connectors
    ```

6. Debugging is enabled by default. See the [IntelliJ Remote Debugger Setup](#intellij-remote-debugger-setup) section for 
instructions on attaching to the remote process and the [Debugging](#debugging) section for general debugging details.
    - If `DEBUG_SUSPEND_FLAG` is set to `y` in the `docker-compose` file, the connect service's startup will pause until 
    a remote debugger is connected. 

### Viewing Connectors & Topics

- Confluent Control Center (running at `localhost:9021`)
- Kafka Topics UI (running at `localhost:8000`)

_If you don't care about these, feel free to comment them out of the `docker-compose` file to save memory usage._

### Connector Configuration
See `spotify-source.json` for full configuration set.

Spotify Config Values:
- `spotify.oauth.clientId`: The client ID provided when setting up a Spotify Integration.
- `spotify.oauth.clientSecret`: The client secret provided when setting up a Spotify Integration.
- `spotify.oauth.accessToken`: Access token for calling the Spotify API.
- `spotify.kafka.topic`: Name of the topic that your messages will be sent to.

### Generating Spotify Credentials
There are two types of credentials that can be provided to the connector so that it can communicate with the Spotify API.

#### Access Token
This is a simple token that will expire after 30 minutes. This approach is best for a simple test.

[Create Token](https://developer.spotify.com/console/get-recently-played)

_If using this approach, remove the client ID and secret configuration values_

#### Client ID & Client Secret (App Integration)
These are credentials that can be used to create tokens on the fly. This approach is best for those
who plan to start the Spotify connector and let it run indefinitely.
    1. Go to [Developer Dashboard](https://developer.spotify.com/dashboard/)
    2. Login w/Spotify Account
    3. Create an App
    4. Copy out Client ID & Secret for `spotify-source.json` config file

_If using this approach, remove the access token configuration value_

### Reading from a Topic
To see if messages are flying around, exec into the broker container and use the built in `kafka-console-consumer` CLI.

```
> docker exec -it ${broker-container-id} bash
> kafka-console-consumer --bootstrap-server localhost:9092 --topic spotify_play_history --from-beginning

# get number of messages written to topic
> kafka-consumer-offset-checker --topic spotify_play_history --zookeeper zookeeper:2181
```

_You can also view the Kafka Topics UI on port `8000`_


### Managing Connectors

The REST endpoints available via the connect service can help manage the setup, updating and teardown of connectors 
and their tasks. Replace `spotify-play-history` with the 'name' value from your config JSON file.

#### Configure Connector

```bash
curl -X POST -H "Content-Type: application/json" --data @spotify-source.json localhost:8083/connectors
```

#### Get Active Connectors
```bash
curl localhost:8083/connectors
```

#### Get Active Tasks for a Connector
```bash
curl localhost:8083/connectors/spotify-play-history/tasks | jq
```

#### Get Connector Status
```bash
curl localhost:8083/connectors/spotify-play-history/status | jq
```

#### Get Connector Configuration
```bash
curl localhost:8083/connectors/spotify-play-history | jq
```

#### Pause Connector
```bash
curl -X PUT localhost:8083/connectors/spotify-play-history/pause
```

#### Resume Connector
```bash
curl -X PUT localhost:8083/connectors/spotify-play-history/resume
```

#### Delete Connector
```bash
curl -X DELETE localhost:8083/connectors/spotify-play-history
```

#### [More Details](https://docs.confluent.io/current/connect/managing.html)

### Debugging
The connect service's docker-compose setup has a few properties than enable remote debugging of the connector.

```yaml
# this snippet only contains properties related to debugging
connect:
    ports:
      # expose the default remote debug port
      - "5005:5005"
    environment:
      # enable remote debugging
      KAFKA_DEBUG: y
      # suspend startup until debugger is attached (optional)
      DEBUG_SUSPEND_FLAG: y
```

The `DEBUG_SUSPEND_FLAG` can be helpful if there are errors being thrown during startup that you want to step through.
Without it the connector and its task(s) might get up and running before you have a chance to step into it.

#### IntelliJ Remote Debugger Setup
   1. Navigate to Run > Edit Configurations
   2. Select 'Add New Configuration (⌘N)'
   3. Name the Configuration, Set `Host: localhost` and `Port: 5005`
   4. Save Configuration and Start Debugger
    
### Sink Connector Idea

If a song comes through more than x times, stuff it in a "favorites playlist"