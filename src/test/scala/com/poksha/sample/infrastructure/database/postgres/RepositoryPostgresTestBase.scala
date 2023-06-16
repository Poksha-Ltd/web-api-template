package com.poksha.sample.infrastructure.database.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import org.slf4j.LoggerFactory

trait RepositoryPostgresTestBase {
  private[this] val logger = LoggerFactory.getLogger(getClass)

  // テストではテスト用のDBを使用します
  val testDataBase: String = "test_authentication"

  val testConfig: PostgresConfig = PostgresConfig(
    url = s"jdbc:postgresql:$testDataBase",
    user = "postgres",
    pass = "password"
  )

  private val testTransActor = Transactor.fromDriverManager[IO](
    driver = testConfig.driver,
    url = testConfig.url,
    user = testConfig.user,
    pass = testConfig.pass
  )

  def clearTestData(): Unit = {
    logger.debug(s"start to clear from $testDataBase ...")
    val resultCount = sql"DELETE FROM auth_user".update.run
      .transact(testTransActor)
      .unsafeRunSync()
    logger.debug(s"cleared $resultCount rows from $testDataBase !!!")
  }
}
