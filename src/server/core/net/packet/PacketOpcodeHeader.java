package server.core.net.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation header that every {@link PacketDecoder} must have.
 * 
 * @author lare96
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketOpcodeHeader {

    /**
     * The opcodes that a packet decoder is able to decode.
     * 
     * @return all of the opcodes.
     */
    int[] value();
}
