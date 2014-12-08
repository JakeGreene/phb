package models

import play.api.db.slick.Config.driver.simple._

class ClassSpellsTable(tag: Tag) extends Table[ClassSpell](tag, "class_spells") {
  def classId = column[Int]("class_id")
  def spellId = column[Int]("spell_id")
  def level = column[Int]("level")

  def pk = primaryKey("pk_class_spell", (classId, spellId))
  def dndClass = foreignKey("class_fk", classId, TableQuery[ClassTable])(_.id)
  def spell = foreignKey("spell_fk", spellId, TableQuery[SpellsTable])(_.id)
  
  def * = (classId, spellId, level) <> (ClassSpell.tupled, ClassSpell.unapply)
}