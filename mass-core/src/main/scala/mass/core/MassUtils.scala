package mass.core

object MassUtils {
  def userDir: String = sys.props.getOrElse("user.dir", "")
}
