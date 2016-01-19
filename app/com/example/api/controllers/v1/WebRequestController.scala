package com.example.api.controllers.v1

import java.util.UUID
import javax.inject.Inject

import _root_.play.api.Configuration
import _root_.play.api.libs.ws.WSClient
import _root_.play.api.mvc.Action
import _root_.play.api.mvc.BodyParsers
import _root_.play.api.mvc.Controller
import com.example.api.controllers.utils.ConstraintReadExtensions.nonEmpty
import com.example.api.controllers.utils.ControllerConventions
import com.example.api.models.Post
import com.example.api.services.PostService
import com.ticketfly.consumer.api.controllers.v2.util.ProxyController

import scala.concurrent.ExecutionContext

class WebRequestController @Inject() (webRequestService: WebRequestService, val ws: WSClient, configuration: Configuration)(implicit ec: ExecutionContext) extends Controller with ControllerConventions {

  private val serviceHost = configuration.getString("service-urls.").get

  def index: Action[AnyContent] = Action.async { request =>
    proxyIndex(request, s"$serviceHost")
  }

}
