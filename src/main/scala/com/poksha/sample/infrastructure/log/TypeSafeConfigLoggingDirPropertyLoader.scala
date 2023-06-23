package com.poksha.sample.infrastructure.log

import ch.qos.logback.core.PropertyDefinerBase
import com.typesafe.config.ConfigFactory

class TypeSafeConfigLoggingDirPropertyLoader extends PropertyDefinerBase {
  var prop: Option[String] = None

  override def getPropertyValue: String = {
    prop match {
      case Some(propertyName) => ConfigFactory.load().getString(propertyName)
      case None =>
        throw new IllegalArgumentException(
          s"loggingDirProperty $prop cannot be loaded"
        )
    }
  }

  // クラスロード時 logback.xml で設定した define.${PROPERTY} に応じた setter が呼び出される
  // 例えば logback.xml に define.propName が定義された場合、setPropName が呼び出され、メソッドの引数に logback.xml で設定した値がバインドされる
  def setLoggingDirProperty(propertyName: String): Unit = {
    prop = Some(propertyName)
  }
}
