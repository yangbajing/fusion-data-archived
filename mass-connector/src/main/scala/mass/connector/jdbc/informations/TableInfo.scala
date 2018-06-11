package mass.connector.jdbc.informations

trait TableInfo extends BaseInfo {
  def tableType: String

  def isInsertable: Boolean
}
