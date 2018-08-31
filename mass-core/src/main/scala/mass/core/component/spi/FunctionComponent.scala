/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.component.spi

/**
 * 转换组件
 * @tparam T
 */
trait FunctionComponent[T] extends BaseComponent {
  def preStart(): Unit

  def process(data: T): T

  def postStop(): Unit
}
