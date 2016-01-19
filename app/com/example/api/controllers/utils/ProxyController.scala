package com.example.api.controllers.utils

import scala.concurrent.{ExecutionContext, Future}

trait ProxyController extends {
  self: Controller =>

  val ws: WSClient
  implicit val ec: ExecutionContext

  def proxyIndex[T](request: Request[T], destinationUrl: String): Future[Result] = {
    ws.url(destinationUrl)
      .get()
      .map(parseResponse)
  }
  private def parseResponse(wsResponse: WSResponse): Result = {
    wsResponse.header(CONTENT_TYPE) match {
      case Some(content) if content.contains(MimeTypes.JSON) => Status(wsResponse.status)(wsResponse.json)
      case other => throw ProxyMimeTypeException(MimeTypes.JSON, other.getOrElse("none"), wsResponse.body)
    }
  }

}
