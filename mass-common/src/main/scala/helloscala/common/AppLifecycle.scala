/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common

trait AppLifecycle {
  def addStopHook(hook: () => Unit): Unit = addStopHook(new Runnable {
    override def run(): Unit = hook()
  })

  def addStopHook(hook: Runnable): Unit
}
