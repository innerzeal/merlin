/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.ivory.entity.v0;

import java.util.TimeZone;

public class EntityHelper {
    public static String getTimeZoneId(TimeZone tz) {
        return tz.getID();
    }
}
