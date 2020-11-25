package org.xyloto.collections

class ArrayWrapperList<T>(
	private val array: Array<T>
) : List<T> {

	override val size get() = array.size

	override fun isEmpty() = array.isEmpty()
	override fun contains(element: T) = array.contains(element)
	override fun containsAll(elements: Collection<T>): Boolean {
		for (element in elements) if (!array.contains(element)) return false
		return true
	}

	override fun get(index: Int) = array[index]
	override fun indexOf(element: T) = array.indexOf(element)
	override fun lastIndexOf(element: T) = array.lastIndexOf(element)

	override fun iterator() = array.iterator()
	override fun listIterator() = listIterator(0)
	override fun listIterator(index: Int): ListIterator<T> {
		if (index < 0 || index > size) throw IndexOutOfBoundsException(index.toString())
		return object : ListIterator<T> {
			private var i = index
			override fun hasNext() = i < size
			override fun hasPrevious() = i - 1 >= 0
			override fun next() = get(i++)
			override fun nextIndex() = i
			override fun previous() = get(--i)
			override fun previousIndex() = i - 1
		}
	}

	override fun subList(fromIndex: Int, toIndex: Int) = TODO("Implement sublist")
}