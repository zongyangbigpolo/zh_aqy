package com.ruoyi.common.utils.uuid;

/**
 * @Author：MXJ
 * @Date：2024/10/11 16:31
 */
public class UniqueIntGenerator {
    public static int generateUniqueInt(){
        UUID uuid = UUID.randomUUID();
        long mostSignificant = uuid.getMostSignificantBits();
        long leastSignificant = uuid.getLeastSignificantBits();
        int uniqueInt = (int)(mostSignificant ^ leastSignificant);
        return uniqueInt;
    }
}
