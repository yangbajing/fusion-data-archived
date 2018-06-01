/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.component.spi

trait BaseComponent {
  def preStart(): Unit

  def postStop(): Unit
}
