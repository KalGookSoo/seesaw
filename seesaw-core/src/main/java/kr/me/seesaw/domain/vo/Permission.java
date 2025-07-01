package kr.me.seesaw.domain.vo;

import java.io.Serializable;

/**
 * Represents a permission granted to a <tt>Sid</tt> for a given domain object.
 *
 * @author Ben Alex
 */
public interface Permission extends Serializable {

    char RESERVED_ON = '~';

    char RESERVED_OFF = '.';

    String THIRTY_TWO_RESERVED_OFF = "................................";

    /**
     * Returns the bits that represents the permission.
     * @return the bits that represent the permission
     */
    int getMask();

    /**
     * Returns a 32-character long bit pattern <code>String</code> representing this
     * permission.
     * <p>
     * Implementations are free to format the pattern as they see fit, although under no
     * circumstances may {@link #RESERVED_OFF} or {@link #RESERVED_ON} be used within the
     * pattern. An exemption is in the case of {@link #RESERVED_OFF} which is used to
     * denote a bit that is off (clear). Implementations may also elect to use
     * {@link #RESERVED_ON} internally for computation purposes, although this method may
     * not return any <code>String</code> containing {@link #RESERVED_ON}.
     * <p>
     * The returned String must be 32 characters in length.
     * <p>
     * This method is only used for user interface and logging purposes. It is not used in
     * any permission calculations. Therefore, duplication of characters within the output
     * is permitted.
     * @return a 32-character bit pattern
     */
    String getPattern();

}
