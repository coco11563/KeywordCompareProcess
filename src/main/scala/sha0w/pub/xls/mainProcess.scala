package sha0w.pub.xls

import java.io.{File, FileOutputStream}
import java.util.Properties

import com.mysql.cj.conf.HostInfo
import com.mysql.cj.jdbc.{ConnectionImpl, JdbcConnection}
import org.apache.poi.ss.usermodel.{Row, Workbook, WorkbookFactory}

object mainProcess {
  Class.forName("com.mysql.cj.jdbc.Driver")
  val property = new Properties
  property.put("user","root")
  property.put("password", "Bigdata,1234")
  property.put("driver","com.mysql.cj.jdbc.Driver")
  val conn: ConnectionImpl = new ConnectionImpl(new HostInfo(null, "192.168.3.131",3306,"root", "Bigdata,1234"))
  val activeConn: JdbcConnection = conn.getActiveMySQLConnection

  def main(args: Array[String]): Unit = {
    val codeArray = Array("H")
    val yearArray = Array("2017","2018","2019")
    for (code <- codeArray) {
      for (year <- yearArray) {
        processApplication(code, year, s"target/OPT/${year}/",s"申请书数据_${code}.xlsx")
      }
    }

  }

  def processApplication(code : String, year : String, outPath : String, fileName : String) : Unit = {
    val sql = "select * from NSFC_KEYWOR_DB." + year + "_APPLICATION_NEW where APPLYID like '"+ code +"%';"
    println(sql)
    val ps = activeConn.clientPrepareStatement(sql)
    val rs = ps.executeQuery()
    val wb : Workbook = WorkbookFactory.create(true)
    val sheet = wb.createSheet(s"${year}面青地项目")
    var index = 1
    val schemaRow = sheet.createRow(0)
    initApplicationSchema(schemaRow)
    val optFile = new File(outPath)
    if (!optFile.exists()) {
      optFile.mkdirs()
    }
    val file = new File(outPath + fileName)
    file.delete()
    val FOS = new FileOutputStream(file)
    var pushAmount = 10
    while(rs.next()) {
      val title = rs.getString("TITLE_ZH")
      val applyid = rs.getString("APPLYID")
      val researchField = rs.getString("RESEARCH_FIELD")
      val keyword = rs.getString("KEYWORD_ZH")
      val abs = rs.getString("ABSTRACT_ZH")
      val row = sheet.createRow(index)
      row.createCell(0).setCellValue(title)
      row.createCell(1).setCellValue(applyid)
      row.createCell(2).setCellValue(researchField)
      row.createCell(3).setCellValue(keyword)
      row.createCell(4).setCellValue(abs)
      index += 1
      pushAmount += 1
      if (pushAmount % 10000 == 0) {
        wb.write(FOS)
        FOS.flush()
      }
    }
    FOS.flush()
    FOS.close()
    wb.close()
    rs.close()
    ps.close()
  }

  def initApplicationSchema (row : Row) : Unit = {
    row.createCell(0).setCellValue("项目的中文名称")
    row.createCell(1).setCellValue("申请代码1")
    row.createCell(2).setCellValue("研究方向")
    row.createCell(3).setCellValue("中文关键词")
    row.createCell(4).setCellValue("中文摘要")
  }
}
