package sha0w.pub.xls

import java.sql.{Connection, DriverManager}
import java.util.Properties

import sha0w.pub.xls.res.{Hierarchy, HierarchyKeyword}

import scala.collection.mutable

object KeywordUsageStatistic {
  def main(args: Array[String]): Unit = {
    val property = new Properties
    property.put("user","root")
    property.put("password", "Bigdata,1234")
    property.put("driver","com.mysql.cj.jdbc.Driver")
    val mysqladd = "jdbc:mysql://192.168.3.131:3306/NSFC_KEYWOR_DB"
    Class.forName("com.mysql.cj.jdbc.Driver")
    val conn: Connection = DriverManager.getConnection(mysqladd, property)

    val oldkeySet = new mutable.HashSet[HierarchyKeyword]

    val oldkeySQL = "SELECT APPLYID,KEYWORD,RESEARCH_FIELD FROM 2019_provided_keyword"
    val oldrsSet = new mutable.HashSet[Hierarchy]()
    val ps = conn.prepareStatement(oldkeySQL)
    val rs = ps.executeQuery()
    while (rs.next()) {
      val applyid = rs.getString("APPLYID")
      val research_field = rs.getString("RESEARCH_FIELD")
      val keyword = rs.getString("KEYWORD")
      if (keyword != null && applyid != null)
        oldkeySet.add(new HierarchyKeyword(keyword, new Hierarchy(research_field, applyid)))
      if (research_field != null && research_field.trim != ""  && applyid != null) {
        oldrsSet.add(new Hierarchy(research_field, applyid))
      }
    }
    rs.close()
    ps.close()
    for (code <- Array("A","B","C","D","E","F","G","H")) {
      val newkeySQL = s"SELECT APPLYID,KEYWORD_ZH,RESEARCH_FIELD FROM 2019_APPLICATION_NEW where APPLYID LIKE '${code}%'"
      val psnew = conn.prepareStatement(newkeySQL)
      val rsnew = psnew.executeQuery()
      var useold = 0
      val useoldDistinct : mutable.HashSet[HierarchyKeyword] = new mutable.HashSet[HierarchyKeyword]()
      val usenewDistinct : mutable.HashSet[HierarchyKeyword] = new mutable.HashSet[HierarchyKeyword]()
      var usedOldProject = 0
      var useNew = 0
      var usedNewProject = 0
      val usedRSSet = new mutable.HashSet[Hierarchy]()
      var usedZero = 0
      var usedOne = 0
      var usedTwo = 0
      var usedThree = 0
      var usedFour = 0
      var usedFive = 0
      while (rsnew.next()) {
        val applyid = rsnew.getString("APPLYID")
        val research_field = rsnew.getString("RESEARCH_FIELD")
        val keyword = rsnew.getString("KEYWORD_ZH")
        if (research_field != null && applyid != null && research_field.trim != "") {
          val h = new Hierarchy(research_field, applyid)
          if (oldrsSet.contains(h)) usedRSSet.add(h)
        }
        if (keyword != null && applyid != null) {

          var useOldFlag = false
          var useNewFlag = false
          val keywords = keyword.split("；")
          var usedNum = 0
          for (key <- keywords) {
            val hk = new HierarchyKeyword(key, new Hierarchy(research_field, applyid))
            if (oldkeySet.contains(hk)) { // 用了旧词
              usedNum += 1
              useold += 1
              useoldDistinct.add(hk)
              useOldFlag = true
            } else {
              useNew += 1
              usenewDistinct.add(hk)
              useNewFlag = true
            }
          }
          if (useOldFlag) {
            usedOldProject += 1
          }

          if (useNewFlag) {
            usedNewProject += 1
          }
          usedNum match {
            case 0 => usedZero += 1
            case 1 => usedOne += 1
            case 2 => usedTwo += 1
            case 3 => usedThree += 1
            case 4 => usedFour += 1
            case 5 => usedFive += 1
            case _ => throw new Exception
          }
        }
      }
      println(s"for code $code")
      println(s"F : 使用历年研究方向数 : ${usedRSSet.size}")
      println(s"I : 申请书使用系统提供的关键词次数（按学部统计每个研究方向下关键词被使用的次数累加）: $useold")
      println(s"J : 申请书使用系统提供的关键词个数（按学部统计每个研究方向下关键词被使用的个数累加，即同一个关键词被使用多次只算1次） ${useoldDistinct.size}")
      println(s"K : 使用系统提供关键词涉及的项目数: $usedOldProject")
      println(s"L : 自填关键词数（按学部去重统计）: ${usenewDistinct.size}")
      println(s"M : 自填关键词次数（按学部不去重统计） : $useNew")
      println(s"N : 自填关键词涉及项目数: $usedNewProject")

      println(s"O : 使用一次: $usedOne")
      println(s"O : 使用两次: $usedTwo")
      println(s"O : 使用三次: $usedThree")
      println(s"O : 使用四次: $usedFour")
      println(s"O : 使用五次: $usedFive")
      println(s"O : 使用零次: $usedZero")

      rsnew.close()
      psnew.close()
    }

  }
}
