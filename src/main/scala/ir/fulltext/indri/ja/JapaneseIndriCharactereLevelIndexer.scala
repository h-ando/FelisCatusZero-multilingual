package ir.fulltext.indri.ja

import ir.fulltext.indri.MultiLingualIndriIndexer
import m17n.Japanese
import util.Config

/**
  * <pre>
  * Created on 2017/01/13.
  * </pre>
  *
  * @author K.Sakamoto
  */
object JapaneseIndriCharactereLevelIndexer extends MultiLingualIndriIndexer with Japanese {
  protected override val indices:       Array[String] = Config.characterLevelIndriIndicesInJapanese.toArray
  protected override val segmentations: Array[String] = Config.characterLevelIndriSegmentationsInJapanese.toArray
  protected override val resources:     Array[String] = Config.trecTextFormatDataInJapanese.toArray
  protected override val reviser = new JapaneseTrecTextFileFormatReviser(1, false)
}
