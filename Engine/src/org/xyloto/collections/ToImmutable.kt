package org.xyloto.collections

import java.util.*

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

fun <E> MutableCollection<E>.toImmutable(): Collection<E> {
	val mutableCollection = this
	return object : Collection<E> {

		override val size get() = mutableCollection.size

		override fun isEmpty() = mutableCollection.isEmpty()
		override fun contains(element: E) = mutableCollection.contains(element)
		override fun containsAll(elements: Collection<E>) = mutableCollection.containsAll(elements)

		override fun iterator() = mutableCollection.iterator().toImmutable()
	}
}

fun <E> MutableList<E>.toImmutable(): List<E> {
	val mutableList = this
	return object : List<E> {

		override val size get() = mutableList.size

		override fun isEmpty() = mutableList.isEmpty()
		override fun contains(element: E) = mutableList.contains(element)
		override fun containsAll(elements: Collection<E>) = mutableList.containsAll(elements)

		override fun get(index: Int) = mutableList[index]
		override fun indexOf(element: E) = mutableList.indexOf(element)
		override fun lastIndexOf(element: E) = mutableList.lastIndexOf(element)

		override fun iterator() = mutableList.iterator().toImmutable()
		override fun listIterator() = mutableList.listIterator().toImmutable()
		override fun listIterator(index: Int) = mutableList.listIterator(index).toImmutable()

		override fun subList(fromIndex: Int, toIndex: Int) = mutableList.subList(fromIndex, toIndex).toImmutable()
	}
}

fun <E> MutableSet<E>.toImmutable(): Set<E> {
	val mutableSet = this
	return object : Set<E> {

		override val size get() = mutableSet.size

		override fun isEmpty() = mutableSet.isEmpty()
		override fun contains(element: E) = mutableSet.contains(element)
		override fun containsAll(elements: Collection<E>) = mutableSet.containsAll(elements)

		override fun iterator() = mutableSet.iterator().toImmutable()
	}
}

fun <K, V> MutableMap<K, V>.toImmutable(): Map<K, V> {
	val mutableMap = this
	return object : Map<K, V> {

		override val size get() = mutableMap.size

		override val entries = object : Set<Map.Entry<K, V>> {

			override val size get() = mutableMap.entries.size

			override fun isEmpty() = mutableMap.entries.isEmpty()

			override fun contains(element: Map.Entry<K, V>): Boolean {
				return if (element is Entry) {
					mutableMap.entries.contains(element.entry)
				} else {
					false
				}
			}

			override fun containsAll(elements: Collection<Map.Entry<K, V>>): Boolean {
				elements.forEach {
					if (it !is Entry) return false
					if (!mutableMap.entries.contains(it.entry)) return false
				}
				return true
			}

			override fun iterator() = object : Iterator<Map.Entry<K, V>> {
				val iterator = mutableMap.iterator()
				override fun hasNext() = iterator.hasNext()
				override fun next() = Entry(iterator.next())
			}

			private inner class Entry(val entry: Map.Entry<K, V>) : Map.Entry<K, V> {
				override val key = entry.key
				override val value = entry.value
			}
		}

		override val keys = object : Set<K> {

			override val size get() = mutableMap.keys.size

			override fun isEmpty() = mutableMap.keys.isEmpty()
			override fun contains(element: K) = mutableMap.keys.contains(element)
			override fun containsAll(elements: Collection<K>) = mutableMap.keys.containsAll(elements)

			override fun iterator() = mutableMap.keys.iterator().toImmutable()
		}

		override val values = object : Collection<V> {

			override val size get() = mutableMap.values.size

			override fun isEmpty() = mutableMap.values.isEmpty()
			override fun contains(element: V) = mutableMap.values.contains(element)
			override fun containsAll(elements: Collection<V>) = mutableMap.values.containsAll(elements)

			override fun iterator() = mutableMap.values.iterator().toImmutable()
		}

		override fun isEmpty() = mutableMap.isEmpty()
		override fun containsKey(key: K) = mutableMap.containsKey(key)
		override fun containsValue(value: V) = mutableMap.containsValue(value)

		override fun get(key: K) = mutableMap[key]
	}
}
