package models

import play.api.db.slick.Config.driver.simple._

class ClassTable(tag: Tag) extends Table[DnDClass](tag, "classes") {
  def id = column[Int]("class_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def * = (id.?, name) <> (DnDClass.tupled, DnDClass.unapply)
}