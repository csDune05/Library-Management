package com.example.librabry_management;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CreateId {
    private static final Random random = new Random();
    public static final ArrayList<String> existingUserIds = new ArrayList<>();
    public static final ArrayList<String> existingDocIds = new ArrayList<>();

    public static String CreateUserId() {
        StringBuilder id;
        do {
            id = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                id.append(random.nextInt(10));
            }
        } while (existingUserIds.contains(id.toString()));
        existingUserIds.add(id.toString());
        return id.toString();
    }

    public static String CreateDocId() {
        StringBuilder id;
        do {
            id = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                id.append(random.nextInt(10));
            }
        } while (existingDocIds.contains(id.toString()));
        existingDocIds.add(id.toString());
        return id.toString();
    }
}
