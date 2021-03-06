package us.feliscat.text.normalizer

import java.nio.charset.{CodingErrorAction, StandardCharsets}
import java.nio.file.{Path, Paths}

import us.feliscat.text.{StringNone, StringOption}
import us.feliscat.util.LibrariesConfig

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.sys.process.Process
import scala.util.matching.Regex

/**
  * @author K.Sakamoto
  *         Created on 2016/02/20
  */
class DictionaryBasedNormalizer(dictionaryNameOpt: StringOption) {
  private def ascii2native(inputPath: Path): Iterator[String] = {
    import us.feliscat.util.process.ProcessBuilderUtils._
    Process(
      s"${System.getProperty("java.home")}/../bin/native2ascii" ::
      "-reverse" ::
      "-encoding" :: "UTF-8" ::
      inputPath.toAbsolutePath.toString :: Nil).lineStream(
        StandardCharsets.UTF_8,
        CodingErrorAction.REPORT,
        CodingErrorAction.REPORT,
        StringNone,
        30.seconds)
  }
  private val regex: Regex = """([^#:][^:]*):\[([^#]+)\](?:#.*)?""".r
  private val terms: Seq[(String, String)] = initialize

  private def initialize: Seq[(String, String)] = {
    if (dictionaryNameOpt.isEmpty) {
      return Nil
    }
    val dictionaryName: String = dictionaryNameOpt.get
    val map = mutable.Map.empty[String, List[String]]
    val buffer = ListBuffer.empty[(String, String)]
    val filePath: Path = Paths.get(LibrariesConfig.resourcesDir, "normalizer", dictionaryName).toAbsolutePath
    ascii2native(filePath) foreach {
      case regex(representation, notationalVariants) =>
        val trimmedRepresentation: String = representation.trim match {
          case "\"\"" => ""
          case otherwise => otherwise
        }
        val sortedNotationalVariants: List[String] = sortNotationVariants(notationalVariants.split(',').toList)
        map(trimmedRepresentation) = if (map.contains(trimmedRepresentation)) {
          sortNotationVariants(map(trimmedRepresentation) ++ sortedNotationalVariants)
        } else {
          sortedNotationalVariants
        }
      case _ =>
        //Do nothing
    }
    sortRepresentations(map.keySet.toList) foreach {
      representation: String =>
        map(representation) foreach {
          notationalVariant: String =>
            buffer += ((notationalVariant, representation))
        }
    }
    buffer.result
  }

  def getTerms: Seq[(String, String)] = terms

  protected def sortNotationVariants(notationVariants: List[String]): List[String] = {
    notationVariants.sorted//alphabetical order
  }

  protected def sortRepresentations(representations: List[String]): List[String] = {
    representations.sorted//alphabetical order
  }

  def normalize(text: StringOption): StringOption = {
    text map {
      t: String =>
        var result: String = t
        if (terms.nonEmpty) {
          terms foreach {
            case (term, replacement) =>
              result = replaceAll(result, term, replacement)
            case _ =>
              //Do nothing
          }
        }
        result
    }
  }

  protected def replaceAll(input: String, term: String, replacement: String): String = {
    import us.feliscat.util.StringUtils._
    input.replaceAllLiteratim(term, replacement)
  }
}
