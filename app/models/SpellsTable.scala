package models

import play.api.db.slick.Config.driver.simple._

class SpellsTable(tag: Tag) extends Table[Spell](tag, "SPELLS") {
  def id = column[Int]("SPELL_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def kind = column[String]("KIND", O.NotNull)
  def time = column[String]("TIME", O.NotNull)
  def range = column[String]("RANGE", O.NotNull)
  def components = column[String]("COMPONENTS", O.NotNull)
  def duration = column[String]("DURATION", O.NotNull)
  def body = column[String]("BODY", O.NotNull)
  def * = (id.?, name, kind, time, range, components, duration, body) <> (Spell.tupled, Spell.unapply)
}