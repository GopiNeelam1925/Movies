package com.example.manaadvice.popularmovietmdb.utils;

import android.content.SharedPreferences;

import javax.inject.Inject;

public final class SortHelper {

    private static final String PREF_SORT_BY_KEY = "sortBy";
    private static final String PREF_SORT_BY_DEFAULT_VALUE = "popularity.desc";

    private SharedPreferences sharedPreferences;

    @Inject
    public SortHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getPrefSortValue() {
        return sharedPreferences.getString(
                PREF_SORT_BY_KEY,
                PREF_SORT_BY_DEFAULT_VALUE
        );
    }

    public Sort getSortByPreference() {
        return Sort.fromString(getPrefSortValue());
    }

    public boolean saveSortByPreference(Sort sort) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                PREF_SORT_BY_KEY,
                sort.toString()
        );
        return editor.commit();
    }
}