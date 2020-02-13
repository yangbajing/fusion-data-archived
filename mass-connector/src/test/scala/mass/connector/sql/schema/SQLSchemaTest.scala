package mass.connector.sql.schema

import fusion.test.FusionWordSpecLike
import mass.connector.sql.TestSchema
import org.scalatest.BeforeAndAfterAll

import scala.collection.immutable

class SQLSchemaTest extends FusionWordSpecLike with BeforeAndAfterAll {
  override protected def afterAll(): Unit = {
    TestSchema.postgres.close()
    super.afterAll()
  }

  "schema-postgres" should {
    val schema = PostgresSchema(TestSchema.postgres)
    var tables = immutable.Seq.empty[TableInfo]

    "listTable" in {
      tables = schema.listTable("public")
      tables should not be empty
      val table = tables.head
      table.schemaName shouldBe "public"
      tables.foreach(println)
    }

    "listColumn" in {
      val columns =
        schema.listColumn(tables.head.tableName, tables.head.schemaName)
      columns should not be empty
      columns.foreach(println)
    }
  }
}
