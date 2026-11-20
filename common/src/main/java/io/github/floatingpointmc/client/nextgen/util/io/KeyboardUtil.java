/*
 * FloatClient-nextgen - A free auxiliary mod for Minecraft Java Edition.
 * Copyright (C) 2026  FloatingPoint-MC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.floatingpointmc.client.nextgen.util.io;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.experimental.UtilityClass;

import java.util.ArrayDeque;

@UtilityClass
public class KeyboardUtil {
    public static final ArrayDeque<InputConstants.Key> KEY_BUFFER = new ArrayDeque<>();
    public static final Object2IntMap<String> NAME_TO_KEY;
    public static final Int2ObjectMap<String> KEY_TO_NAME;

    static {
        Object2IntMap<String> nameToKey = new Object2IntOpenHashMap<>();
        Int2ObjectMap<String> keyToName = new Int2ObjectOpenHashMap<>();
        nameToKey.put("0", 39);
        keyToName.put(39, "0");
        nameToKey.put("1", 30);
        keyToName.put(30, "1");
        nameToKey.put("2", 31);
        keyToName.put(31, "2");
        nameToKey.put("3", 32);
        keyToName.put(32, "3");
        nameToKey.put("4", 33);
        keyToName.put(33, "4");
        nameToKey.put("5", 34);
        keyToName.put(34, "5");
        nameToKey.put("6", 35);
        keyToName.put(35, "6");
        nameToKey.put("7", 36);
        keyToName.put(36, "7");
        nameToKey.put("8", 37);
        keyToName.put(37, "8");
        nameToKey.put("9", 38);
        keyToName.put(38, "9");
        nameToKey.put("A", 4);
        keyToName.put(4, "A");
        nameToKey.put("B", 5);
        keyToName.put(5, "B");
        nameToKey.put("C", 6);
        keyToName.put(6, "C");
        nameToKey.put("D", 7);
        keyToName.put(7, "D");
        nameToKey.put("E", 8);
        keyToName.put(8, "E");
        nameToKey.put("F", 9);
        keyToName.put(9, "F");
        nameToKey.put("G", 10);
        keyToName.put(10, "G");
        nameToKey.put("H", 11);
        keyToName.put(11, "H");
        nameToKey.put("I", 12);
        keyToName.put(12, "I");
        nameToKey.put("J", 13);
        keyToName.put(13, "J");
        nameToKey.put("K", 14);
        keyToName.put(14, "K");
        nameToKey.put("L", 15);
        keyToName.put(15, "L");
        nameToKey.put("M", 16);
        keyToName.put(16, "M");
        nameToKey.put("N", 17);
        keyToName.put(17, "N");
        nameToKey.put("O", 18);
        keyToName.put(18, "O");
        nameToKey.put("P", 19);
        keyToName.put(19, "P");
        nameToKey.put("Q", 20);
        keyToName.put(20, "Q");
        nameToKey.put("R", 21);
        keyToName.put(21, "R");
        nameToKey.put("S", 22);
        keyToName.put(22, "S");
        nameToKey.put("T", 23);
        keyToName.put(23, "T");
        nameToKey.put("U", 24);
        keyToName.put(24, "U");
        nameToKey.put("V", 25);
        keyToName.put(25, "V");
        nameToKey.put("W", 26);
        keyToName.put(26, "W");
        nameToKey.put("X", 27);
        keyToName.put(27, "X");
        nameToKey.put("Y", 28);
        keyToName.put(28, "Y");
        nameToKey.put("Z", 29);
        keyToName.put(29, "Z");
        nameToKey.put("F1", 58);
        keyToName.put(58, "F1");
        nameToKey.put("F2", 59);
        keyToName.put(59, "F2");
        nameToKey.put("F3", 60);
        keyToName.put(60, "F3");
        nameToKey.put("F4", 61);
        keyToName.put(61, "F4");
        nameToKey.put("F5", 62);
        keyToName.put(62, "F5");
        nameToKey.put("F6", 63);
        keyToName.put(63, "F6");
        nameToKey.put("F7", 64);
        keyToName.put(64, "F7");
        nameToKey.put("F8", 65);
        keyToName.put(65, "F8");
        nameToKey.put("F9", 66);
        keyToName.put(66, "F9");
        nameToKey.put("F10", 67);
        keyToName.put(67, "F10");
        nameToKey.put("F11", 68);
        keyToName.put(68, "F11");
        nameToKey.put("F12", 69);
        keyToName.put(69, "F12");
        nameToKey.put("F13", 104);
        keyToName.put(104, "F13");
        nameToKey.put("F14", 105);
        keyToName.put(105, "F14");
        nameToKey.put("F15", 106);
        keyToName.put(106, "F15");
        nameToKey.put("F16", 107);
        keyToName.put(107, "F16");
        nameToKey.put("F17", 108);
        keyToName.put(108, "F17");
        nameToKey.put("F18", 109);
        keyToName.put(109, "F18");
        nameToKey.put("F19", 110);
        keyToName.put(110, "F19");
        nameToKey.put("F20", 111);
        keyToName.put(111, "F20");
        nameToKey.put("F21", 112);
        keyToName.put(112, "F21");
        nameToKey.put("F22", 113);
        keyToName.put(113, "F22");
        nameToKey.put("F23", 114);
        keyToName.put(114, "F23");
        nameToKey.put("F24", 115);
        keyToName.put(115, "F24");
        nameToKey.put("NUMLOCK", 83);
        keyToName.put(83, "NUMLOCK");
        nameToKey.put("NUMPAD0", 98);
        keyToName.put(98, "NUMPAD0");
        nameToKey.put("NUMPAD1", 89);
        keyToName.put(89, "NUMPAD1");
        nameToKey.put("NUMPAD2", 90);
        keyToName.put(90, "NUMPAD2");
        nameToKey.put("NUMPAD3", 91);
        keyToName.put(91, "NUMPAD3");
        nameToKey.put("NUMPAD4", 92);
        keyToName.put(92, "NUMPAD4");
        nameToKey.put("NUMPAD5", 93);
        keyToName.put(93, "NUMPAD5");
        nameToKey.put("NUMPAD6", 94);
        keyToName.put(94, "NUMPAD6");
        nameToKey.put("NUMPAD7", 95);
        keyToName.put(95, "NUMPAD7");
        nameToKey.put("NUMPAD8", 96);
        keyToName.put(96, "NUMPAD8");
        nameToKey.put("NUMPAD9", 97);
        keyToName.put(97, "NUMPAD9");
        nameToKey.put("NUMPADCOMMA", 220);
        keyToName.put(220, "NUMPADCOMMA");
        nameToKey.put("NUMPADENTER", 88);
        keyToName.put(88, "NUMPADENTER");
        nameToKey.put("NUMPADEQUALS", 103);
        keyToName.put(103, "NUMPADEQUALS");
        nameToKey.put("DOWN", 81);
        keyToName.put(81, "DOWN");
        nameToKey.put("LEFT", 80);
        keyToName.put(80, "LEFT");
        nameToKey.put("RIGHT", 79);
        keyToName.put(79, "RIGHT");
        nameToKey.put("UP", 82);
        keyToName.put(82, "UP");
        nameToKey.put("ADD", 87);
        keyToName.put(87, "ADD");
        nameToKey.put("APOSTROPHE", 52);
        keyToName.put(52, "APOSTROPHE");
        nameToKey.put("BACKSLASH", 49);
        keyToName.put(49, "BACKSLASH");
        nameToKey.put("COMMA", 54);
        keyToName.put(54, "COMMA");
        nameToKey.put("EQUALS", 46);
        keyToName.put(46, "EQUALS");
        nameToKey.put("GRAVE", 53);
        keyToName.put(53, "GRAVE");
        nameToKey.put("LBRACKET", 47);
        keyToName.put(47, "LBRACKET");
        nameToKey.put("MINUS", 45);
        keyToName.put(45, "MINUS");
        nameToKey.put("MULTIPLY", 85);
        keyToName.put(85, "MULTIPLY");
        nameToKey.put("PERIOD", 55);
        keyToName.put(55, "PERIOD");
        nameToKey.put("RBRACKET", 48);
        keyToName.put(48, "RBRACKET");
        nameToKey.put("SEMICOLON", 51);
        keyToName.put(51, "SEMICOLON");
        nameToKey.put("SLASH", 56);
        keyToName.put(56, "SLASH");
        nameToKey.put("SPACE", 44);
        keyToName.put(44, "SPACE");
        nameToKey.put("TAB", 43);
        keyToName.put(43, "TAB");
        nameToKey.put("LALT", 226);
        keyToName.put(226, "LALT");
        nameToKey.put("LCONTROL", 224);
        keyToName.put(224, "LCONTROL");
        nameToKey.put("LSHIFT", 225);
        keyToName.put(225, "LSHIFT");
        nameToKey.put("LGUI", 227);
        keyToName.put(227, "LGUI");
        nameToKey.put("RALT", 230);
        keyToName.put(230, "RALT");
        nameToKey.put("RCONTROL", 228);
        keyToName.put(228, "RCONTROL");
        nameToKey.put("RSHIFT", 229);
        keyToName.put(229, "RSHIFT");
        nameToKey.put("RGUI", 231);
        keyToName.put(231, "RGUI");
        nameToKey.put("RETURN", 40);
        keyToName.put(40, "RETURN");
        nameToKey.put("ESCAPE", 41);
        keyToName.put(41, "ESCAPE");
        nameToKey.put("BACKSPACE", 42);
        keyToName.put(42, "BACKSPACE");
        nameToKey.put("DELETE", 76);
        keyToName.put(76, "DELETE");
        nameToKey.put("END", 77);
        keyToName.put(77, "END");
        nameToKey.put("HOME", 74);
        keyToName.put(74, "HOME");
        nameToKey.put("INSERT", 73);
        keyToName.put(73, "INSERT");
        nameToKey.put("PAGEDOWN", 78);
        keyToName.put(78, "PAGEDOWN");
        nameToKey.put("PAGEUP", 75);
        keyToName.put(75, "PAGEUP");
        nameToKey.put("CAPSLOCK", 57);
        keyToName.put(57, "CAPSLOCK");
        nameToKey.put("PAUSE", 72);
        keyToName.put(72, "PAUSE");
        nameToKey.put("SCROLLLOCK", 71);
        keyToName.put(71, "SCROLLLOCK");
        nameToKey.put("PRINTSCREEN", 70);
        keyToName.put(70, "PRINTSCREEN");
        NAME_TO_KEY = Object2IntMaps.unmodifiable(nameToKey);
        KEY_TO_NAME = Int2ObjectMaps.unmodifiable(keyToName);
    }
}
