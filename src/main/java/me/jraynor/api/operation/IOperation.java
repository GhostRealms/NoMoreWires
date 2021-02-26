package me.jraynor.api.operation;

import me.jraynor.api.node.INode;

/**
 * This is the base operation. It allows for
 */
public interface IOperation extends INode {
    default void execute() {}
}
