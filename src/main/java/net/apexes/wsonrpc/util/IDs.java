package net.apexes.wsonrpc.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class IDs {
	
	private IDs() {}
		
	/**
     * 获取 UUID 的字节值。此方法与<code>{@link #toUUID(byte[])}</code>互逆
     *
     * @param uuid
     * @return
     */
    public static byte[] toByteArray(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

    /**
     * 将字节值转成UUID。此方法与 <code>{@link #toByteArray(UUID)}</code> 互逆
     *
     * @param byteArray
     * @return
     */
    public static UUID toUUID(byte[] byteArray) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (byteArray[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (byteArray[i] & 0xff);
        }
        UUID result = new UUID(msb, lsb);
        return result;
    }
    
    /**
     * 将UUID转成不超过22字节的字符串
     * @param uuid
     * @return
     */
    public static String toString(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base58.encode(bb.array());
    }
    
    /**
     * 生成一个不超过22字节长度的UUID字符串
     * @return
     */
    public static String randomUUID() {
        return toString(UUID.randomUUID());
    }
}
