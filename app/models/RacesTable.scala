package models

import play.api.db.slick.Config.driver.simple._

class RacesTable(tag: Tag) extends Table[Race](tag, "RACES") {
  def id = column[Int]("RACE_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def * = (id.?, name) <> (Race.tupled, Race.unapply)
}