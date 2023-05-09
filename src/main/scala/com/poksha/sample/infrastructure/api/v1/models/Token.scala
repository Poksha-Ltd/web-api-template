package com.poksha.sample.infrastructure.api.v1.models

case class Token(accessToken: String, tokenType: String = "bearer")
