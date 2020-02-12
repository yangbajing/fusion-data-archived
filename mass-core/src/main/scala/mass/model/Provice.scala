package mass.model

case class County(count: String, postCode: String)
case class Province(provice: String, couties: Seq[County])
