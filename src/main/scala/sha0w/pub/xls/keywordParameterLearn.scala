package sha0w.pub.xls

import java.io.File

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

object keywordParameterLearn {
  var paraKeyword = 1.0
  var paraAbs = 0.2
  var paraTitle = 0.4
  var learningRate = 0.001
  var thresholdNew = 2.0
  var thresholeOld = 6.0
  def main(args: Array[String]): Unit = {
    val wb = new XSSFWorkbook(new File("C:\\Users\\coco1\\IdeaProjects\\KeywordCompareProcess\\src\\main\\scala\\sha0w\\pub\\xls\\res\\补充2018年新词_A.xlsx"))
    val sheet: XSSFSheet = wb.getSheetAt(0)
    val iter = sheet.iterator()
    var index = 0
    while (iter.hasNext) {
      val row = iter.next()
      if (index == 0) {
        index += 1
      } else {

      }
    }
  }
  class record (isnew : Boolean, fApply : Int, fRos : Int, fApplyKey : Int, fRosKey : Int) {
    val fRosTitle: Int = fRos - fRosKey
  }
}
