package me.xfl03.morecrashinfo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintHelper {
    public static List<String> createLine(String... data) {
        return Arrays.asList(data);
    }

    public static List<String> printLines(List<List<String>> datas) {
        //Check data
        if (datas == null || datas.isEmpty()) {
            return new ArrayList<>();
        }

        //Calculate width
        int columns = datas.get(0).size();
        int[] width = new int[columns];
        for (int i = 0; i < columns; i++) {
            for (List<String> it : datas) {
                width[i] = Math.max(it.get(i).length(), width[i]);
            }
        }

        //Add title line
        List<String> line = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < width[i]; j++) {
                sb.append('-');
            }
            line.add(sb.toString());
        }
        datas.add(1, line);

        //Generate text
        List<String> s = new ArrayList<>();
        for (List<String> data : datas) {
            s.add(genString(data, columns, width));
        }
        return s;
    }

    public static String printLine(String suffix, List<List<String>> datas) {
        StringBuilder sb = new StringBuilder();
        PrintHelper.printLines(datas).forEach(it -> sb.append(suffix).append(it));
        return sb.toString();
    }

    public static void appendString(StringBuilder sb, String s, int w) {
        sb.append(s);
        for (int i = 0; i < w - s.length(); i++) {
            sb.append(' ');
        }
    }

    public static String genString(List<String> data, int columns, int[] width) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        for (int i = 0; i < columns; i++) {
            appendString(sb, data.get(i), width[i]);
            sb.append(" | ");
        }
        return sb.toString();
    }
}
