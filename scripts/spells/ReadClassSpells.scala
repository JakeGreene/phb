package spells

import scala.io.Source
import java.io._
import models._
import play.api.libs.json._
import dispatch._
import dispatch.Defaults._
import scala.concurrent.Await
import scala.concurrent.duration._

case class SpellLevel(id: Int, level: Int)
case class ClassSpells(name: String, levels: Seq[ClassLevel])
case class ClassLevel(level: Int, spells: Seq[SpellName])

object ReadClassSpells {
  implicit val spellReads = Json.reads[Spell]
  implicit val classReads = Json.reads[DnDClass]
  implicit val classWrites = Json.writes[DnDClass]
  implicit val classSpellWrites = Json.writes[ClassSpell]
  implicit val spellLevelWrites = Json.writes[SpellLevel]
  implicit val classLevelReads = Json.reads[ClassLevel]
  implicit val classSpellsReads = Json.reads[ClassSpells]

  def parseClassData(data: Source): Seq[ClassSpells] = {
    val classData = Json.parse(data.mkString)
    (classData \ "classes").as[Seq[ClassSpells]]
  }

  def apply(host: String = "http://localhost:9000"): Unit = {
    val classSpellSource = Source.fromFile("data/class-spells.json")
    val allClassData = parseClassData(classSpellSource)
    
    val baseUrl = host
    def spellApi = url(s"${baseUrl}/spells")
    def classApi = url(s"${baseUrl}/classes")

    allClassData.foreach { classData =>
      val numSpells = classData.levels.map(_.spells.size).sum
      println(s"${classData.name} has ${numSpells} spells")
      val spellResponse = Http(spellApi OK as.String).apply()
      val spells = Json.parse(spellResponse).as[Seq[Spell]]

      val classResponse = Http(classApi OK as.String).apply()
      val classes = Json.parse(classResponse).as[Seq[DnDClass]]

      val classId = classes.find(_.name.trim == classData.name.trim).flatMap(_.id).get

      val classLevelsByName = classData.levels.map { levelSpells => 
        val level = levelSpells.level
        val names = levelSpells.spells
        names.map(level -> _)
      }.flatten.map { case (lvl, name) => 
        name.trim.filterNot(_ == '\'') -> lvl 
      }.toMap
      if (classLevelsByName.keys.size != numSpells) println(s"Started $numSpells but have ${classLevelsByName.keys.size}")
      
      val classFullSpells = spells.filter(s => classLevelsByName.contains(s.name.trim))
      val notFound = classFullSpells.map(_.name).toSet.diff(classLevelsByName.keySet)
      if (notFound.nonEmpty) println(s"${classData.name} missing $notFound")

      val classSpellGroups = classFullSpells.map(s => SpellLevel(s.id.get, classLevelsByName(s.name))).grouped(20).map(s => Json.toJson(s))
      def classSpellApi = url(s"${baseUrl}/classes/${classId}/spells/all")
      def classSpellJsonApi = classSpellApi.setContentType("application/json", "UTF-8")
      def sendClassSpell(j: JsValue) = classSpellJsonApi << j.toString
      val responses = Future.sequence(classSpellGroups.map(sendClassSpell).map(request => Http(request OK as.String)))
      Await.result(responses, 1.minutes)
    }

  }

}

