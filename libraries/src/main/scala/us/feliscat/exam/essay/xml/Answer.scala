package us.feliscat.exam.essay.xml

import us.feliscat.text.StringOption

/**
  * @param isGoldStandard is this us.feliscat.answer a gold standard?
  * @param writer writer
  * @param expression expression
  * @author K. Sakamoto
  *         Created on 2016/10/28
  */
class Answer(val isGoldStandard: Boolean,
             val writer: StringOption,
             val expression: StringOption) {
  override def toString: String = {
    s"""Is gold standard?:
       |${if (isGoldStandard) {"Yes."} else {"No."}}
       |Writer:
       |${writer.getOrElse("An unknown person.")}
       |Essay:
       |${expression.getOrElse("")}
       |""".stripMargin
  }
}
