package nl.halteradar.util;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import nl.bisonnl.netex.Line;
import nl.bisonnl.netex.LineString;
import nl.bisonnl.netex.MultilingualString;
import nl.bisonnl.netex.PresentationStructure;
import nl.bisonnl.netex.PrivateCodes;
import nl.bisonnl.netex.VersionOfObjectRefStructure;
import nl.bisonnl.netex.VersionOfObjectRefStructureWithClass;
import nl.bisonnl.netex.VersionOfObjectRefStructureWithOptionalClass;
import nl.halteradar.Table;

public abstract class CommonTabler<T> implements BiFunction<T, String, Stream<Table>> {
    static protected final String Delimiter = ";";

    protected static String binBool(Boolean b) {
        if (b == null)
            return null;
        return b ? "1" : "0";
    }

    protected static String text(MultilingualString multilingualString) {
        if (multilingualString == null)
            return null;

        return multilingualString.getValue();
    }

    protected static String privateCode(PrivateCodes protectedCodes, String typ) {
        if (protectedCodes == null)
            return null;

        var result = new StringBuilder();

        for (var code : protectedCodes.getPrivateCode()) {
            if (code.getType().equals(typ)) {
                if (result.length() > 0)
                    result.append(Delimiter);
                result.append(code.getValue());
            }
        }
        return result.toString();
    }

    protected static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    protected static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    protected static String[] presentationColor(PresentationStructure pres) {
        if (pres == null)
            return new String[] { "", "" };

        String color = "";
        String textColor = "";

        if (pres.getColour() != null) {
            color = "#" + encodeHexString(pres.getColour());
        }

        if (pres.getTextColour() != null) {
            textColor = "#" + encodeHexString(pres.getTextColour());
        }

        return new String[] { color, textColor };
    }

    protected static String transportMode(Line line) {
        var mode = line.getTransportMode();
        if (mode == null)
            return "";

        var submode = line.getTransportSubmode();
        if (submode == null)
            return mode.value();

        return switch (mode) {
            case BUS -> submode.getBusSubmode() != null
                    ? mode.value() + ":" + submode.getBusSubmode().value()
                    : mode.value();
            case METRO -> submode.getMetroSubmode() != null
                    ? mode.value() + ":" + submode.getMetroSubmode().value()
                    : mode.value();
            case TRAM -> submode.getTramSubmode() != null
                    ? mode.value() + ":" + submode.getTramSubmode().value()
                    : mode.value();
            case RAIL -> submode.getRailSubmode() != null
                    ? mode.value() + ":" + submode.getRailSubmode().value()
                    : mode.value();
            case WATER -> submode.getWaterSubmode() != null
                    ? mode.value() + ":" + submode.getWaterSubmode().value()
                    : mode.value();
            case ALL -> mode.value();
            case UNKNOWN -> mode.value();
        };
    }

    protected static String str(Object v) {
        return str(v, () -> "");
    }

    protected static String str(Object v, Supplier<String> def) {
        return v == null ? def.get() : v.toString();
    }

    protected static String val(Enum<?> v) {
        if (v == null)
            return null;
        return v.name();
    }

    protected static String toString(Object d) {
        return d == null ? "" : d.toString();
    }

    protected static String refString(VersionOfObjectRefStructure ref) {
        return ref == null ? "" : ref.getRef();
    }

    protected static String refStringClass(VersionOfObjectRefStructureWithClass ref) {
        return ref == null ? "" : ref.getRef();
    }

    protected static String refStringOptClass(VersionOfObjectRefStructureWithOptionalClass ref) {
        return ref == null ? "" : ref.getRef();
    }

    protected static String pos(Object location) {
        if (location == null)
            return "";

        /*
         * Gok: Location.getPos().getValue()
         */
        var p = ((nl.bisonnl.netex.LocationStructure) location).getPos();
        if (p == null || p.getValue() == null)
            return "";

        return p.getValue();
    }

    protected static String pointWkt(Object location) {
        var p = pos(location);
        return p.isEmpty() ? "" : "POINT(" + p + ")";
    }

    protected static String linestringWkt(LineString ls) {
        if (ls == null)
            return "";

        if (ls.getPosList() == null || ls.getPosList().getValue() == null)
            return "";

        var fields = ls.getPosList().getValue().trim().split("\\s+");
        var out = new StringBuilder("LINESTRING(");

        for (int i = 0; i + 1 < fields.length; i += 2) {
            if (i > 0)
                out.append(",");
            out.append(fields[i]).append(" ").append(fields[i + 1]);
        }

        return out.append(")").toString();
    }

    protected static <T> Stream<String> toStringStream(List<T> elems, Function<T, String> mapper) {
        return elems.stream().map(mapper);
    }

    protected static <T> Stream<String> toStringStream(List<T> elems) {
        return toStringStream(elems, Object::toString);
    }

    protected static <T> String optionalString(T value, Function<T, String> mapper) {
        if (value == null)
            return null;
        return mapper.apply(value);
    }

    protected static <T> String optionalString(T value) {
        return optionalString(value, Object::toString);
    }
}
