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
package se.trixon.nbpackager.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.ToggleSwitch;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPane;
import se.trixon.nbpackager.Options;
import static se.trixon.nbpackager_core.Options.DEFAULT_APP_IMAGE_OPTIONS;
import static se.trixon.nbpackager_core.Options.DEFAULT_APP_IMAGE_TOOL;
import static se.trixon.nbpackager_core.Options.DEFAULT_SNAP_OPTIONS;
import static se.trixon.nbpackager_core.Options.OPT_APP_IMAGE_OPTIONS;
import static se.trixon.nbpackager_core.Options.OPT_APP_IMAGE_TOOL;
import static se.trixon.nbpackager_core.Options.OPT_SNAP_OPTIONS;

/**
 *
 * @author Patrik Karlström
 */
public class OptionsPanel extends GridPane {

    private FileChooserPane mAppImageChooserPane;
    private TextField mAppImageTextField;
    private final se.trixon.nbpackager_core.Options mCoreOptions = se.trixon.nbpackager_core.Options.getInstance();
    private final ToggleSwitch mNightModeToggleSwitch = new ToggleSwitch(Dict.NIGHT_MODE.toString());
    private final Options mOptions = Options.getInstance();
    private TextField mSnapcraftTextField;
    private final ToggleSwitch mWordWrapToggleSwitch = new ToggleSwitch(Dict.DYNAMIC_WORD_WRAP.toString());

    public OptionsPanel() {
        createUI();

        mAppImageChooserPane.setPath(mCoreOptions.get(OPT_APP_IMAGE_TOOL, DEFAULT_APP_IMAGE_TOOL));
        mAppImageTextField.setText(mCoreOptions.get(OPT_APP_IMAGE_OPTIONS, DEFAULT_APP_IMAGE_OPTIONS));
        mSnapcraftTextField.setText(mCoreOptions.get(OPT_SNAP_OPTIONS, DEFAULT_SNAP_OPTIONS));
    }

    public void save() {
        mCoreOptions.put(OPT_APP_IMAGE_TOOL, mAppImageChooserPane.getPathAsString());
        mCoreOptions.put(OPT_APP_IMAGE_OPTIONS, mAppImageTextField.getText());
        mCoreOptions.put(OPT_SNAP_OPTIONS, mSnapcraftTextField.getText());
    }

    private void createUI() {
        setHgap(32);
        setVgap(2);
        //setGridLinesVisible(true);
        FxHelper.autoSizeColumn(this, 1);
        mAppImageChooserPane = new FileChooserPane(Dict.SELECT.toString(), "AppImageTool", FileChooserPane.ObjectMode.FILE, SelectionMode.SINGLE);
        mAppImageTextField = new TextField();
        mSnapcraftTextField = new TextField();

        var appImageLabel = new Label("AppImageTool options");
        var snapcraftLabel = new Label("Snapcraft options");
        int row = 0;

        add(mAppImageChooserPane, 0, row++, 1, 1);
        add(appImageLabel, 0, row++, 1, 1);
        add(mAppImageTextField, 0, row++, 1, 1);
        add(snapcraftLabel, 0, row++, 1, 1);
        add(mSnapcraftTextField, 0, row++, 1, 1);
        add(mWordWrapToggleSwitch, 0, row++, 1, 1);
        add(mNightModeToggleSwitch, 0, row++, 1, 1);

        FxHelper.setPadding(new Insets(8, 0, 0, 0),
                appImageLabel,
                snapcraftLabel,
                mNightModeToggleSwitch
        );
        FxHelper.setPadding(new Insets(18, 0, 0, 0),
                mWordWrapToggleSwitch
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
