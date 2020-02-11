package mass.rdp.etl.graph

import helloscala.common.IntStatus
import helloscala.common.exception.HSException

class EtlGraphException(message: String) extends HSException(IntStatus.BAD_REQUEST, message) {}
