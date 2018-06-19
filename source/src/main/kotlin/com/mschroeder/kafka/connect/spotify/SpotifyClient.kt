package com.mschroeder.kafka.connect.spotify

class SpotifyClient(val oauthToken: String) {

    // remove once client is implemented
    fun fetchMockData(): String {
        return "spotify data fetched for $oauthToken"
    }
}
