package com.septech.centauri.domain.models;

import androidx.annotation.NonNull;

public class Session extends GenericModel {
    @Override
    public void initTestData() {

    }

    @NonNull
    @Override
    public String toString() {
        return "Session " + super.toString();
    }
}
