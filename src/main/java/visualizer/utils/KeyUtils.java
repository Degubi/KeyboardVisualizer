package visualizer.utils;

public final class KeyUtils {

    public static String getKeyboardKeyFromCode(int keyCode) {
        return switch(keyCode) {
            case 48 -> "0"; case 49 -> "1"; case 50 -> "2"; case 51 -> "3"; case 52 -> "4";
            case 53 -> "5"; case 54 -> "6"; case 55 -> "7"; case 56 -> "8"; case 57 -> "9";

            case 65 -> "A"; case 66 -> "B"; case 67 -> "C"; case 68 -> "D"; case 69 -> "E";
            case 70 -> "F"; case 71 -> "G"; case 72 -> "H"; case 73 -> "I"; case 74 -> "J";
            case 75 -> "K"; case 76 -> "L"; case 77 -> "M"; case 78 -> "N"; case 79 -> "O";
            case 80 -> "P"; case 81 -> "Q"; case 82 -> "R"; case 83 -> "S"; case 84 -> "T";
            case 85 -> "U"; case 86 -> "V"; case 87 -> "W"; case 88 -> "X"; case 89 -> "Y";
            case 90 -> "Z";

            case 192 -> "Ö"; case 191 -> "Ü"; case 187 -> "Ó"; case 219 -> "Ő";
            case 221 -> "Ú"; case 186 -> "É"; case 222 -> "Á"; case 220 -> "Ű";
            case 188 -> ","; case 190 -> "."; case 189 -> "-"; case 226 -> "Í";

            case 9  -> "TAB";   case 16 -> "SHIFT";     case 17 -> "CTRL";  case 18 -> "ALT";
            case 32 -> "SPACE"; case 8  -> "BACKSPACE"; case 10 -> "ENTER"; case 91 -> "WIN";

            case 38 -> "UP"; case 40 -> "DOWN"; case 37 -> "LEFT"; case 39 -> "RIGHT";

            case 45 -> "INSERT"; case 36 -> "HOME"; case 33 -> "PG UP";
            case 46 -> "DELETE"; case 35 -> "END";  case 34 -> "PG DOWN";

            case 112 -> "F1"; case 113 -> "F2";  case 114 -> "F3";  case 115 -> "F4";
            case 116 -> "F5"; case 117 -> "F6";  case 118 -> "F7";  case 119 -> "F8";
            case 120 -> "F9"; case 121 -> "F10"; case 122 -> "F11"; case 123 -> "F12";

            case 111 -> "NP_/"; case 106 -> "NP_*";  case 109 -> "NP_-";
            case 103 -> "NP_7"; case 104 -> "NP_8";  case 105 -> "NP_9";
            case 100 -> "NP_4"; case 101 -> "NP_5";  case 102 -> "NP_6";
            case 97  -> "NP_1"; case 98  -> "NP_2";  case 99  -> "NP_3";
            case 96  -> "NP_0"; case 110 -> "NP_.";  case 107 -> "NP_+";

            case 93 -> "MENU";
            case 0  -> "UNDEFINED";
            default -> null;
        };
    }

    public static int getKeyColorIndex(int keyCode) {
        return switch(keyCode) {
            case 48 -> 0;  case 49 -> 1;  case 50 -> 2;  case 51 -> 3;  case 52 -> 4;  case 53 -> 5;  case 54 -> 6;  case 55 -> 7;  case 56 -> 8;  case 57 -> 9;
            case 81 -> 10; case 87 -> 11; case 69 -> 12; case 82 -> 13; case 84 -> 14; case 90 -> 15; case 85 -> 16; case 73 -> 17; case 79 -> 18; case 80 -> 19;
            case 65 -> 20; case 83 -> 21; case 68 -> 22; case 70 -> 23; case 71 -> 24; case 72 -> 25; case 74 -> 26; case 75 -> 27; case 76 -> 28;
            case 89 -> 29; case 88 -> 30; case 67 -> 31; case 86 -> 32; case 66 -> 33; case 78 -> 34; case 77 -> 35;
            default -> -1;
        };
    }

    private KeyUtils() {}
}