package sha0w.pub.xls

import java.sql.{Connection, DriverManager}
import java.util.Properties

import sha0w.pub.xls.res.{Hierarchy, HierarchyKeyword}

import scala.collection.mutable

object KeywordStatistic {
  def main(args: Array[String]): Unit = {
    val property = new Properties
    property.put("user","root")
    property.put("password", "Bigdata,1234")
    property.put("driver","com.mysql.cj.jdbc.Driver")
    val mysqladd = "jdbc:mysql://192.168.3.131:3306/NSFC_KEYWOR_DB"
    Class.forName("com.mysql.cj.jdbc.Driver")
    val conn: Connection = DriverManager.getConnection(mysqladd, property)

    for (i <- Array(2017,2018,2019)) {
      var all = 0
      for (code <- Array("A","B","C","D","E","F","G","H")) {
        val sql = s"select APPLYID,KEYWORD_ZH,RESEARCH_FIELD from ${i}_APPLICATION_NEW WHERE APPLYID like '${code}%'"
        val ps = conn.prepareStatement(sql)
        val rs = ps.executeQuery()
        val set = new mutable.ListBuffer[HierarchyKeyword]
        var k = 0
        while(rs.next()) {
          val applyid = rs.getString("APPLYID")
          val research_field = rs.getString("RESEARCH_FIELD")
          val keyword = rs.getString("KEYWORD_ZH")

          if (keyword != null) {
            keyword.
              split("；").
              map(str => new HierarchyKeyword(str, new Hierarchy(research_field, applyid)))
              .foreach(hk => {
                set.append(hk)
              })
          }
          k += 1
        }
        all += set.size
        println(s"YEAR : $i code : $code , keyword num is ${set.size}")
        println(s"YEAR : $i code : $code , application num is $k")
      }
        println(s"year : $i , all keyword num is $all")
    }
  }
}
