package com.iyer.arun.bbc.tracks.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.iyer.arun.bbc.tracks.domain.{Track, Tracks}

class TracksActor extends Actor with ActorLogging {

  import com.iyer.arun.bbc.tracks.actors.TracksActor._

  var tracks: Set[Track] = Set.empty;

  override def receive: Receive = {
    case FetchAllTracks =>
      fetchAllTracks()
    case FetchTrack(id) =>
      fetchTrack(id)
    case DeleteTrack(id) =>
      deleteTrack(id)
    case CreateOrUpdateTrack(newTrack) =>
      deleteTrack(newTrack)
  }

  private def deleteTrack(newTrack: Track): Unit = {
    val maybeTrack = tracks.find(_.id == newTrack.id)
    maybeTrack foreach { oldTrack => tracks -= oldTrack }
    tracks += newTrack
    if (maybeTrack.isDefined) {
      sender() ! Feedback(s"There was a track present with id ${newTrack.id}. This track was updated")
    } else {
      sender() ! Feedback(s"Track with id: ${newTrack.id} successfully created.")
    }
  }

  private def deleteTrack(id: String): Unit = {
    val maybeTracks = tracks.find(_.id == id)
    if (maybeTracks.isEmpty) {
      sender() ! Feedback(s"No track found with $id. Nothing was deleted")
    } else {
      maybeTracks foreach { track => tracks -= track }
      sender() ! Feedback(s"Track with id: $id was successfully deleted.")
    }
  }

  private def fetchTrack(id: String): Unit = {
    sender() ! tracks.find(_.id == id)
  }

  private def fetchAllTracks(): Unit = {
    sender() ! Tracks(tracks.toSeq)
    sender() ! Feedback(s"Tracks successfully returned.")
  }
}

object TracksActor {

  def props: Props = Props[TracksActor]

  final case class Feedback(description: String)

  final case class FetchAllTracks()

  final case class FetchTrack(name: String)

  final case class DeleteTrack(name: String)

  final case class CreateOrUpdateTrack(track: Track)

}