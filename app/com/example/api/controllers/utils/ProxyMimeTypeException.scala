package com.example.api.controllers.utils

case class ProxyMimeTypeException(expected: String, received: String, body: String) extends RuntimeException {
  override def getMessage(): String = s"expected content-type $expected, received $received, with body: '$body'"
}
