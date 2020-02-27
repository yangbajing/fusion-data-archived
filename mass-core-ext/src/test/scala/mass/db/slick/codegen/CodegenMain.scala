package mass.db.slick.codegen

import fusion.core.FusionApplication
import fusion.inject.guice.GuiceApplication
import fusion.jdbc.FusionJdbc
import mass.db.slick.PgProfile
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.sql.SqlProfile.ColumnOption

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

object CodegenMain extends App {
  val application = FusionApplication.start().asInstanceOf[GuiceApplication]
  val pgProfile: PgProfile = application.instance[PgProfile]
  implicit val ec = application.typedSystem.executionContext

  try {
    codegenWrite(
      "mass.db.slick.PgProfile",
      "mass-core-ext/src/test/scala/",
      "mass.job.db.model",
      "QrtzModels",
      "QrtzModels.scala")
  } catch {
    case NonFatal(e) =>
      e.printStackTrace()
  } finally {
    application.classicSystem.terminate()
  }

  private def codegenWrite(profile: String, folder: String, pkg: String, container: String, fileName: String): Unit = {
    val db = pgProfile.api.databaseForDataSource(FusionJdbc(application.classicSystem).component)

    val modelAction = pgProfile.createModel(Some(MTable.getTables(None, None, Some("qrtz_%"), Some(Seq("TABLE")))))
    val modelFuture = db.run(modelAction)

    val codegenFuture = modelFuture.map { tableModel =>
      tableModel.tables.foreach(println)

      new SourceCodeGenerator(tableModel) {
        override val ddlEnabled: Boolean = true

        override def tableName: String => String = _.toCamelCase + "Model"

        override def entityName: String => String = _.toCamelCase

        override def Table = new Table(_) { table =>
          override def Column = new Column(_) { column =>
            override def rawType: String = {
              column.model.options
                .find(_.isInstanceOf[ColumnOption.SqlType])
                .flatMap { tpe =>
                  tpe.asInstanceOf[ColumnOption.SqlType].typeName match {
                    case "hstore"                                      => Option("Map[String, String]")
                    case "_text" | "text[]" | "_varchar" | "varchar[]" => Option("List[String]")
                    case "geometry"                                    => Option("com.vividsolutions.jts.geom.Geometry")
                    case "_int8" | "int8[]"                            => Option("List[Long]")
                    case "_int4" | "int4[]"                            => Option("List[Int]")
                    case "_int2" | "int2[]"                            => Option("List[Short]")
                    case "timestamptz"                                 => Option("java.time.OffsetDateTime")
                    case "int4" | "int" =>
                      if (column.model.table.table == "qrtz_trigger_log" && column.model.name == "completion_status") {
                        Option("mass.model.job.RunStatus")
                      } else None
                    case _ => None
                  }
                }
                .getOrElse {
                  column.model.tpe match {
                    case "java.sql.Date"      => "java.time.LocalDate"
                    case "java.sql.Time"      => "java.time.LocalTime"
                    case "java.sql.Timestamp" => "java.time.LocalDateTime"
                    case _                    => super.rawType
                  }
                }
            }
          }
        }

        override def packageCode(
            profile: String,
            pkg: String,
            container: String,
            parentType: Option[String]): String = {
          s"""
package $pkg
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object $container extends $container {
  val profile = $profile
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait $container${parentType.map(t => s" extends $t").getOrElse("")} {
  val profile: $profile
  import profile.api._
  ${indent(code)}
}
""".trim()
        }

        override def packageContainerCode(profile: String, pkg: String, container: String): String = {
          val mixinCode =
            codePerTable.keys.map(tableName => s"${handleQuotedNamed(tableName)}").mkString("extends ", " with ", "")
          s"""
package $pkg
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object $container extends $container {
  val profile = $profile
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.)
    Each generated XXXXTable trait is mixed in this trait hence allowing access to all the TableQuery lazy vals.
  */
trait $container${parentType.map(t => s" extends $t").getOrElse("")} $mixinCode {
  val profile: $profile
  import profile.api._
  ${indent(codeForContainer)}

}
      """.trim()
        }

        def handleQuotedNamed(tableName: String): String =
          if (tableName.endsWith("`")) s"${tableName.init}Table`" else s"${tableName}Table"
      }
    }

    val codegen = Await.result(codegenFuture, Duration.Inf)
    codegen.writeToMultipleFiles(profile, folder, pkg, container)
  }
}
