package com.example.photogalleryapp;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class UITest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withId(R.id.buttonSearchMain)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-09-22 00:00:00"), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(typeText("2020-09-22 23:59:59"), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(typeText("Caption 1"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.editTextCaptionMain)).check(matches(withText("Caption 1")));
        onView(withId(R.id.buttonRight)).perform(click());
        onView(withId(R.id.buttonLeftMain)).perform(click());
    }
}
