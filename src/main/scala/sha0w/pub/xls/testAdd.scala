package sha0w.pub.xls

import scala.collection.mutable

object testAdd {
  def main(args: Array[String]): Unit = {
    val set = new mutable.HashSet[String]
    addComponent(set, "A01")
    addComponent(set, "A0101")
    addComponent(set, "A0101")
    addComponent(set, "A010101")
  }
  def addComponent(set : mutable.HashSet[String], applyId : String) : Unit = {
    if (!set.contains(applyId)) {
      var flagUpper = false
      for (aid <- set) {
        if (aid.contains(applyId)) flagUpper = true //set中存在下位词，这个词不加入
        if (applyId.contains(aid)) set.remove(aid) //词为set中某个词的上位词 删去这个词
      }
      if(!flagUpper) set.add(applyId)
    }
    println(set)
  }
}
