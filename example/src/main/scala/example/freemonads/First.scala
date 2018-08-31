package example.freemonads

import cats.data.State
import cats.free.Free
import cats.free.Free.liftF
import cats.{~>, Id}

import scala.collection.mutable

sealed trait KVStoreA[A]
case class Put[T](key: String, value: T) extends KVStoreA[Unit]
case class Get[T](key: String) extends KVStoreA[Option[T]]
case class Delete(key: String) extends KVStoreA[Unit]

object First {
  type KVStore[A] = Free[KVStoreA, A]

  def put[T](key: String, value: T): KVStore[Unit] = liftF[KVStoreA, Unit](Put[T](key, value))

  def get[T](key: String): KVStore[Option[T]] = liftF[KVStoreA, Option[T]](Get[T](key))

  def delete(key: String): KVStore[Unit] = liftF(Delete(key))

  def update[T](key: String, f: T => T): KVStore[Unit] =
    for {
      maybe <- get[T](key)
      _ <- maybe.map(v => put[T](key, f(v))).getOrElse(Free.pure(()))
    } yield ()

  def program: KVStore[Option[Int]] =
    for {
      _ <- put("wild-cats", 2)
      _ <- update[Int]("wild-cats", state => state + 12)
      _ <- put("tame-cats", 5)
      n <- get[Int]("wild-cats")
      _ <- delete("tame-cats")
    } yield n

  val kvs = mutable.Map.empty[String, Any]

  val impureCompile: KVStoreA ~> Id = new (KVStoreA ~> Id) {
    override def apply[A](fa: KVStoreA[A]): Id[A] = fa match {
      case Put(key, value) =>
        println(s"put($key, $value)")
        kvs(key) = value
        ()
      case Get(key) =>
        println(s"get($key)")
        kvs.get(key).map(_.asInstanceOf[A])
      case Delete(key) =>
        println(s"delete($key)")
        kvs.remove(key)
        ()
    }
  }

  type KVStoreState[A] = State[mutable.Map[String, Any], A]

  val pureCompiler: KVStoreA ~> KVStoreState = new (KVStoreA ~> KVStoreState) {
    override def apply[A](fa: KVStoreA[A]): KVStoreState[A] = fa match {
      case Put(key, value) =>
        State.modify { m =>
          m.update(key, value)
          m
        }
      case Get(key)    => State.inspect(_.get(key).map(_.asInstanceOf[A]))
      case Delete(key) => State.modify(_ - key)
    }
  }

  val pureCompilers: KVStoreA ~> KVStoreState = new (KVStoreA ~> KVStoreState) {
    override def apply[A](fa: KVStoreA[A]): KVStoreState[A] = fa match {
      case Put(key, value) => State.modify(_.updated(key, value))
      case Get(key)        => State.inspect(_.get(key).map(_.asInstanceOf[A]))
      case Delete(key)     => State.modify(_ - key)
    }
  }

  def main(args: Array[String]): Unit = {
    val result: Option[Int] = program.foldMap(impureCompile)
    println(s"result: $result")

    val kvs2 = mutable.Map[String, Any]("ddd" -> 1, "ccc" -> 2)
    kvs2.update("xxx", 3)
    val result2 = program.foldMap(pureCompiler).run(kvs2).value
    println(s"result2: $result2")

    println("-------------------")
    kvs.foreach(println)
    println("-------------------")
    kvs2.foreach(println)
  }

}
