package com.iyer.arun.bbc.tracks.app

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.iyer.arun.bbc.tracks.actors.TracksActor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object TracksServer extends App with TracksRoutes {

  implicit val system: ActorSystem = ActorSystem("TracksServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  lazy val routes: Route = tracksRoutes

  val tracksActor: ActorRef = system.actorOf(TracksActor.props, "tracksActor")
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(tracksRoutes, args(0), args(1).toInt)

  serverBinding.onComplete {
    case Success(bound) =>
      logger.info(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      logger.error(s"Server could not start!", e)
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
