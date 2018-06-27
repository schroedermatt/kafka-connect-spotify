package com.mschroeder.kafka.connect.spotify.schema

import org.apache.kafka.connect.data.Struct
import com.wrapper.spotify.model_objects.specification.ArtistSimplified as ArtistModel
import com.wrapper.spotify.model_objects.specification.Context as ContextModel
import com.wrapper.spotify.model_objects.specification.PlayHistory as PlayHistoryModel
import com.wrapper.spotify.model_objects.specification.TrackSimplified as TrackModel

/**
 * Extension methods for the Spotify Models to create a Kafka Connect Struct
 * https://kotlinlang.org/docs/reference/extensions.html
 */

fun PlayHistoryModel.createStruct(): Struct = Struct(PlayHistory.SCHEMA)
        .put(PlayHistory.PLAYED_AT_FIELD, this.playedAt)
        .put(PlayHistory.TRACK_FIELD, this.track.createStruct())
        .put(PlayHistory.CONTEXT_FIELD, this.context?.createStruct())

fun TrackModel.createStruct() : Struct = Struct(Track.SCHEMA)
        .put(BaseSchema.HREF_FIELD, this.href)
        .put(BaseSchema.ID_FIELD, this.id)
        .put(BaseSchema.NAME_FIELD, this.name)
        .put(BaseSchema.TYPE_FIELD, this.type.type)
        .put(BaseSchema.URI_FIELD, this.uri)
        .put(Track.ARTISTS_FIELD, this.artists.first().createStruct())
        // todo - support array of Artists
        //.put(Track.ARTISTS_FIELD, this.artists.map { ArtistModel::createStruct }.toMutableList())
        .put(Track.DURATION_MS_FIELD, this.durationMs)
        .put(Track.EXPLICIT_FIELD, this.isExplicit)
        .put(Track.IS_PLAYABLE_FIELD, this.isPlayable)
        .put(Track.PREVIEW_URL_FIELD, this.previewUrl)
        .put(Track.TRACK_NUM_FIELD, this.trackNumber)

fun ArtistModel.createStruct() : Struct = Struct(Artist.SCHEMA)
        .put(BaseSchema.HREF_FIELD, this.href)
        .put(BaseSchema.ID_FIELD, this.id)
        .put(BaseSchema.NAME_FIELD, this.name)
        .put(BaseSchema.TYPE_FIELD, this.type.type)
        .put(BaseSchema.URI_FIELD, this.uri)

fun ContextModel.createStruct() : Struct = Struct(Context.SCHEMA)
            .put(BaseSchema.HREF_FIELD, this.href)
            .put(BaseSchema.TYPE_FIELD, this.type.type)
            .put(BaseSchema.URI_FIELD, this.uri)