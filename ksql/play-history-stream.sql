CREATE STREAM play_history \
  WITH ( \
    kafka_topic='spotify_play_history', \
    value_format='JSON' \
  );