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

object HandBook extends Controller {
  
  implicit val spellReads = Json.reads[Spell]
  implicit val spellWrites = Json.writes[Spell]
  val Spells = TableQuery[SpellsTable]

  implicit val classReads = Json.reads[DnDClass]
  implicit val classWrites = Json.writes[DnDClass]
  val DnDClasses = TableQuery[ClassTable]
  
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
    val builder = new HandbookBuilder(fonts, 55, 13, 2)
    builder.start()
    Spells.foreach { spell =>
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
  
  def getRaces() = getAll(Races)
  
  def addRace() = addTo(Races)
  
  private def getAll[T: Writes](access: TableQuery[_ <: Table[T]]) = DBAction { implicit rs =>
    val all = access.list
    Ok(Json.toJson(all))
  }
  
  private def addTo[T: Reads](access: TableQuery[_ <: Table[T]]) = Action(parse.json) { request =>
    val thing = request.body.asOpt[T]
    thing.fold(BadRequest("Not a valid input")) { t =>
      DB.withSession { implicit session =>
        access.insert(t)
      }
      Ok(s"Stored thing $t")
    }
  }
}