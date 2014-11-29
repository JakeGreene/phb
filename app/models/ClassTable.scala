package models

import play.api.db.slick.Config.driver.simple._

class ClassTable(tag: Tag) extends Table[DnDClass](tag, "CLASSES") {
  def id = column[Int]("CLASS_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def * = (id.?, name) <> (DnDClass.tupled, DnDClass.unapply)
}