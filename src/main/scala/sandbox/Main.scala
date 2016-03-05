package sandbox

import shapeless._

object Main extends App {
  case class Person(name: String, age: Int)
  case class Talk(name: String, speaker: Person)

  val dave = Person("Dave", 37)
  val talk = Talk("Shapeless Workshop", dave)

  sealed trait Tree
  final case class Branch(a: Tree, b: Tree) extends Tree
  final case class Leaf(value: String) extends Tree

  object Tree {
    val empty: Tree = Leaf("nothing-to-see-here")
  }

  trait TreeWriter[A] {
    def write(value: A): Tree
  }

  object TreeWriter {
    def write[A](value: A)(implicit writer: TreeWriter[A]): Tree =
      writer.write(value)
  }

  implicit val stringTreeWriter: TreeWriter[String] =
    new TreeWriter[String] {
      def write(value: String): Tree =
        Leaf(value)
    }

  implicit val intTreeWriter: TreeWriter[Int] =
    new TreeWriter[Int] {
      def write(value: Int): Tree =
        Leaf(value.toString)
    }

  implicit val hnilTreeWriter: TreeWriter[HNil] =
    new TreeWriter[HNil] {
      def write(value: HNil): Tree = Tree.empty
    }

  implicit def hconsTreeWriter[H, T <: HList](
    implicit
    headWriter: TreeWriter[H],
    tailWriter: Lazy[TreeWriter[T]]
  ): TreeWriter[H :: T] = new TreeWriter[H :: T] {
    def write(value: H :: T): Tree = {
      val head :: tail = value
      val headTree = headWriter.write(head)
      val tailTree = tailWriter.value.write(tail)
      Branch(headTree, tailTree)
    }
  }

  implicit def genericTreeWriter[A, L <: HList](
    implicit
    gen: Generic.Aux[A, L],
    listWriter: TreeWriter[L]
  ): TreeWriter[A] = new TreeWriter[A] {
    def write(value: A): Tree =
      listWriter.write(gen.to(value))
  }

  val hlist1 = "hello2" :: 123 :: "hello2" :: "hello2" :: 123 :: HNil
  val hlist2 = 234 :: "hello" :: hlist1 :: hlist1 :: HNil

  println(TreeWriter.write(talk))
}
