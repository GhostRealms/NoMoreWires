package me.jraynor.api.operation.insert;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.operation.OperationServer;
import me.jraynor.api.util.NodeType;

/**
 * This operation will allow for us to be able to extract from a {@link me.jraynor.api.link.ILink}
 */
@Log4j2
public class InsertOperationServer extends OperationServer {
    @Getter private NodeType nodeType = NodeType.INSERT_OP;

    /**
     * This will extract from the linked from node.
     * it will be called once per tick
     */
    public void execute() {
        if (getFrom().isPresent() && getTo().isPresent()) {
            System.out.println("TODO insert!");
        }
    }

}
