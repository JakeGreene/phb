package models

import org.apache.pdfbox.pdmodel.font.PDType1Font

case class Font(style: PDType1Font, size: Int)
case class BookFont(title: Font, sub: Font, header: Font, body: Font)