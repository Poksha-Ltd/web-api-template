package com.poksha.sample.infrastructure.log

import ch.qos.logback.core.PropertyDefinerBase
import com.typesafe.config.ConfigFactory

class TypeSafeConfigLoggingLevelPropertyLoader extends PropertyDefinerBase {
  var prop: Option[String] = None

  override def getPropertyValue: String = {
    prop match {
      case Some(propertyName) => ConfigFactory.load().getString(propertyName)
      case None =>
        throw new IllegalArgumentException(
          s"loggingLevelProperty $prop cannot be loaded"
        )
    }
  }

  def setLoggingLevelProperty(propertyName: String): Unit = {
    prop = Some(propertyName)
  }
}
