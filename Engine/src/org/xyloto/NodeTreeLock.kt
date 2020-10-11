package org.xyloto

internal object NodeTreeLock {

	const val LOCK_NOTHING: Byte = 0
	const val LOCK_ROOT: Byte = 1
	const val LOCK_PARENT: Byte = 2
	const val LOCK_DESTROY: Byte = 3

	private var lock: Byte = LOCK_NOTHING

	fun check(lock: Byte) {
		check(this.lock == LOCK_NOTHING) {
			when (lock) {
				LOCK_ROOT -> "Can't change the root"
				LOCK_PARENT -> "Can't change the parent"
				LOCK_DESTROY -> "Can't destroy a the node"
				else -> "Can't modify the node tree"
			} +
			when (this.lock) {
				LOCK_ROOT -> " while the root is being set"
				LOCK_PARENT -> " while a parent is being set"
				LOCK_DESTROY -> " while a node is being destroyed"
				else -> ""
			}
		}
	}

	inline fun use(lock: Byte, block: () -> Unit) {
		this.lock = lock
		block()
		this.lock = LOCK_NOTHING
	}
}