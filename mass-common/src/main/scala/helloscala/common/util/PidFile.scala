/*
 * Copyright 2018 羊八井(yangbajing)（杨景）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helloscala.common.util

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}

object PidFile {

  def apply(pid: Long): PidFile = new PidFile(pid)

}

class PidFile(val pid: Long) {

  /**
   * Creates a new PidFile and writes the current process ID into the provided path
   *
   * @param path         the path to the pid file. The file is newly created or truncated if it already exists
   * @param deleteOnExit if <code>true</code> the pid file is deleted with best effort on system exit
   */
  @throws[IOException]("if an IOException occurs")
  def create(path: Path, deleteOnExit: Boolean): PidFile =
    create(path, deleteOnExit, Utils.getPid)

  @throws[IOException]
  def create(path: Path, deleteOnExit: Boolean, pid: Long): PidFile = {
    val parent = path.getParent
    if (parent != null) {
      if (Files.exists(parent) && !Files.isDirectory(parent))
        throw new IllegalArgumentException(
          parent + " exists but is not a directory"
        )

      if (!Files.exists(parent)) {
        // only do this if it doesn't exists we get a better exception further down
        // if there are security issues etc. this also doesn't work if the parent exists
        // and is a soft-link like on many linux systems /var/run can be a link and that should
        // not prevent us from writing the PID
        Files.createDirectories(parent)
      }
    }

    if (Files.exists(path) && !Files.isRegularFile(path))
      throw new IllegalArgumentException(
        path + " exists but is not a regular file"
      )

    val stream = Files.newOutputStream(
      path,
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    )
    try {
      stream.write(pid.toString.getBytes(StandardCharsets.UTF_8))
    } finally {
      if (stream != null) stream.close()
    }

    if (deleteOnExit) {
      addShutdownHook(path)
    }
    new PidFile(pid)
  }

  private def addShutdownHook(path: Path): Unit =
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit =
        try {
          Files.deleteIfExists(path)
        } catch {
          case e: IOException =>
            throw new IllegalArgumentException(
              "Failed to delete pid file " + path,
              e
            )
        }
    })

}
