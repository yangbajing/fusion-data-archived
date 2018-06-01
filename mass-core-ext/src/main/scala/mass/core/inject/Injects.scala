/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

//package mass.core.inject
//
//import javax.inject.Named
//
//import com.google.inject.{Guice, Injector, Key, Module}
//import helloscala.common.util.StringUtils
//import mass.core.server.MassServer
//
//import scala.reflect.ClassTag
//
//object Injects {
//  private[this] val MODULES_PATH = "mass.modules"
//
//  lazy val injector: Injector = Guice.createInjector(generateModules(): _*)
//
//  private def generateModules() =
//    MassServer.configuration.get[Seq[String]](MODULES_PATH)
//      .filter(s => StringUtils.isNoneBlank(s))
//      .map(t => Class.forName(t).newInstance().asInstanceOf[Module])
//
//  /**
//   * 根据类型获类实例
//   * @param ev 类型
//   * @tparam T ClassTag[T]
//   * @return
//   */
//  def instance[T](implicit ev: ClassTag[T]): T = injector.getInstance(ev.runtimeClass).asInstanceOf[T]
//
//  /**
//   * 根据类型及注解获取类实例
//   * @param annotation 注解
//   * @param ev 类实例
//   * @tparam T ClassTag[T]
//   * @return
//   */
//  def instance[T](annotation: Named)(implicit ev: ClassTag[T]): T =
//    injector.getInstance(Key.get(ev.runtimeClass, annotation)).asInstanceOf[T]
//
//  /**
//   * Java Api
//   * @param c 类实例
//   * @return
//   */
//  def getInstance[T](c: Class[T]): T = injector.getInstance(c).asInstanceOf[T]
//
//  /**
//   * Java Api
//   * @param key Guice Key
//   * @return
//   */
//  def getInstance[T](key: Key[T]): T = injector.getInstance(key).asInstanceOf[T]
//
//  /**
//   * Java Api
//   * @param c 类实例
//   * @param a 命名注解
//   * @return
//   */
//  def getInstance[T](c: Class[T], a: Named): T = injector.getInstance(Key.get(c, a)).asInstanceOf[T]
//}
