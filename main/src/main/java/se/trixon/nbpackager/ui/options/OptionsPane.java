/*
 * Copyright 2024 Patrik Karlström <patrik@trixon.se>.
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
package se.trixon.nbpackager.ui.options;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javax.swing.JFileChooser;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPaneSwingFx;
import se.trixon.nbpackager.Options;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
class OptionsPane extends GridPane {

    private final FileChooserPaneSwingFx mAppImageChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), "AppImageTool", Almond.getFrame(), JFileChooser.FILES_ONLY);
    private final TextField mAppImageTextField = new TextField();
    private final Options mOptions = Options.getInstance();
    private final TextField mSnapcraftTextField = new TextField();

    public OptionsPane() {
        createUI();
        load();
    }

    private void load() {
        mAppImageChooserPane.setPath(mOptions.get(Options.OPT_APP_IMAGE_TOOL, Options.DEFAULT_APP_IMAGE_TOOL));
        mAppImageTextField.setText(mOptions.get(Options.OPT_APP_IMAGE_OPTIONS, Options.DEFAULT_APP_IMAGE_OPTIONS));
        mSnapcraftTextField.setText(mOptions.get(Options.OPT_SNAP_OPTIONS, Options.DEFAULT_SNAP_OPTIONS));
    }

    public void save() {
        mOptions.put(Options.OPT_APP_IMAGE_TOOL, mAppImageChooserPane.getPathAsString());
        mOptions.put(Options.OPT_APP_IMAGE_OPTIONS, mAppImageTextField.getText());
        mOptions.put(Options.OPT_SNAP_OPTIONS, mSnapcraftTextField.getText());
    }

    private void createUI() {
        setHgap(32);
        setVgap(2);
        //setGridLinesVisible(true);
        FxHelper.autoSizeColumn(this, 1);
        var appImageLabel = new Label("AppImageTool options");
        var snapcraftLabel = new Label("Snapcraft options");
        int row = 0;
        add(mAppImageChooserPane, 0, row++, 1, 1);
        add(appImageLabel, 0, row++, 1, 1);
        add(mAppImageTextField, 0, row++, 1, 1);
        add(snapcraftLabel, 0, row++, 1, 1);
        add(mSnapcraftTextField, 0, row++, 1, 1);
        FxHelper.setPadding(new Insets(8, 0, 0, 0), appImageLabel, snapcraftLabel);
        for (var columnConstraint : getColumnConstraints()) {
            columnConstraint.setFillWidth(true);
            columnConstraint.setHgrow(Priority.ALWAYS);
        }
    }

}
