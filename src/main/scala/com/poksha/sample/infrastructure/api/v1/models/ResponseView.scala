package com.poksha.sample.infrastructure.api.v1.models

sealed trait ResponseView[TypeOfDataField] {
  val success: Boolean
  val message: String
  val data: TypeOfDataField
}
object ResponseView {

  case class SuccessView[ResultView] private (
      success: Boolean = true,
      message: String = "Success",
      data: ResultView
  ) extends ResponseView[ResultView] {}
  object SuccessView {
    def apply[ResultView](data: ResultView): SuccessView[ResultView] = {
      new SuccessView(data = data)
    }
  }

  case class FailureView private (
      success: Boolean = false,
      message: String,
      data: String,
      errorCode: Int
  ) extends ResponseView[String]
  object FailureView {
    def apply(error: ViewError): FailureView = {
      new FailureView(
        message = error.msg,
        data = "", // 処理失敗時はdataは空で返却する
        errorCode = error.code
      )
    }
  }
}
