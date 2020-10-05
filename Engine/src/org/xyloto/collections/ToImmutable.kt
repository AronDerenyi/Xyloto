package org.xyloto.collections

fun <T> MutableIterator<T>.toImmutable(): Iterator<T> {
	val mutableIterator = this
	return object : Iterator<T> {
		override fun hasNext() = mutableIterator.hasNext()
		override fun next() = mutableIterator.next()
	}
}

fun <T> MutableListIterator<T>.toImmutable(): ListIterator<T> {
	val mutableListIterator = this
	return object : ListIterator<T> {
		override fun hasNext() = mutableListIterator.hasNext()
		override fun hasPrevious() = mutableListIterator.hasPrevious()
		override fun next() = mutableListIterator.next()
		override fun nextIndex() = mutableListIterator.nextIndex()
		override fun previous() = mutableListIterator.previous()
		override fun previousIndex() = mutableListIterator.previousIndex()
	}
}

fun <T> MutableCollection<T>.toImmutable(): Collection<T> {
	val mutableCollection = this
	return object : Collection<T> {

		override val size get() = mutableCollection.size

		override fun isEmpty() = mutableCollection.isEmpty()
		override fun contains(element: T) = mutableCollection.contains(element)
		override fun containsAll(elements: Collection<T>) = mutableCollection.containsAll(elements)

		override fun iterator() = mutableCollection.iterator().toImmutable()
	}
}

fun <T> MutableList<T>.toImmutable(): List<T> {
	val mutableList = this
	return object : List<T> {

		override val size get() = mutableList.size

		override fun isEmpty() = mutableList.isEmpty()
		override fun contains(element: T) = mutableList.contains(element)
		override fun containsAll(elements: Collection<T>) = mutableList.containsAll(elements)

		override fun get(index: Int) = mutableList[index]
		override fun indexOf(element: T) = mutableList.indexOf(element)
		override fun lastIndexOf(element: T) = mutableList.lastIndexOf(element)

		override fun iterator() = mutableList.iterator().toImmutable()
		override fun listIterator() = mutableList.listIterator().toImmutable()
		override fun listIterator(index: Int) = mutableList.listIterator(index).toImmutable()

		override fun subList(fromIndex: Int, toIndex: Int) = mutableList.subList(fromIndex, toIndex).toImmutable()
	}
}
