package com.example.api.daos

import java.util.UUID
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.reflectiveCalls
import slick.driver.JdbcProfile

// format: OFF
trait DAOConventions[T<:{ def id: UUID }] { // scalastyle:ignore structural.type

  def dbConfigProvider: DatabaseConfigProvider
  protected lazy final val dbConfig = dbConfigProvider.get[JdbcProfile]
  protected lazy final val driver = dbConfig.driver
  protected lazy final val db = dbConfig.db

  import driver.api._

  abstract class BaseTable(tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)
    def deleted: Rep[Boolean] = column[Boolean]("deleted", O.Default(false))
  }

  type ModelTable<:BaseTable

  val table: TableQuery[ModelTable]
  lazy val deleteAction = table.delete

  def all: Future[Seq[T]] = {
    db.run(table.filter(_.deleted === false).result)
  }

  def create(t: T)(implicit ec: ExecutionContext): Future[T] = {
    db.run(table += t).map(_ => t)
  }

  def findById(id: UUID): Future[Option[T]] = {
    db.run(table.filter(t => t.id === id && t.deleted === false).result.headOption)
  }

  def insertOrUpdate(t: T)(implicit ec: ExecutionContext): Future[T] = {
    db.run(table.insertOrUpdate(t)).map(_ => t)
  }

  def destroyById(id: UUID): Future[Int] = {
    val query = table.filter(_.id === id).map(_.deleted).update(true)
    db.run(query)
  }

}
// format: ON
