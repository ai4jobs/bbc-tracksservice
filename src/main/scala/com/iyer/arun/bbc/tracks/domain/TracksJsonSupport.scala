package com.iyer.arun.bbc.tracks.domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.iyer.arun.bbc.tracks.actors.TracksActor.Feedback
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait TracksJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val titlesJsonFormat: RootJsonFormat[Titles] = jsonFormat(Titles, "primary", "secondary", "tertiary")
  implicit val availabilityJsonFormat: RootJsonFormat[Availability] = jsonFormat(Availability, "from", "to", "label")
  implicit val trackJsonFormat: RootJsonFormat[Track] = jsonFormat(Track, "trackType", "id", "urn", "titles", "availability")
  implicit val tracksJsonFormat: RootJsonFormat[Tracks] = jsonFormat1(Tracks)

  implicit val logEventJsonFormat: RootJsonFormat[Feedback] = jsonFormat1(Feedback)
}
