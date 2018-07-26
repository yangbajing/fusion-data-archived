/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.component.spi

/**
  * 数据组件：数据加载、数据存储
  */
trait DataComponent extends BaseComponent {
  def preStart(): Unit

  def authorization(): Boolean

  def authroizationAfter(): Unit

  def run(): CollectResult

  def postStop(): Unit
}
