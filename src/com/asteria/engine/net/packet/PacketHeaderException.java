package com.asteria.engine.net.packet;

/**
 * An exception thrown when a {@link PacketDecoder} has no
 * {@link PacketOpcodeHeader} annotation.
 * 
 * @author lare96
 */
public class PacketHeaderException extends RuntimeException {

    /**
     * Create a new {@link PacketHeaderException}.
     * 
     * @param decoder
     *            the decoder throwing this exception.
     */
    public PacketHeaderException(PacketDecoder decoder) {
        super("No PacketOpcodeHeader detected for this packet: "
                + decoder.getClass().getSimpleName());
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = 7194056704861664451L;
}
