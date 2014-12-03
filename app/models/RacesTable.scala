package models

import play.api.db.slick.Config.driver.simple._

class RacesTable(tag: Tag) extends Table[Race](tag, "races") {
  def id = column[Int]("race_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def * = (id.?, name) <> (Race.tupled, Race.unapply)
}