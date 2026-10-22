package com.ThrishenGovender.smartpantry.database;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Converters {

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(value.split(","));
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }
}