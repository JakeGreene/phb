package controllers

import models._
import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.edit._
import org.apache.pdfbox.pdmodel.font._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.DB
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import com.fasterxml.jackson.core.JsonParseException

object HandBook extends Controller {
  
  implicit val spellReads = Json.reads[Spell]
  implicit val spellWrites = Json.writes[Spell]
  val Spells = TableQuery[SpellsTable]

  implicit val classReads = Json.reads[DnDClass]
  implicit val classWrites = Json.writes[DnDClass]
  val DnDClasses = TableQuery[ClassTable]
  
  val ClassSpells = TableQuery[ClassSpellsTable]
  
  implicit val raceReads = Json.reads[Race]
  implicit val raceWrites = Json.writes[Race]
  val Races = TableQuery[RacesTable]
  
  val fonts = BookFont(Font(PDType1Font.TIMES_BOLD, 14),
                       Font(PDType1Font.TIMES_ITALIC, 12),
                       Font(PDType1Font.TIMES_BOLD, 12),
                       Font(PDType1Font.TIMES_ROMAN, 12))
  
  def index() = Action {
    DB.withSession { implicit session => 
      val classes = DnDClasses.run
      val races = Races.run
      Ok(views.html.Handbook.index(races, classes))
    }
  }
  
  def phb() = DBAction { implicit rs =>
    val builder = new HandbookBuilder(fonts, 52, 13, 2)
    builder.start()
    val spellsByName = Spells.list.sortWith { case (a, b) =>
      a.name < b.name
    }
    
    spellsByName.foreach { spell =>
      builder.addSpell(spell)
    }  
    val document = builder.toPDF
    Ok.chunked(Enumerator.outputStream { os =>
      document.save(os)
      os.close()
    }).withHeaders(
      CONTENT_TYPE -> "application/pdf",
      CONTENT_DISPOSITION -> "filename=player-handbook.pdf"
    )
  }
  
  def getSpells() = getAll(Spells)
  def addSpell() = addTo(Spells)
  
  def getClasses() = getAll(DnDClasses)
  def addClass() = addTo(DnDClasses)
  
  def getClassSpells(classId: Int) = DBAction { implicit rs =>
    val spells = for {
      dndClass <- DnDClasses 
      if dndClass.id === classId
      classSpell <- ClassSpells 
      if classSpell.classId === dndClass.id
      spell <- Spells 
      if spell.id === classSpell.spellId
    } yield spell
    Ok(Json.toJson(spells.list))
  }
  
  def addClassSpell(classId: Int) = Action(parse.json) { request =>
    val spellData = request.body
    val spellId = (spellData \ "id").as[Int]
    val level = (spellData \ "level").as[Int]
    DB.withSession { implicit session =>
      ClassSpells.insert(ClassSpell(classId, spellId, level))  
    }
    Ok(s"Stored spell $spellId")
  }
  
  def getRaces() = getAll(Races)
  def addRace() = addTo(Races)
  
  private def getAll[T: Writes](access: TableQuery[_ <: Table[T]]) = DBAction { implicit rs =>
    val all = access.list
    Ok(Json.toJson(all))
  }
  
  private def addTo[T: Reads](access: TableQuery[_ <: Table[T]]) = Action(parse.raw) { request =>
    val s = new String(request.body.asBytes().get)
    try {
      val json = Json.parse(s)    
      val parsed = json.validate[T]
      parsed.map { t =>
        DB.withSession { implicit session =>
          access.insert(t)
        }
        Created
      }.recoverTotal {
        e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
      }
    } catch {
      case e: JsonParseException => BadRequest(s"Could not parse $s due to ${e.toString}")
    }
  }
}