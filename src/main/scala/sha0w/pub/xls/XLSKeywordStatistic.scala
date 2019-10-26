package sha0w.pub.xls

import java.io.{File, FileInputStream}

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{Row, Sheet, Workbook}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import sha0w.pub.xls.res.{Hierarchy, HierarchyKeyword}

import scala.collection.mutable

object XLSKeywordStatistic {
  def main(args: Array[String]): Unit = {
    val path: String = "C:\\Users\\coco1\\IdeaProjects\\KeywordCompareProcess\\src\\main\\scala\\sha0w\\pub\\xls\\res\\2019年库里的申请代码研究方向关键词-最终版（发中科院版）.xlsx"
    processStatistic(path)
  }

  /**
   * 统计
   * 系统提供末级代码个数
   * 系统提供研究方向个数（包含“其他”，“其他研究方向”）
   * 系统提供关键词个数（不同方向，不同代码下相同的关键词要重复计算进来，不要去重计算）
   *
   * @param path XLS文件地址
   */
  def processStatistic (path : String) : Unit = {
    val xlsFile = new File(path)
    val wb = processFile(xlsFile)
    val sheet: Sheet = wb.getSheetAt(0)
    val firstRowIndex = sheet.getFirstRowNum + 1   //第一行是列名，所以不读
    val lastRowIndex = sheet.getLastRowNum
    val keywordMap  = new mutable.HashMap[String, (mutable.HashSet[String], mutable.HashSet[Hierarchy], mutable.ListBuffer[String])]
    for (j <- firstRowIndex until lastRowIndex) {
      val row: Row = sheet.getRow(j)
      val applyid = row.getCell(0).getStringCellValue
      val ros = row.getCell(1).getStringCellValue
      val keyword = row.getCell(2).getStringCellValue
      val applyEnd = applyid.charAt(0).toString
//      if (applyid.contains("E05"))
      if (keywordMap.contains(applyEnd)) {
        val (codeSet, hiSet, keywordSet) = keywordMap(applyEnd)
//        codeSet.add(applyid)
        addComponent(codeSet, applyid)
        if (ros != null&&ros.trim != "")
          hiSet.add(new Hierarchy(ros, applyid))
        if (keyword != null) {
          keyword.split(",").foreach(keywordSet.append)
        }
        keywordMap.update(applyEnd, (codeSet, hiSet, keywordSet))
      } else {
        val codeSet = new mutable.HashSet[String]
        val hiSet = new mutable.HashSet[Hierarchy]
        val keywordSet = new mutable.ListBuffer[String]
//        codeSet.add(applyid)
        addComponent(codeSet, applyid)
        if (ros != null&&ros.trim != "") //研究方向为空的去除
          hiSet.add(new Hierarchy(ros, applyid))
        if (keyword != null) {
          keyword.split(",").foreach(keywordSet.append)
        }
        keywordMap.put(applyEnd, (codeSet, hiSet, keywordSet))
      }
//      申请代码	研究方向	关键词
    }
    for ((key, (codeSet, hiSet, keywordSet)) <- keywordMap) {
      println(s"the code is ${key}")
      println(s"系统提供的末级代码数为：${codeSet.size}")

      println(s"系统提供研究方向个数：${hiSet.size}")

      println(s"系统提供关键词个数：${keywordSet.size}")
    }

  }

  def processFile (file : File) : Workbook = {
    val split = file.getName.split("\\.") //.是特殊字符，需要转义！！！！！
    var wb : Workbook = null
    //根据文件后缀（xls/xlsx）进行判断
    if ("xls" == split(1)) {
      val fis = new FileInputStream(file) //文件流对象
      wb = new HSSFWorkbook(fis)
    }
    else if ("xlsx" == split(1)) wb = new XSSFWorkbook(file);
    else {
      throw new Exception("文件类型错误!")
    }
    wb
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
  }
}
