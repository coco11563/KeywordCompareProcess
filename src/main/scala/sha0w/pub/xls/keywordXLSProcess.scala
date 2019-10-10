package sha0w.pub.xls

import java.io.{File, FileOutputStream}
import java.sql.{Connection, DriverManager}
import java.util.Properties

import org.apache.poi.ss.usermodel.{Row, WorkbookFactory}

object keywordXLSProcess {
  def main(args: Array[String]): Unit = {
//    Class.forName("com.mysql.cj.jdbc.Driver")
    val property = new Properties
    property.put("user","root")
    property.put("password", "Bigdata,1234")
    property.put("driver","com.mysql.cj.jdbc.Driver")
    val mysqladd = "jdbc:mysql://192.168.3.131:3306/NSFC_KEYWOR_DB"
    Class.forName("com.mysql.cj.jdbc.Driver")
    val conn: Connection = DriverManager.getConnection(mysqladd, property)
    for (year <- Array("2018", "2019")) {
      mkxls(conn, year, s"target/OPT/stat/${year}/", s"${year}关键词推荐")
    }
  }

  def mkxls (conn : Connection, year: String, outname : String, filename:String) : Unit = {
    val stmt = conn.createStatement()

    for (code <- Array("A","B","C","D","E","F","G","H")) {
      val wb = WorkbookFactory.create(true)
      val wb_sub = WorkbookFactory.create(true)
      val optFile = new File(outname)
      if (!optFile.exists()) {
        optFile.mkdirs()
      }
      val file = new File(outname + filename + s"_${code}.xlsx")
      val file_sb = new File(outname + filename + s"_${code}_sub.xlsx")
      file.delete()
      file_sb.delete()
      val FOS = new FileOutputStream(file)
      val subFOS = new FileOutputStream(file_sb)
      val sheet = wb.createSheet(s"${code}")
      val sheet_sub = wb_sub.createSheet(s"${code}")
      val schemaRow = sheet.createRow(0)
      val schemaRow_sub = sheet_sub.createRow(0)
      initApplicationSchema(schemaRow)
      initApplicationSchema(schemaRow_sub)
      var index = 1
      var index_sub = 1
      val sql = s"select * from m_${year}_keyword_recommend_with_bias where applyid like '${code}%'"
      print(sql)
      val rs = stmt.executeQuery(sql)
      while(rs.next()) {
        val applyid = rs.getString("applyid")
        val research_field = rs.getString("research_field")
        val keyword = rs.getString("keyword")
        val status = rs.getString("status")

        val count = rs.getString("学部下总频次")
        val percentage = rs.getString("研究方向下总频次")
        val weight = rs.getString("学部下关键词中出现频次")
        val title_f = rs.getString("研究方向下关键词中出现频次")
        val flag = rs.getString("是否需要补充到上级")
        if (flag == "0") {
          val row = sheet.createRow(index)
          row.createCell(0).setCellValue(applyid)
          row.createCell(1).setCellValue(research_field)
          row.createCell(2).setCellValue(keyword)
          row.createCell(3).setCellValue(status)
          row.createCell(4).setCellValue(count)
          row.createCell(5).setCellValue(percentage)
          row.createCell(6).setCellValue(weight)
          row.createCell(7).setCellValue(title_f)
          index += 1
        } else {
          val row = sheet_sub.createRow(index_sub)
          row.createCell(0).setCellValue(applyid)
          row.createCell(1).setCellValue(research_field)
          row.createCell(2).setCellValue(keyword)
          row.createCell(3).setCellValue(status)
          row.createCell(4).setCellValue(count)
          row.createCell(5).setCellValue(percentage)
          row.createCell(6).setCellValue(weight)
          row.createCell(7).setCellValue(title_f)
          index_sub += 1
        }

      }
      wb.write(FOS)
      wb_sub.write(subFOS)
      FOS.flush()
      subFOS.flush()
    }
  }

  def initApplicationSchema (row : Row) : Unit = {
    row.createCell(0).setCellValue("末级代码")
    row.createCell(1).setCellValue("研究方向")
    row.createCell(2).setCellValue("关键词")
    row.createCell(3).setCellValue("是否新词")
    row.createCell(4).setCellValue("学部下总频次（标题+摘要+关键词）")
    row.createCell(5).setCellValue("研究方向下总频次（标题+摘要+关键词）")
    row.createCell(6).setCellValue("学部下填作申请书关键词频次")
    row.createCell(7).setCellValue("研究方向下填作申请书关键词频次")
  }

}
