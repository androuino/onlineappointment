package com.intellisrc.josapedmoreno.types

import java.lang.IllegalArgumentException

/**
 * List of available colors
 * @since 2020/01/15.
 */
enum class CustomColor {
    NONE, RED, BLUE, GREEN, YELLOW, ORANGE, PURPLE, PINK, BROWN, WHITE, BLACK;

    /**
     * Returns Color as String
     * @return lowercase String
     */
    override fun toString(): String {
        return name.toLowerCase()
    }

    companion object {
        /**
         * Gets Color from String.
         * @param color : String of color
         * @return : Color object
         */
        fun fromString(color: String): CustomColor {
            return try {
                if (color.isEmpty()) NONE else valueOf(color.trim { it <= ' ' }
                    .toUpperCase())
            } catch (ignored: IllegalArgumentException) {
                NONE
            }
        }
    }
}