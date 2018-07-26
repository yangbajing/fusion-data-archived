package mass.rdp.etl.graph

import helloscala.common.ErrCodes
import helloscala.common.exception.HSException

class EtlGraphException(message: String)
    extends HSException(ErrCodes.BAD_REQUEST, message)
