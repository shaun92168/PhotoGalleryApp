package com.example.photogalleryapp;

import androidx.test.rule.ActivityTestRule;

import com.example.photogalleryapp.Views.GalleryActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class UITest {
    @Rule
    public ActivityTestRule<GalleryActivity> activityRule =
            new ActivityTestRule<>(GalleryActivity.class);
/*
    @Test
    public void listGoesOverTheFold() {
        onView(withId(R.id.btSearchMain)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-09-23 19:50:00"), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(typeText("2020-10-10 19:57:00"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.btRight)).perform(click());
        onView(withId(R.id.btLeftMain)).perform(click());
    }
 */
}
