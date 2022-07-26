/* 
 * Copyright 2022 Patrik Karlström.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.nbpackager;

import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import se.trixon.almond.util.OptionsBase;

/**
 *
 * @author Patrik Karlström
 */
public class Options extends OptionsBase {

    private static final boolean DEFAULT_UI_NIGHTMODE = true;
    private static final boolean DEFAULT_UI_WORD_WRAP = true;
    private static final String KEY_UI_NIGHTMODE = "ui.nightmode";
    private static final String KEY_UI_WORDWRAP = "ui.wordwrap";
    private final BooleanProperty mNightModeProperty = new SimpleBooleanProperty();
    private final BooleanProperty mWordWrapProperty = new SimpleBooleanProperty();

    public static Options getInstance() {
        return Holder.INSTANCE;
    }

    private Options() {
        setPreferences(Preferences.userNodeForPackage(AppStart.class));

        mNightModeProperty.set(is(KEY_UI_NIGHTMODE, DEFAULT_UI_NIGHTMODE));
        mWordWrapProperty.set(is(KEY_UI_WORDWRAP, DEFAULT_UI_WORD_WRAP));

        initListeners();
    }

    public boolean isNightMode() {
        return mNightModeProperty.get();
    }

    public boolean isWordWrap() {
        return mWordWrapProperty.get();
    }

    public BooleanProperty nightModeProperty() {
        return mNightModeProperty;
    }

    public void setNightMode(boolean nightMode) {
        mNightModeProperty.set(nightMode);
    }

    public void setWordWrap(boolean value) {
        mWordWrapProperty.set(value);
    }

    public BooleanProperty wordWrapProperty() {
        return mWordWrapProperty;
    }

    private void initListeners() {
        ChangeListener<Object> changeListener = (observable, oldValue, newValue) -> {
            save();
        };

        mNightModeProperty.addListener(changeListener);
        mWordWrapProperty.addListener(changeListener);
    }

    private void save() {
        put(KEY_UI_NIGHTMODE, isNightMode());
        put(KEY_UI_WORDWRAP, isWordWrap());
    }

    private static class Holder {

        private static final Options INSTANCE = new Options();
    }
}
