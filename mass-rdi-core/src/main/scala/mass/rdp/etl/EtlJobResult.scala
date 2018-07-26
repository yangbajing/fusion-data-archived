package mass.rdp.etl

import mass.core.job.JobResult

case class EtlJobResult(result: EtlResult) extends JobResult
