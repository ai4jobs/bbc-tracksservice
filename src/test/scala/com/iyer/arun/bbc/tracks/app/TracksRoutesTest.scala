package com.iyer.arun.bbc.tracks.app

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.iyer.arun.bbc.tracks.actors.TracksActor
import com.iyer.arun.bbc.tracks.domain.{Availability, Titles, Track}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class TracksRoutesTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with TracksRoutes {
  override val tracksActor: ActorRef = system.actorOf(TracksActor.props, "tracksActor")

  lazy val routes: Route = tracksRoutes

  "TracksRoutes" should {
    "return no tracks if no tracks are present (GET /tracks)" in {
      val request = HttpRequest(uri = "/tracks")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"tracks":[]}""")
      }
    }

    "be able to add a new track if track with the id does not already exist (POST /tracks)" in {
      val track = Track("track", "0001", "urn:test-urn:0001", Titles("primary001", "secondary001", "tertiary001"), Availability("2019-09-04T00:00:00Z", "2019-10-04T00:00:00Z", "Available for a month"))
      val trackEntity = Marshal(track).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      val request = Post("/tracks").withEntity(trackEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"Track with id: 0001 successfully created."}""")
      }
    }

    "fetch a track given its id" in {
      val request = HttpRequest(uri = "/tracks/0001")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"availability":{"from":"2019-09-04T00:00:00Z","label":"Available for a month","to":"2019-10-04T00:00:00Z"},"id":"0001","titles":{"primary":"primary001","secondary":"secondary001","tertiary":"tertiary001"},"trackType":"track","urn":"urn:test-urn:0001"}""")
      }
    }

    "be able to update an existing track if a track with the id already exists (POST /tracks)" in {
      val updateTrack = Track("track", "0001", "urn:test-urn:0001-updated", Titles("updated-primary001", "updated-secondary001", "updated-tertiary001"), Availability("2019-09-04T00:00:00Z", "2019-11-04T00:00:00Z", "Available for 2 months"))
      val updateTrackEntity = Marshal(updateTrack).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      val request = Post("/tracks").withEntity(updateTrackEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"There was a track present with id 0001. This track was updated"}""")
      }
      val verifyGetTracksRequest = Get("/tracks")

      verifyGetTracksRequest  ~> routes ~> check {
        entityAs[String] should ===("""{"tracks":[{"availability":{"from":"2019-09-04T00:00:00Z","label":"Available for 2 months","to":"2019-11-04T00:00:00Z"},"id":"0001","titles":{"primary":"updated-primary001","secondary":"updated-secondary001","tertiary":"updated-tertiary001"},"trackType":"track","urn":"urn:test-urn:0001-updated"}]}""")
      }
    }

    "be able to delete a track given its id (DELETE /tracks)" in {
      val request = Delete(uri = "/tracks/0001")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"Track with id: 0001 was successfully deleted."}""")
      }
      val verifyGetTracksRequest = Get("/tracks")

      verifyGetTracksRequest  ~> routes ~> check {
        entityAs[String] should ===("""{"tracks":[]}""")
      }
    }

    "return an appropriate message when a track wasn't found to be deleted (DELETE /tracks)" in {
      val request = Delete(uri = "/tracks/non-existent-id")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"No track found with non-existent-id. Nothing was deleted"}""")
      }
    }
  }

}
