package com.poksha.sample.infrastructure.log

import ch.qos.logback.classic.pattern.MDCConverter
import org.log4s.MDC

import java.util.UUID

case class MetaData(value: Map[String, String])

object LogContextPropagator extends MDCConverter {
  val requestId = "requestId"
  def logContext[A, B](
      arg: A,
      metaData: Option[MetaData] = None
  )(f: A => B): B = {
    // 呼び出し元から情報を受け取りたい場合は metaData フィールドを使ってMDC に put する
    MDC.put(requestId, UUID.randomUUID().toString)
    val res = f(arg)
    MDC.remove(requestId)
    res
  }
}
