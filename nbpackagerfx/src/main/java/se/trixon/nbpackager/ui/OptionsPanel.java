/*
 * Copyright 2021 Patrik Karlström.
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
package se.trixon.nbpackager.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.ToggleSwitch;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.nbpackager.Options;

/**
 *
 * @author Patrik Karlström
 */
public class OptionsPanel extends GridPane {

    private final ToggleSwitch mNightModeToggleSwitch = new ToggleSwitch(Dict.NIGHT_MODE.toString());
    private final Options mOptions = Options.getInstance();
    private final ToggleSwitch mWordWrapToggleSwitch = new ToggleSwitch(Dict.DYNAMIC_WORD_WRAP.toString());

    public OptionsPanel() {
        createUI();
    }

    private void createUI() {
        setHgap(32);
        setVgap(2);
        //setGridLinesVisible(true);
        FxHelper.autoSizeColumn(this, 1);

        int row = 0;
        add(mWordWrapToggleSwitch, 0, row++, 1, 1);
        add(mNightModeToggleSwitch, 0, row++, 1, 1);

        FxHelper.setPadding(new Insets(28, 0, 0, 0),
                mNightModeToggleSwitch
        );

        for (var columnConstraint : getColumnConstraints()) {
            columnConstraint.setFillWidth(true);
            columnConstraint.setHgrow(Priority.ALWAYS);
        }

        mWordWrapToggleSwitch.setMaxWidth(Double.MAX_VALUE);
        mNightModeToggleSwitch.setMaxWidth(Double.MAX_VALUE);

        mWordWrapToggleSwitch.selectedProperty().bindBidirectional(mOptions.wordWrapProperty());
        mNightModeToggleSwitch.selectedProperty().bindBidirectional(mOptions.nightModeProperty());
    }

}
