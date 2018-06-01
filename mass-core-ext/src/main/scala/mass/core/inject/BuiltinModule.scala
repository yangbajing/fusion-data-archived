/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

//package mass.core.inject
//
//import javax.inject.{Provider, Singleton}
//
//import akka.actor.ActorSystem
//import akka.stream.{ActorMaterializer, Materializer}
//import com.typesafe.config.Config
//import helloscala.common.{AppLifecycle, Configuration}
//import mass.core.server.{DefaultAppLifecycle, MassAbstractModule, MassServer}
//
//import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
//
//@Singleton
//class ActorSystemProvider extends Provider[ActorSystem] {
//  override def get(): ActorSystem = MassServer.actorSystem
//}
//
//@Singleton
//class ActorMaterializerProvider() extends Provider[ActorMaterializer] {
//  override def get(): ActorMaterializer = MassServer.actorMaterializer
//}
//
//@Singleton
//class ExecutionContextExecutorProvider() extends Provider[ExecutionContextExecutor] {
//  override def get(): ExecutionContextExecutor = MassServer.actorSystem.dispatcher
//}
//
//@Singleton
//class ConfigurationProvider() extends Provider[Configuration] {
//  override def get(): Configuration = MassServer.configuration
//}
//
//@Singleton
//class ConfigProvider() extends Provider[Config] {
//  override def get(): Config = MassServer.config
//}
//
//class BuiltinModule extends MassAbstractModule {
//
//  override def setup(): Unit = {
//    bind(classOf[ActorSystem]).toProvider(classOf[ActorSystemProvider])
//
//    bind(classOf[Config]).toProvider(classOf[ConfigProvider])
//    bind(classOf[Configuration]).toProvider(classOf[ConfigurationProvider])
//
//    bind(classOf[AppLifecycle]).to(classOf[DefaultAppLifecycle])
//    bind(classOf[ActorMaterializer]).toProvider(classOf[ActorMaterializerProvider])
//    bind(classOf[Materializer]).to(classOf[ActorMaterializer])
//
//    bind(classOf[ExecutionContextExecutor]).toProvider(classOf[ExecutionContextExecutorProvider])
//    bind(classOf[ExecutionContext]).to(classOf[ExecutionContextExecutor])
//  }
//
//}
