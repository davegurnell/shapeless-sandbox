# Topics to cover:

Test data:

~~~ scala
case class Person(name: String, age: Int)
case class Talk(name: String, speaker: Person)

val dave = Person("Dave", 37)
val talk = Talk("Shapeless Workshop", dave)

sealed trait Tree
final case class Branch(items: List[Tree]) extends Tree
final case class Leaf(value: String) extends Tree

trait TreeWriter[A] {
  def write(value: A): Tree
}

object TreeWriter {
  def write[A](value: A)(implicit writer: TreeWriter[A]): Tree =
    writer.write(value)
}

implicit val personTreeWriter = new TreeWriter[Person] {
  def write(p: Person) = Branch(List(
    Leaf("Person"),
    Leaf(p.name),
    Leaf(p.age.toString)
  ))
}

implicit val talkTreeWriter = new TreeWriter[Talk] {
  def write(t: Talk) = Branch(List(
    Leaf("Talk"),
    TreeWriter.write(t.name)
  ))
}
~~~

HLists:

~~~ scala
val hlist1 = 12345 :: "hello" :: HNil
val hlist2 = "goodbye" :: 54321 :: HNil
~~~

Coproducts:

~~~ scala
val coprod1: Int :+: String :+: CNil = Inl(12345)
val coprod2: Int :+: String :+: CNil = Inr(Inl("hello"))
~~~

Generic:

~~~ scala
case class Person(name: String, age: Int)

val dave = Person("Dave", 37)

val gen   = implicitly[Generic[Person]]
val hlist = gen.to(dave)
val dave2 = gen.from(hlist)
~~~

LabelledGeneric:

(If we have time...)
