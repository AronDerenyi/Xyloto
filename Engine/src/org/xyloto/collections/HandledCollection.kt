package org.xyloto.collections

import kotlin.NoSuchElementException

// TODO: Faster forEach implementation
class HandledCollection<T> : MutableCollection<T> {

	var first: Handle? = null
		private set

	var last: Handle? = null
		private set

	private var sizeInternal = 0
	override val size: Int get() = sizeInternal

	override fun isEmpty() = sizeInternal == 0

	override fun contains(element: T): Boolean {
		var handle = first
		while (handle != null) {
			if (handle.value == element) return true
			handle = handle.next
		}
		return false
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		elements.forEach { if (!contains(it)) return false }
		return true
	}

	override fun add(element: T): Boolean {
		Handle(element)
		return true
	}

	override fun addAll(elements: Collection<T>): Boolean {
		elements.forEach { Handle(it) }
		return true
	}

	override fun clear() {
		var handle = first
		while (handle != null) {
			val next = handle.next
			handle.remove()
			handle = next
		}
	}

	override fun remove(element: T): Boolean {
		var handle = first
		while (handle != null) {
			if (handle.value == element) {
				handle.remove()
				return true
			}
			handle = handle.next
		}
		return false
	}

	override fun removeAll(elements: Collection<T>): Boolean {
		var result = false
		elements.forEach { result = remove(it) || result }
		return result
	}

	override fun retainAll(elements: Collection<T>): Boolean {
		var result = false
		var handle = first
		while (handle != null) {
			val next = handle.next
			if (!elements.contains(handle.value)) {
				handle.remove()
				result = true
			}
			handle = next
		}
		return result
	}

	override fun iterator() = Iterator()

	inner class Iterator : MutableIterator<T> {

		private var next: Handle? = first

		override fun hasNext() = next != null

		override fun next(): T {
			val next = next ?: throw NoSuchElementException()
			this.next = next.next
			return next.value
		}

		override fun remove() {
			val next = next
			val previous = (if (next == null) last else next.previous) ?: throw NoSuchElementException()
			previous.remove()
		}
	}

	inner class Handle(val value: T) {

		val collection get() = this@HandledCollection

		var previous: Handle? = last
			private set

		var next: Handle? = null
			private set

		private var removed = false

		init {
			previous?.next = this
			last = this

			++sizeInternal
		}

		fun remove() {
			if (removed) return
			removed = true

			val previous = previous
			val next = next

			if (previous == null) {
				if (next == null) {
					first = null
					last = null
				} else {
					first = next
					next.previous = null
				}
			} else {
				if (next == null) {
					previous.next = null
					last = previous
				} else {
					previous.next = next
					next.previous = previous
				}
			}

			--sizeInternal
		}
	}
}

fun <T> HandledCollection<T>.forEach(action: (T) -> Unit) {

}
