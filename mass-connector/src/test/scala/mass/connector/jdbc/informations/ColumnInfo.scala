package mass.connector.jdbc.informations

trait ColumnInfo extends BaseInfo {
  def columnName: String
  def ordinalPosition: Int
  def columnDefault: Option[String]
  def isNullable: Boolean
  def dataType: String
  def characterMaximumLength: Option[Int]
  def characterOctetLength: Option[Int]
  def numericPrecision: Option[Int]
  def numericPrecisionRadix: Option[Int]
  def numericScale: Option[Int]
  def datetimePrecision: Option[Int]
  def isUpdatable: Option[Boolean]
}
