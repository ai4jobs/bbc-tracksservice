package com.iyer.arun.bbc.tracks.app

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.iyer.arun.bbc.tracks.actors.TracksActor._
import com.iyer.arun.bbc.tracks.domain.{Track, Tracks, TracksJsonSupport}

import scala.concurrent.Future
import scala.concurrent.duration._

trait TracksRoutes extends TracksJsonSupport {

  implicit def system: ActorSystem

  def tracksActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  lazy val logger = Logging(system, classOf[TracksRoutes])

  lazy val tracksRoutes: Route =
    pathPrefix("tracks") {
      concat(
        pathEnd {
          concat(fetchAllTracksRoute, post {
            createOrUpdateTrackRoute
          })
        },
        path(Segment) { id =>
          concat(get {
            fetchTrackRoute(id)
          }, delete {
            deleteTrackRoute(id)
          })
        }
      )
    }

  private def deleteTrackRoute(id: String) = {
    val trackDeleted: Future[Feedback] =
      (tracksActor ? DeleteTrack(id)).mapTo[Feedback]
    onSuccess(trackDeleted) { performed =>
      logger.info("Deleted track [{}]: {}", id, performed.description)
      complete((StatusCodes.OK, performed))
    }
  }

  private def fetchTrackRoute(id: String) = {
    val maybeTrack: Future[Option[Track]] =
      (tracksActor ? FetchTrack(id)).mapTo[Option[Track]]
    rejectEmptyResponse {
      complete(maybeTrack)
    }
  }

  private def createOrUpdateTrackRoute = {
    entity(as[Track]) { track =>
      val trackCreated: Future[Feedback] =
        (tracksActor ? CreateOrUpdateTrack(track)).mapTo[Feedback]
      onSuccess(trackCreated) { performed =>
        logger.info("Created/Updated track [{}]: {}", track.id, performed.description)
        complete((StatusCodes.Created, performed))
      }
    }
  }

  private def fetchAllTracksRoute = {
    get {
      val tracks: Future[Tracks] =
        (tracksActor ? FetchAllTracks).mapTo[Tracks]
      complete(tracks)
    }
  }
}
