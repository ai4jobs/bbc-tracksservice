package com.iyer.arun.bbc.tracks.exceptions

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

trait TracksExceptions {
  val tracksDataAccessExceptionHandler = ExceptionHandler {
    case _: TracksDataAccessException =>
      extractUri { uri =>
        complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Request to $uri could not be handled normally"))
      }
  }
}
