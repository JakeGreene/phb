package models

import play.api.db.slick.Config.driver.simple._

class SpellsTable(tag: Tag) extends Table[Spell](tag, "spells") {
  def id = column[Int]("spell_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def kind = column[String]("kind", O.NotNull)
  def time = column[String]("time", O.NotNull)
  def range = column[String]("range", O.NotNull)
  def components = column[String]("components", O.NotNull)
  def duration = column[String]("duration", O.NotNull)
  def body = column[String]("body", O.NotNull)
  def * = (id.?, name, kind, time, range, components, duration, body) <> (Spell.tupled, Spell.unapply)
}