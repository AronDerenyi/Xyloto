package org.xyloto.builder

import org.xyloto.Attribute
import org.xyloto.Node

inline class NodeBuilder(val node: Node) {

	constructor(vararg attributes: Attribute) : this(Node(*attributes))
}

inline fun build(vararg attributes: Attribute, init: NodeBuilder.() -> Unit = {}): Node {
	val builder = NodeBuilder(*attributes)
	builder.init()
	return builder.node
}

inline fun NodeBuilder.node(vararg attributes: Attribute, init: NodeBuilder.() -> Unit = {}): Node {
	val node = build(*attributes) { init() }
	node.parent = this.node
	return node
}
